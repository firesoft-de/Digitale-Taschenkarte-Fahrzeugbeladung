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

namespace ServerManager.util
{
    class httpManager
    {
        private string url;
        private string user;
        private string pass;
        WebRequest webRequest;

        static private string getdbversion = "getDBVersion.php";
        static private string dbManagment = "dbManagment.php";

        public string Pass { get => pass;}

        public httpManager(string url)
        {

            if (url.Substring(0,7) != "http://")
            {
                this.url = "http://" + url;
            }
            else
            {
                this.url = url;
            }

        }

        //===============================================================================
        //==============================Grundfunktionen==================================
        //===============================================================================

        public void setAuth(string user, string pass)
        {
            this.user = user;
            this.pass = hashPasswort(pass);
        }

        public void send()
        {

        }
        
        private string connect(string serverURL)
        {
            webRequest = WebRequest.Create(serverURL);

            WebResponse response = webRequest.GetResponse();
            Stream responseStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(responseStream);

            StringBuilder builder = new StringBuilder();

            while (!reader.EndOfStream)
            {
                builder.Append(reader.ReadLine());
            }

            string text = builder.ToString();
            response.Close();
            return text;
        }

        //post hat die Form feld1=ABCD&feld2=EFGH
        private string connect(string serverURL, string post)
        {
            webRequest = (HttpWebRequest) WebRequest.Create(serverURL);
            
            var data = Encoding.ASCII.GetBytes(post);

            webRequest.Method = "POST";
            webRequest.ContentType = "application/x-www-form-urlencoded";
            webRequest.ContentLength = data.Length;

            Stream requestStream = webRequest.GetRequestStream();
            requestStream.Write(data, 0, data.Length);

            WebResponse response = webRequest.GetResponse();
            Stream responseStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(responseStream);

            StringBuilder builder = new StringBuilder();

            while (!reader.EndOfStream)
            {
                builder.Append(reader.ReadLine());
            }

            string text = builder.ToString();
            response.Close();
            return text;
        }

        //===============================================================================
        //============================Verbindungsfunktionen==============================
        //===============================================================================

        public string getDBVersion()
        {
            string url = mergeURL(getdbversion);
            return connect(url);
        }


        public string pushData()
        {
            return "";
        }

        //===============================================================================
        //==============================Testfunktionen===================================
        //===============================================================================

        public string testConnection()
        {
            string url = mergeURL(getdbversion);
            return connect(url);
        }

        public string testUserAndPass()
        {
            string query = dbManagment;
            string post = "user=" + user + "&" + "pass=" + Pass + "&" + "group=B1&command=insert&data=null";

            string url = mergeURL(query);
            return computeResponse(connect(url, post));
        }

        //===============================================================================
        //==============================Hilfsfunktionen==================================
        //===============================================================================

        private string mergeURL(string query)
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

        private string hashPasswort(string pass)
        {
            SHA256 hash;
            hash = SHA256.Create();
            byte[] hashResult;
            hashResult = hash.ComputeHash(Encoding.UTF8.GetBytes(pass));

            StringBuilder stringBuilder = new StringBuilder();

            foreach (byte element in hashResult)
            {
                stringBuilder.Append(element.ToString());
            }

            return stringBuilder.ToString();
        }

        public string computeResponse(string response)
        {
            if (File.Exists("settings.xml"))
            {
                XmlReader reader = XmlReader.Create("messages.xml");
                if (!reader.EOF)
                {
                    reader.MoveToContent();

                    while (reader.Read())
                    {
                        if (reader.NodeType == XmlNodeType.Element && reader.GetAttribute("name") == response)
                        {
                            return reader.ReadElementContentAsString();
                        }
                    }
                }
                reader.Close();
            }
            return "Konnte Nachrichtendatei nicht laden oder eine passende Meldung finden! Servernachricht: " + response;
        }
    }
}
