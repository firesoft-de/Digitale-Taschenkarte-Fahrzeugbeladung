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

using Microsoft.Win32;
using ServerManager.data;
using ServerManager.Loader;
using ServerManager.util;
using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;

namespace ServerManager
{
    /// <summary>
    /// Interaktionslogik für MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        
        AppSettings settings;
        HttpManager netManager;
        
        LoadManager loader;
        
        //===========================================================================
        //===========================Window Methoden=================================
        //===========================================================================

        public MainWindow()
        {
            InitializeComponent();
            settings = new AppSettings();
            settings.load();
            txb_url.Text = settings.Url;
            txb_user.Text = settings.User;
            cbBx_Command.SelectedIndex = 0;
        }

        //Toolbar Overflowbutton ausblenden
        private void ToolBar_Loaded(object sender, RoutedEventArgs e)
        {
            ToolBar toolBar = sender as ToolBar;
            var overflowGrid = toolBar.Template.FindName("OverflowGrid", toolBar) as FrameworkElement;
            if (overflowGrid != null)
            {
                overflowGrid.Visibility = Visibility.Collapsed;
            }

            var mainPanelBorder = toolBar.Template.FindName("MainPanelBorder", toolBar) as FrameworkElement;
            if (mainPanelBorder != null)
            {
                mainPanelBorder.Margin = new Thickness(0);
            }
        }

        private void Window_Closed(object sender, EventArgs e)
        {
            //if (eManager != null)
            //{
            //    eManager.close();
            //}

            settings.save();
        }
        
        //===========================================================================
        //==============================UI Methoden==================================
        //===========================================================================

        private void BT_Load_Excel(object sender, RoutedEventArgs e)
        {
            OpenFileDialog fileDialog = new OpenFileDialog();
            fileDialog.Filter="Excel Datei|*.xlsx";
            Nullable<bool> result = fileDialog.ShowDialog();
            
            if (result == true)
            {
                loader = new LoadManager(LoadManager.DataType.Excel, fileDialog.FileName, ExcelCallback);
                loader.FinishedLoadingEvent += new EventHandler(LoadEventHandler);
                loader.LoadData();
            }
            else
            {
                //Fehlermeldung implementieren            
            }
        }

        private void TestSetting_Click(object sender, RoutedEventArgs e)
        {
            AppSettings settings = new AppSettings();
            settings.load();
            settings.Url = "http://test.de";
            settings.User = "asdf";
            settings.save();
            settings = null;
            settings = new AppSettings();
            settings.load();
        }

        private void SendToServer_Click(object sender, RoutedEventArgs e)
        {
            settings.User = txb_user.Text;
            settings.Url = txb_url.Text;

            HttpManager netManager = new HttpManager(settings.Url, HttpCallback, HttpUICallback);
            netManager.SetAuth(settings.User, settings.Url);
            txb_hash.Text = netManager.Pass;
        }

        private void BT_Click_CheckServer(object sender, RoutedEventArgs e)
        {
            if (txb_user.Text != "" && txb_url.Text != "" && txb_pass.Text != "")
            {
                settings.User = txb_user.Text;
                settings.Url = txb_url.Text;

                netManager = new HttpManager(settings.Url, settings.User, txb_pass.Text, HttpCallback, HttpUICallback);
                txb_hash.Text = netManager.Pass;

                netManager.GetDBVersion();
            }
            else
            {
                PrintTXB("Bitte geben Sie Server-Url, Benutzername und Passwort ein!");
            }
        }

