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
