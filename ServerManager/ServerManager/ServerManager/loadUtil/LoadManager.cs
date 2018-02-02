using ServerManager.data;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace ServerManager.Loader
{
    /// <summary>
    /// Ist für das Laden von Daten aus externen Quellen verantwortlich.
    /// </summary>
    class LoadManager
    {

        public delegate void loaderUICallback(int tableCount, int entryCount, List<string> tablenames);
        public delegate void loaderCallback(string message);

        public event EventHandler FinishedLoadingEvent;

        private loaderCallback caller;

        AbstractLoader loader;

        public enum DataType
        {
            Excel,
            CSV
        }

        public LoadManager(DataType type, string path, loaderCallback caller)
        {
            switch (type)
            {
                case DataType.Excel:
                    loader = new LoaderExcel(path, caller);
                    break;
                case DataType.CSV:
                    break;
            }

            this.caller = caller;
        }

        public async void LoadData()
        {
            var progressreporter = new Progress<string>(ReportProgress);
            Task loadTask;

            loadTask = new Task(() => { loader.Load(progressreporter); });
            loadTask.Start();

            await loadTask;

            caller("Datenabruf abgeschlossen!");
            FinishedLoadingEvent(this, null);
        }

        public List<UploadObject> Data { get => loader.Data; }
        public List<string> Tablenames { get => loader.Tablenames; }
        public int Entries { get => loader.Entries; }
        public int Tables { get => loader.Tables; }

        protected void LoadFinishedMethod()
        {
            FinishedLoadingEvent(this,null);
        }

        /// <summary>
        /// Setzt für ein UploadObject aus der internen Liste den Serverbefehl
        /// </summary>
        /// <param name="index">Index des zu modifzierenden Objektes</param>
        /// <param name="command">Der Befehl</param>
        public void setCommand(int index, string command)
        {
            loader.Data[index].Command = command;
        }

        /// <summary>
        /// Diese Methode schickt Fortschrittsnachrichten an den Nutzer ohne dabei die Threadbegrenzungen zu durchbrechen
        /// </summary>
        public void ReportProgress(string message)
        {
            caller(message);
        }

        public List<UploadObject> GetObjectsByName(List<string> names)
        {
            List<UploadObject> objects = new List<UploadObject>();

            foreach (string name in names)
            {
                foreach (UploadObject item in loader.Data)
                {
                    if (item.Table == name)
                    {
                        objects.Add(item);
                        break;
                    }
                }
            }

            return objects;
        }

    }
}
