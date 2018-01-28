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
using System.Text;
using Microsoft.Office.Interop.Excel;
using System.Runtime.InteropServices;
using System.Xml;
using System.Threading.Tasks;
using System.ComponentModel;

namespace ServerManager
{
    class ExcelManager
    {
        string path;
        List<List<string>> serversort;

        List<string> data;
        List<string> tablenames;
        int entries;
        int tables;

        //Application application;
        //Workbook book;

        //Eventhandler um den Fortschritt durch die Threads zu schicken
        //public event EventHandler<ExcelEventArgs> ReportProgressEvent;

        //Delegate um Nachrichten aus dieser Klasse über das Ausgabefenster an den Nutzer zu senden
        public delegate void excelcallback(string message);
        excelcallback caller;

        public delegate void excelUICallback(int tableCount, int entryCount, List<string> tablenames);
        excelUICallback uiCaller;

        public int CountEntries { get => entries; set => entries = value; }
        public int CountTables { get => tables; set => tables = value; }

        /// <summary>
        /// Initialisiert den ExcelManager
        /// </summary>
        /// <param name="path">Pfad der Exceldatei</param>
        public ExcelManager(string path, excelcallback callback, excelUICallback uICallback)
        {
            this.path = path;
            caller = callback;
            uiCaller = uICallback;

            serversort = new List<List<string>>();
            data = new List<string>();
            tablenames = new List<string>();
            entries = 0;
            tables = 0;
        }
        
        public void Open()
        {
            SubOpen();
        }

        public async void SubOpen()
        {
            var progressreporter = new Progress<string>(ReportProgress);
            Task<List<string>> loadTask;
            loadTask = new Task<List<string>>(() => { return Load(path, progressreporter); });
            loadTask.Start();
            var res = await loadTask;
            data = res;
            caller("Datenabruf abgeschlossen!");
            uiCaller(tables, entries, tablenames);
        }


        private List<string> Load(string path, IProgress<string> reporter)
        {
            Application application;
            Workbook book;

            //Excelanwendung öffnen
            application = new Application();
            application.Visible = true;
            book = application.Workbooks.Open(path);

            List<string> data = new List<string>();

            //Serversortierung laden
            serversort = loadServerSorting(reporter);

            if (serversort != null)
            {
                foreach (Worksheet sheet in book.Worksheets)
                {
                    tablenames.Add(sheet.Name);
                    reporter.Report("Lade Daten aus Tabelle: " + sheet.Name);
                    data.Add(readSheet(sheet, serversort,reporter));
                    tables += 1;
                }
            }
            else
            {
                reporter.Report("Konnte XML-Daten zur Serversortierung nicht laden!");
            }

            book.Close();
            application.Quit();

            return data;
        }

        private string readSheet(Worksheet sheet, List<List<string>> serversort, IProgress<string> reporter)
        {
            StringBuilder output = new StringBuilder();
            List<string> localsort = new List<string>();

            //Tabellenname als Kennung verwenden. Diese wird an Position 0 der Liste gespeichert
            string name = sheet.Name;

            //Präamble in den Stringbuilder einfügen:
            output.Append("{ \"INPUT\": [");
            
            Range range = sheet.UsedRange;

            //Spalten und Zeilen zählen
            int rows = range.Rows.Count;
            int cols = range.Columns.Count;
            int col_anmerkung = -1;

            // Die erste Zeile enthält die Spaltenbezeichnungen der Exceltabelle.
            // Sie muss als erstes ausgelesen und gespeichert werden. 
            for (int i = 1; i <= cols; i++)
            {
                var res = ((Range)sheet.Cells[1, i]).Value;
                if (res != "Anmerkungen") { //Absicherung gegen die Spalte Anmkerungen
                    localsort.Add(res);
                }
                else
                {
                    col_anmerkung = i;
                }
            }


            int[] finalsort = generateSortingTranslation(sheet.Name, localsort, serversort);
            string[] fieldnames = getServerFieldNames(name, serversort).ToArray();

            // Jetzt werden die einzelnen Zellen ausgelesen
            for (int j = 2; j <= rows; j++) //j=2, da die erste Zeile nicht ausgelesen werden soll
            { //Zeilenweise

                //if (rows / 4 == j)
                //{
                //    reporter.Report("Auslesen bei 25%!");
                //}
                //else if ((rows / 4) * 3 == j)
                //{
                //    reporter.Report("Auslesen bei 75%!");
                //}
                //else if (rows / 2 == j)
                //{
                //    reporter.Report("Auslesen bei 50%!");
                //}
                double tmp = (100D * (Double) j / (Double) rows);
                tmp = Math.Round(tmp, 2);
                reporter.Report("Auslesen bei " + tmp.ToString() + "%");

                    string[]  presort = new string[localsort.Count];
                for (int i = 1; i < cols; i++)
                { //Spaltenweise
                    if (i != col_anmerkung)
                    {
                        string res = Convert.ToString(((Range)sheet.Cells[j, i]).Value);
                        presort[i - 1] = res;
                    }
                }
                // Anhand des finalsort Arrays können nun die aus der Exceltabelle ausgelesenen Daten in die richtige Reihenfolge gebracht werden

                string[] tmpsort = new string[presort.Length];

                for (int i = 0; i < presort.Length; i++)
                {
                    tmpsort[finalsort[i]] = presort[i];
                }

                //Sortiertes Schema in den Stringbuilder eintragen und alles in JSON codieren
                output.Append(ToJson(fieldnames, tmpsort));
                entries += 1;
            }

            //Letztes Komma aus dem String entfernen, da keine weiteren Einträge kommen
            output = output.Remove(output.Length - 3, 1);

            //Abschluss für JSON einfügen
            output.Append(" ] }");

            string json = output.ToString();

            return json;
        }
        
