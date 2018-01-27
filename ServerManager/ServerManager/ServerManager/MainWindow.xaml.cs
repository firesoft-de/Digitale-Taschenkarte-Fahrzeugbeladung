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
using ServerManager.util;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace ServerManager
{
    /// <summary>
    /// Interaktionslogik für MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {

        ExcelManager eManager;
        appSettings settings;

        //===========================================================================
        //===========================Window Methoden=================================
        //===========================================================================

        public MainWindow()
        {
            InitializeComponent();
            settings = new appSettings();
            settings.load();
            txb_url.Text = settings.Url;
            txb_user.Text = settings.User;
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
            if (eManager != null)
            {
                eManager.close();
            }

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
            
            if (result == true) {
                eManager = new ExcelManager(fileDialog.FileName);
            }
            else
            {
                //Fehlermeldung implementieren            
            }
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            settings.User = txb_user.Text;
            settings.Url = txb_url.Text;
            settings.save();

            httpManager netManager = new httpManager(settings.Url);
            netManager.setAuth(settings.User, settings.Url);
            txb_hash.Text = netManager.Pass;
            
            txb_response.Text += netManager.testUserAndPass() + Environment.NewLine;
        }

        private void TestServer_Click(object sender, RoutedEventArgs e)
        {
            settings.User = txb_user.Text;
            settings.Url = txb_url.Text;

            httpManager netManager = new httpManager(settings.Url);
            netManager.setAuth(settings.User, settings.Url);
            txb_hash.Text = netManager.Pass;
            tb_serverversion.Text = netManager.testConnection();
        }

        private void TestSetting_Click(object sender, RoutedEventArgs e)
        {
            appSettings settings = new appSettings();
            settings.load();
            settings.Url = "http://test.de";
            settings.User = "asdf";
            settings.save();
            settings = null;
            settings = new appSettings();
            settings.load();
        }
    }
}