        private void BT_Start_Upload(object sender, RoutedEventArgs e)
        {
            //Prüfen ob ein HttpManager exisitiert
            if (netManager != null)
            {
                settings.User = txb_user.Text;

                netManager.SetAuth(settings.User, txb_pass.Text);

                //Prüfen ob per Loader Daten geladen wurden
                if (loader != null && loader.Entries != 0 && loader.Tables != 0)
                {
                    string command = cbBx_Command.Text;
                    List<string> selectedTables = new List<string>();
                    
                    //Prüfen ob Tabellen ausgewählt wurden
                    foreach (CheckBox ckBx in wrpPnl_tables.Children)
                    {
                        if (ckBx.IsChecked == true)
                        {
                            selectedTables.Add(ckBx.Content.ToString());
                        }
                    }

                    if (selectedTables.Count == 0)
                    {
                        PrintTXB("Bitte wählen Sie mindestens eine Tabelle aus.");
                        return;
                    }

                    //Ausgewählte Tabellen abrufen
                    List<UploadObject> objects = loader.GetObjectsByName(selectedTables);

                    foreach (UploadObject item in objects)
                    {
                        //Befehl setzen und abschicken
                        item.Command = command;
                        netManager.PushData(item);
                    }

                    //Version erhöhen
                    int version;

                    if (ckBx_IncreaseServerVersion.IsChecked == true)
                    {
                        version = Int16.Parse(tb_serverversion.Text) + 1;
                    }
                    else
                    {
                        version = Int16.Parse(txb_version.Text);
                    }
                    netManager.SetDBVersion(version);

                    netManager.GetDBVersion();

                }
                else
                {
                    PrintTXB("Keine Daten gefunden! Zuest Daten laden!");
                }
            }
            else
            {
                PrintTXB("Kein netManager initalisiert! Zuerst \"Server abfragen\" verwenden!");
            }
        }

        private void CkBx_IncreaseServerVersion_Checked(object sender, RoutedEventArgs e)
        {
            txb_version.IsEnabled = true;
            txb_version.IsReadOnly = true;
        }

        private void CkBx_IncreaseServerVersion_Unchecked(object sender, RoutedEventArgs e)
        {
            txb_version.IsEnabled = false;
            txb_version.IsReadOnly = false;
        }

        //===========================================================================
        //==============================Hilfsmethoden================================
        //===========================================================================

        /// <summary>
        /// Zeigt eine Nachricht im Ausgabefenster an
        /// </summary>
        /// <param name="message"></param>
        private void PrintTXB(string message)
        {
            txb_response.Text = message + Environment.NewLine + txb_response.Text;
            //txb_response.AppendText(message + Environment.NewLine);
            //txb_response.ScrollToEnd();
        }

        /// <summary>
        /// Dient als Rückrufmethode für den excelManager
        /// </summary>
        /// <param name="message"></param>
        private void ExcelCallback(string message)
        {
            PrintTXB(message);
        }

        private void HttpCallback(int objectId, string message)
        {
            if (objectId >= 0)
            {
                if (message.Contains("SUCCESS"))
                {
                    loader.Data[objectId].RaiseSuccess();
                }
                else
                {
                    loader.Data[objectId].RaiseFailed();
                }
                PrintTXB(message);
            }
            else if (objectId == -2)
            {
                //wird aufgerufen, wenn der Uploadthread seine Arbeit abgeschlossen hat
                UploadObject upload = loader.Data[objectId];
                PrintTXB("Erfolgreiche Uploads: " + upload.SuccessfullUploads.ToString() + " / Gescheiterte Uploads: " + 
                    upload.FailedUploads.ToString());
                PrintTXB(message);
            }
            else
            {
                PrintTXB(message);
            }
        }

        private void LoadEventHandler(object sender,EventArgs e)
        {
            tb_entries.Text = loader.Entries.ToString();
            tb_tables.Text = loader.Tables.ToString();

            foreach (string item in loader.Tablenames)
            {
                CheckBox checkBox = new CheckBox();
                checkBox.Content = item.ToString();
                checkBox.Margin = new Thickness(5, 0, 0, 0);
                wrpPnl_tables.Children.Add(checkBox);
            }
        }

        /// <summary>
        /// Übernimmt die Verarbeitung der Rückgabewerte der asynchron ausgeführten HTTP-Abfragen.
        /// Es werden keine Fortschrittsmeldungen für den Benutzer verarbeitet.
        /// </summary>
        /// <param name="method"></param>
        /// <param name="response"></param>
        private void HttpUICallback(short method, string response, int objectId)
        {
            switch (method)
            {
                case 1:
                    //GetDBVersion
                    PrintTXB("Serverabfrage abgeschlossen!");
                    tb_serverversion.Text = response;
                    break;

                case 0:
                    PrintTXB("Upload abgeschlossen!");
                    UploadObject upload = loader.Data[objectId];
                    PrintTXB("Erfolgreiche Uploads: " + upload.SuccessfullUploads.ToString() + " / Gescheiterte Uploads: " +
                        upload.FailedUploads.ToString());
                    break;
            }
        }
    }
}
