/* 	Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
Copyright (C) 2017  David Schlossarczyk

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

For the full license visit https://www.gnu.org/licenses/gpl-3.0. */

using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Net;
using System.IO;
using System.Xml;
using ServerManager.util;
using System.Threading.Tasks;
using ServerManager.data;

namespace ServerManager.util
{
    class HttpManager
    {
        private string url;
        private string user;
        private string pass;
        private string webrequeststatuscode;

        static private string getdbversion = "getDBVersion.php";
        static private string dbManagment = "dbManagment.php";

        public string Pass { get => pass; }

        public delegate void httpCaller(int objectId, string response);
        httpCaller caller;
        
        public delegate void UICallback(short method, string response, int objectId);
        UICallback uiCaller;

        /// <summary>
        /// Instantziert einen neuen HttpManager
        /// </summary>
        /// <param name="url">URL unter der der Server zu finden ist</param>
        /// <param name="caller">Callback Methode zur Ausgabe von Fortschrittsnachrichten</param>
        /// <param name="uiCallback">Callback Methode zum Übergeben von Rückgabewerten aus asynchronen HTTP-Abfragen</param>
        public HttpManager(string url, httpCaller caller, UICallback uiCallback)
        {

            if (url.Substring(0, 7) != "http://")
            {
                this.url = "http://" + url;
            }
            else
            {
                this.url = url;
            }

            this.caller = caller;
            uiCaller = uiCallback;

        }

        /// <summary>
        /// Instantziert einen neuen HttpManager
        /// </summary>
        /// <param name="url">URL unter der der Server zu finden ist</param>
        /// <param name="user">Benutzername für den Serverzugriff</param>
        /// <param name="pass">Benutzerpasswort für den Serverzugriff</param>
        /// <param name="caller">Callback Methode zur Ausgabe von Fortschrittsnachrichten</param>
        /// <param name="uiCallback">Callback Methode zum Übergeben von Rückgabewerten aus asynchronen HTTP-Abfragen</param>
        public HttpManager(string url, string user, string pass, httpCaller caller, UICallback uiCallback)
        {

            if (url.Substring(0, 7) != "http://")
            {
                this.url = "http://" + url;
            }
            else
            {
                this.url = url;
            }

            this.caller = caller;
            uiCaller = uiCallback;
            SetAuth(user,pass);

        }

        //===============================================================================
        //==============================Grundfunktionen==================================
        //===============================================================================

        /// <summary>
        /// Hilfsmethode um die Authentifizierungsdaten auch nachträglich schnell setzen zu können
        /// </summary>
        /// <param name="user"></param>
        /// <param name="pass"></param>
        public void SetAuth(string user, string pass)
        {
            this.user = user;
            this.pass = CreateHash(pass);
        }

        /// <summary>
        /// Stellt eine Verbindung zum Datenbankserver her
        /// </summary>
        /// <param name="serverURL">Die URL unter der der Server zu finden ist.</param>
        /// <param name="post">Wenn keine Postparameter verwendet werden sollen, kann als Wert "" oder null übergeben werden</param>
        /// <returns>Den Responsestream des Servers oder eine Fehlermeldung</returns>
        private string Connect(string serverURL, string post, IProgress<string> reporter, int uploadObjectID)
        {
            HttpWebRequest webRequest = (HttpWebRequest) WebRequest.Create(serverURL);
            HttpWebResponse response;

            try
            {
                if (post == null || post == "")
                {
                    response = (HttpWebResponse)webRequest.GetResponse();
                }
                else
                {
                    //Postdatan sind vorhanden, diese jetzt anhängen
                    webRequest = attachPost(webRequest, post);
                    response = (HttpWebResponse)webRequest.GetResponse();
                }
            }
            catch (WebException e)
            {
                webrequeststatuscode = e.Status.ToString();

                if  (e.Status == WebExceptionStatus.ConnectFailure ||
                    e.Status == WebExceptionStatus.ConnectionClosed || 
                    e.Status == WebExceptionStatus.RequestCanceled)
                {
                    return "INTERNAL_ERROR_WEB_NO_CONNECTION";
                }      
                else
                {
                    return "INTERNAL_ERROR_WEB_WEBREQUEST";
                }
            }

            if (response.StatusCode != HttpStatusCode.OK && response.StatusCode != HttpStatusCode.Accepted)
            {
                return "HTTP_ERROR-" + ((int)response.StatusCode).ToString();
            }

            Stream responseStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(responseStream);

            StringBuilder builder = new StringBuilder();

            while (!reader.EndOfStream)
            {
                string line = reader.ReadLine();

                if (line.Contains("ID "))
                { //Aktiv, wenn eine Datenübertragung stattfindet.
                    reporter.Report(uploadObjectID.ToString() + "#" + line);
                }
                builder.Append(line);
            }

            string text = builder.ToString();
            response.Close();
            return text;
        }

