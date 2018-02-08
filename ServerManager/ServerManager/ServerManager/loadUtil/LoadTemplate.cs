using ServerManager.data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Xml;

namespace ServerManager.Loader
{
    /// <summary>
    /// Die abstrakte Klasse sorgt dafür, dass definierte Schnittstellen exisitieren, an denen der LoadManager Aktionen ausführen und Daten abrufen kann.
    /// </summary>
    public abstract class AbstractLoader
    {
        public abstract void Load(IProgress<string> reporter);
        
        /// <summary>
        /// Enthält eine Liste mit UploadObjecten
        /// </summary>
        public abstract List<UploadObject> Data { get; }

        /// <summary>
        /// Enthält die Anzahl der geladenenen Einträge
        /// </summary>
        public abstract int Entries { get; }

        /// <summary>
        /// Enthält die Anzahl der geladenen Tabellen
        /// </summary>
        public abstract int Tables { get; }

        /// <summary>
        /// Enthält den Pfad zur Datenquelle
        /// </summary>
        public abstract string Path { get; set; }

        /// <summary>
        /// Enhält eine Liste mit den Namen der geladenenen Tabellen
        /// </summary>
        public abstract List<string> Tablenames { get; }

        /// <summary>
        /// Stellt eine Möglichkeit zur Verfügung Ausgaben an den Nutzer zu tätigen
        /// </summary>
        internal LoadManager.loaderCallback Caller { get => caller; set => caller = value; }


        //Delegate um Nachrichten aus dieser Klasse über das Ausgabefenster an den Nutzer zu senden 
        private LoadManager.loaderCallback caller;

        /// <summary>
        /// Lädt aus der Datei environment.xml die Tabellensortierung des Servers
        /// </summary>
        public List<List<string>> LoadServerSorting(IProgress<string> reporter)
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

        /// <summary>
        /// Erzeugt ein Array welches für jeden Eintrag den Zielindex auf dem Server enthält 
        /// </summary>
        /// <param name="tablename">Tabellenname</param>
        /// <param name="tablesort">Sortierung der vorliegenden Tabelle</param>
        /// <param name="serversort">Sortierung des Servers (wird anhand der environment.xml bestimmt)</param>
        /// <returns>Das zurückgegebene Array gibt für jede Position im nichtsortierten Zustand die neue Position im sortierten Zustand an.</returns>
        public int[] generateSortingTranslation(string tablename, List<string> tablesort, List<List<string>> serversort)
        {
            int[] sortingTranslation = new int[tablesort.Count];

            foreach (List<string> item in serversort)
            {
                if (item.ElementAt(0).ToLower() == tablename.ToLower())
                {
                    for (int i = 1; i < item.Count; i++)
                    {
                        foreach (var tablesortitem in tablesort)
                        {
                            if (tablesortitem.ToLower() == item.ElementAt(i).ToLower())
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
    }
}
