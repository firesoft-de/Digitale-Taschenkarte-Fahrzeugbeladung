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
using System.Threading.Tasks;
using ServerManager.data;
using static ServerManager.Loader.LoadManager;

namespace ServerManager.Loader
{
    class LoaderExcel : AbstractLoader //ILoaderInterface
    {
        string path;
        List<List<string>> serversort;

        List<UploadObject> data;
        List<string> tablenames;
        int entries;
        int tables;

        
        public override List<UploadObject> Data { get => data;}
        public override int Entries { get => entries;}
        public override int Tables { get => tables;}
        public override string Path { get => path; set => path = value; }
        public override List<string> Tablenames { get => tablenames; }

        /// <summary>
        /// Initialisiert den LoaderExcel
        /// </summary>
        /// <param name="path">Pfad der Exceldatei</param>
        public LoaderExcel(string path, loaderCallback callback)
        {
            this.path = path;
            Caller = callback;

            serversort = new List<List<string>>();
            data = new List<UploadObject>();
            tablenames = new List<string>();
            entries = 0;
            tables = 0;
        }


        //public async override void Open()
        //{
        //    var progressreporter = new Progress<string>(ReportProgress);
        //    Task<List<UploadObject>> loadTask;

        //    loadTask = new Task<List<UploadObject>>(() => { return Load(path, progressreporter); });
        //    loadTask.Start();

        //    var res = await loadTask;
        //    data = res;

        //    Caller("Datenabruf abgeschlossen!");
        //    FinishedLoadingEvent();
        //}

        /// <summary>
        /// Führt den Datenabruf durch
        /// </summary>
        /// <param name="path">Pfad der Quelldatei</param>
        /// <param name="reporter">Progress-Objekt um Fortschritt zu melden</param>
        /// <returns></returns>
        public override void Load(IProgress<string> reporter)
        {
            Application application;
            Workbook book;

            reporter.Report("Starte Excel!");

            //Excelanwendung öffnen
            application = new Application();
            application.Visible = false;
            book = application.Workbooks.Open(path);
            
            reporter.Report("Excel gestartet!");

            List<UploadObject> data = new List<UploadObject>();

            //Serversortierung laden
            serversort = LoadServerSorting(reporter);

            if (serversort != null)
            {
                foreach (Worksheet sheet in book.Worksheets)
                {
                    tablenames.Add(sheet.Name);
                    reporter.Report("Lade Daten aus Tabelle: " + sheet.Name);
                    UploadObject currentObject = ReadSheet(sheet, serversort,reporter);
                    currentObject.ID = tables;
                    data.Add(currentObject);
                    tables += 1;
                }
            }
            else
            {
                reporter.Report("Konnte XML-Daten zur Serversortierung nicht laden!");
            }

            book.Close();
            application.Quit();

            this.data = data;
        }

        /// <summary>
        /// Liest ein einzelnes Blatt einer Exceltabelle aus
        /// </summary>
        /// <param name="sheet">Das Arbeitsblatt</param>
        /// <param name="serversort">Die Liste mit den Serversortierungen</param>
        /// <param name="reporter"></param>
        /// <returns></returns>
        private UploadObject ReadSheet(Worksheet sheet, List<List<string>> serversort, IProgress<string> reporter)
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
                if (!res.Contains("Anmerkungen")) { //Absicherung gegen die Spalte Anmkerungen
                    localsort.Add(res);
                }
                else
                {
                    col_anmerkung = i;
                }
            }

            int[] finalsort = generateSortingTranslation(sheet.Name, localsort, serversort);
            string[] fieldnames = getServerFieldNames(name, serversort).ToArray();

            UploadObject uObject = new UploadObject();
            uObject.Table = sheet.Name;

            // Jetzt werden die einzelnen Zellen ausgelesen
            for (int j = 2; j <= rows; j++) //j=2, da die erste Zeile nicht ausgelesen werden soll
            { //Zeilenweise

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
                uObject.AppendJSON(fieldnames, tmpsort);
                entries += 1;
            }

            uObject.FinishJSON();
            
            return uObject;
        }

        /// <summary>
        /// Erstellt aus der Gesamtliste aller ausgelesenen Tabellen (environment.xml) eine Liste mit den Spalten einer spezifischen Tabelle.
        /// Diese Liste enthält als Eintrag 0 NICHT mehr den Tabellenname.
        /// </summary>
        /// <param name="tableName">Aktueller Tabellenname</param>
        /// <param name="serversort">Sortierung des Servers (wird anhand der environment.xml bestimmt)</param>
        /// <returns>String Liste mit den Spalten der eingegebenen Tabelle</returns>
        private List<string> getServerFieldNames(string tableName, List<List<string>> serversort)
        {
            foreach (List<string> item in serversort)
            {
                if (item.ElementAt(0).ToLower() == tableName.ToLower())
                {
                    List<string> export = new List<string>();
                    export.AddRange(item);
                    export.Remove(tableName);
                    return export;
                }
            }
            return null;
        }
    }
}