        //public void close()
        //{
        //    book.Close();
        //    application.Quit();
        //}

        /// <summary>
        /// Erzeugt ein Array welches für jeden Eintrag den Zielindex auf dem Server enthält 
        /// </summary>
        /// <param name="tablename"></param>
        /// <param name="tablesort"></param>
        /// <returns>Das zurückgegebene Array gibt für jede Position im nichtsortierten Zustand die neue Position im sortierten Zustand an.</returns>
        private int[] generateSortingTranslation(string tablename, List<string> tablesort, List<List<string>> serversort)
        {
            int[] sortingTranslation = new int[tablesort.Count];

            foreach (List<string> item in serversort)
            {
                if (item.ElementAt(0) == tablename)
                {
                    for (int i = 1; i < item.Count; i++)
                    {
                        foreach (var tablesortitem in tablesort)
                        {
                            if (tablesortitem == item.ElementAt(i))
                            {
                                int tablesort_item_id = tablesort.IndexOf(tablesortitem);
                                sortingTranslation[tablesort_item_id] = i - 1;
                                break;
                            }
                        }
                    }
                    break;
                }
            }

            return sortingTranslation;

        }

        private string ToJson(string[] fieldname, string[] fieldvalue)
        {
            StringBuilder output = new StringBuilder();

            output.AppendLine("{");

            for (int i = 0; i < fieldname.Length; i++)
            {
                output.Append("\"");
                output.Append(fieldname[i]);
                output.Append("\"");
                output.Append(":");
                output.Append("\"");
                output.Append(fieldvalue[i]);
                output.Append("\"");
                output.AppendLine(",");
            }

            output.AppendLine("},");

            return output.ToString();
        }

        /// <summary>
        /// Lädt aus der Datei environment.xml die Tabellensortierung des Servers
        /// </summary>
        private List<List<string>> loadServerSorting(IProgress<string> reporter)
        {
            if (System.IO.File.Exists("environment.xml"))
            {
                List<List<string>> serversort = new List<List<string>>();

                XmlDocument document = new XmlDocument();
                document.Load("environment.xml");
                XmlNode rootnode = document.GetElementsByTagName("sorting")[0];

                XmlNodeList childs = rootnode.ChildNodes;

                foreach (XmlNode node in childs)
                {
                    List<string> singlesort = new List<string>();

                    singlesort.Add(node.Name);

                    string content = node.InnerText;

                    var cols = content.Split(';');
                    foreach (var col in cols)
                    {
                        singlesort.Add(col);
                    }

                    serversort.Add(singlesort);
                }

                return serversort;
            }
            else
            {
                //TODO: Fehler
                reporter.Report("Konnte die Datei environment.xml nicht finden!");
                return null;
            }
        }

        private List<string> getServerFieldNames(string tableName, List<List<string>> serversort)
        {
            foreach (List<string> item in serversort)
            {
                if (item.ElementAt(0) == tableName)
                {
                    List<string> export = new List<string>();
                    export.AddRange(item);
                    export.Remove(tableName);
                    return export;
                }
            }
            return null;
        }

        /// <summary>
        /// Diese Methode schickt Fortschrittsnachrichten an den Nutzer ohne dabei die Threadbegrenzungen zu durchbrechen
        /// </summary>
        private void ReportProgress(string message)
        {

            caller(message);

            //if (ReportProgressEvent != null)
            //{
                
            //    ISynchronizeInvoke s = (ISynchronizeInvoke) ReportProgressEvent.Target;

            //    if (s != null & s.InvokeRequired)
            //    {
            //        s.Invoke(ReportProgressEvent, new object[] { this, new ExcelEventArgs {EventMessage = message }});
            //    }
            //    else
            //    {
            //        ReportProgressEvent(this, new ExcelEventArgs{EventMessage = message});
            //    }
            //}
        }

    }

    public class ExcelEventArgs : EventArgs
    {
        public string EventMessage { get; set; }
    }
}
