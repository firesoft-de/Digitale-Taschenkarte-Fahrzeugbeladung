using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ServerManager.data
{
    class UploadObject
    {
        private int id;
        private int successfull_uploads;
        private int failed_uploads;
        private string data;
        private string table;
        private string command;

        public UploadObject(int id, string table, string command, string data)
        {
            this.id = id;
            this.data = data;
            this.table = table;
            this.command = command;
        }


        public int ID { get => id; set => id = value; }
        public int SuccessfullUploads { get => successfull_uploads; set => successfull_uploads = value; }
        public int FailedUploads { get => failed_uploads; set => failed_uploads = value; }
        public string Data { get => data; set => data = value; }
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

    }
}