        /// <summary>
        /// Hängt an eine HTTPWebRequest POST-Daten an
        /// </summary>
        /// <param name="webRequest">Zu bearbeitende HTTPWebRequest.</param>
        /// <param name="post">Die anzuhängenden Daten.</param>
        /// <returns>Die bearbeitete HTTPWebRequest.</returns>
        private HttpWebRequest attachPost(HttpWebRequest webRequest, string post)
        {
            //post hat die Form feld1=ABCD&feld2=EFGH
            var data = Encoding.UTF8.GetBytes(post);

            webRequest.Method = "POST";
            webRequest.ContentType = "application/x-www-form-urlencoded";
            webRequest.ContentLength = data.Length;

            Stream requestStream = webRequest.GetRequestStream();   
            requestStream.Write(data, 0, data.Length);

            return webRequest;
        }

        

        //===============================================================================
        //============================Verbindungsfunktionen==============================
        //===============================================================================

        /// <summary>
        /// Ruft die auf dem Server vorhandene Datenbankversion ab.
        /// </summary>
        /// <returns>Serverversion</returns>
        public void GetDBVersion()
        {
            string url = MergeURL(getdbversion);

            ConnectAsync(1, -1, url, null);

        }

        /// <summary>
        /// Übermittelt mittels POST Datenbankeinträge an den Server
        /// </summary>
        /// <param name="data">Die zu übertragenden Datenbankeinträge im JSON Format</param>
        /// <returns>Abschlussmeldung</returns>
        public void PushData(UploadObject upload)
        {
            string query = dbManagment;
            string url = MergeURL(query);
            StringBuilder post = new StringBuilder();

            post.Append("user=" + user + "&");
            post.Append("pass=" + Pass + "&");
            //post.Append("group=" + group + "&");
            post.Append("command=" + upload.Command + "&");
            post.Append("table=" + upload.Table + "&");
            post.Append("data=");

            ConnectAsync(0,upload.ID,url,post.ToString() + upload.Data);

        }

        private async void ConnectAsync(short method, int uploadObjectID, string url, string post)
        {
            var progressreporter = new Progress<string>(ReportProgress);

            Task<string> connectTask;
            connectTask = new Task<string>(() => { return Connect(url, post, progressreporter, uploadObjectID); });
            connectTask.Start();

            string response = await connectTask;
            string scannedResponse = ComputeResponse(response);

            if (scannedResponse.Contains("Konnte keine gültige Zuordnung finden!") || scannedResponse == "NO_ERROR")
            {
                uiCaller(method, response, uploadObjectID);
            }
            else
            {
                caller(-1,scannedResponse);
            }
        }

        //===============================================================================
        //==============================Testfunktionen===================================
        //===============================================================================

        ///// <summary>
        ///// Testfunktion für die Verbindungsfunktionalität
        ///// </summary>
        ///// <returns></returns>
        //public string testConnection()
        //{
        //    string url = MergeURL(getdbversion);
        //    return Connect(url, null);
        //}

        ///// <summary>
        ///// Testfunktion für die Authentifizierung
        ///// </summary>
        ///// <returns></returns>
        //public string testUserAndPass()
        //{
        //    string query = dbManagment;
        //    string post = "user=" + user + "&" + "pass=" + Pass + "&" + "group=B1&command=insert&data=null";

