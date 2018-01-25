using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Office.Interop.Excel;
using System.Runtime.InteropServices;

namespace ServerManager
{
    class ExcelManager
    {
        string path;

        Application application;
        Workbook book;
        Worksheet sheet;

        /// <summary>
        /// Initialisiert den ExcelManager
        /// </summary>
        /// <param name="path">Pfad der Exceldatei</param>
        public ExcelManager(string path)
        {
            this.path = path;
            open();
        }

        private void open()
        {
            application = new Application();
            application.Visible = true;
            book = application.Workbooks.Open(path);        
        }

        public void close()
        {
            book.Close();
            application.Quit();
        }

    }
}
