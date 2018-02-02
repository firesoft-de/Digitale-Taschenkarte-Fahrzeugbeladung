using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ServerManager.data
{
    public class UploadObject
    {
        private int id;
        private StringBuilder data;
        private string table;
        private string command;

        private int successfull_uploads;
        private int failed_uploads;

        public UploadObject(int id, string table, string command, string data)
        {
            this.data = new StringBuilder();
            this.data.Append(data);
            this.id = id;
            this.table = table;
            this.command = command;
        }

        public UploadObject()
        {
            data = new StringBuilder();
            data.Append("{ \"INPUT\": [");
        }
        
        public int ID { get => id; set => id = value; }
        public int SuccessfullUploads { get => successfull_uploads; set => successfull_uploads = value; }
        public int FailedUploads { get => failed_uploads; set => failed_uploads = value; }
        public string Data { get => data.ToString(); }
        public string Table { get => table; set => table = value; }
        public string Command { get => command; set => command = value; }

        public void RaiseSuccess()
        {
            successfull_uploads += 1;
        }

        public void RaiseFailed()
        {
            failed_uploads += 1;
        }

        /// <summary>
        /// Baut mit den eingegebenen Arrays ein JSON Objekt auf und fügt dieses an das Data Feld an
        /// </summary>
        /// <param name="fieldname">Array mit den Feldnamen</param>
        /// <param name="fieldvalue">Array mit den Feldwerten</param>
        public void AppendJSON(string[] fieldname, string[] fieldvalue)
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

            output.Remove(output.Length - 3, 2);
            output.AppendLine("},");

            data.Append(output.ToString());
        }

        public void FinishJSON()
        {
            //Letztes Komma aus dem String entfernen, da keine weiteren Einträge kommen
            data = data.Remove(data.Length - 3, 1);

            //Abschluss für JSON einfügen
            data.Append(" ] }");
        }

    }
}