        //    string url = MergeURL(query);
        //    return ComputeResponse(Connect(url, post));
        //}

        //===============================================================================
        //==============================Hilfsfunktionen==================================
        //===============================================================================

        /// <summary>
        /// Verbindet die gespeicherte URL mit dem eingegebenen Query. Wird verwendet um die verschiedenen 
        /// Skriptenamen und GET-Anfragen an die URL anzuhängen.
        /// </summary>
        /// <param name="query"></param>
        /// <returns></returns>
        private string MergeURL(string query)
        {
            string result = this.url;
            if (result.Substring(url.Length - 1, 1) == "/")
            {
                result = result + query;
            }
            else
            {
                result = result + "/" + query;
            }

            return result;
        }

        /// <summary>
        /// Erzeugt eine zur Eingabe passenden SHA256 Hash. Ist im ServerManager vor allem für das Passwort gedacht
        /// </summary>
        /// <param name="raw">Zu hashender String</param>
        /// <returns>Gehashter Wert</returns>
        public string CreateHash(string raw)
        {
            SHA256 hash;
            hash = SHA256.Create();
            byte[] hashResult;
            hashResult = hash.ComputeHash(Encoding.UTF8.GetBytes(raw));

            StringBuilder stringBuilder = new StringBuilder();

            foreach (byte element in hashResult)
            {
                stringBuilder.Append(element.ToString());
            }

            return stringBuilder.ToString();
        }

        /// <summary>
        /// Verarbeitet Ausgaben des Servers und prüft sie auf Fehlermeldungen. Falls eine Fehlermeldung gefunden wird, wird aus einer
        /// mitgelieferten XML-Datei die passende Fehlermeldung ausgegeben
        /// </summary>
        /// <param name="response">Antwort des Servers</param>
        /// <returns>Freigabe oder Fehlermeldung</returns>
        public string ComputeResponse(string response)
        {
            //Wenn die Eingabe ausschließlich numerisch ist, wird es sich wahrscheinlich um die Antwort auf die getDBVersion Anfragen handlen
            //Die kann direkt durchgewunken werden
            if (Helper.isNumeric(response))
            {
                return "NO_ERROR";
            }
            else if (response.Contains("HTTP_ERROR"))
            {
                return "HTTP-Fehler festgestellt! Fehlercode: " + response.Split('-')[1];
            }
            else if (response.Contains("DOCTYPE HTML PUBLIC")) {
                //Es ist davon auszugehen, dass eine falsche Seite aufgerufen wurde, da vom Server keine HTML-Dokumente zurückgeliefert werden
                response = "INTERNAL_ERROR_WEB_HTML_DETECTED";
            }

            if (File.Exists("settings.xml"))
            {
                XmlDocument document = new XmlDocument();
                document.Load("environment.xml");
                XmlNode rootnode = document.GetElementsByTagName("messages")[0];

                try
                {
                    XmlNode node = rootnode.SelectNodes("//message[@name='" + response + "']")[0];

                    //Für die internen Verbindungsfehler werden noch die ErrorCodes des Fehlers angehängt.
                    if (response.Contains("ERROR_WEB"))
                    {
                        return node.InnerText + webrequeststatuscode;
                    }
                    else
                    {
                        return node.InnerText;
                    }
                }
                catch (Exception)
                {
                    return "Konnte keine gültige Zuordnung finden! Servernachricht: " + response;
                }
            }
            else
            {
                return "Konnte Nachrichtendatei nicht laden oder eine passende Meldung finden! Servernachricht: " + response;
            }
        }

        /// <summary>
        /// Diese Methode schickt Fortschrittsnachrichten an den Nutzer ohne dabei die Threadbegrenzungen zu durchbrechen
        /// </summary>
        private void ReportProgress(string message)
        {
                string[] array = message.Split('#');
                caller(Int16.Parse(array[0]),array[1]);
        }
    }
}
