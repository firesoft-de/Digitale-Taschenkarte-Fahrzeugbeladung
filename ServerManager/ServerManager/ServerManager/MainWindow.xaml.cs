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

        //===========================================================================
        //===========================Window Methoden=================================
        //===========================================================================

        public MainWindow()
        {
            InitializeComponent();
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
            httpManager netManager = new httpManager("");
            netManager.setAuth("Nutzer", "pass");
        }

        private void TestServer_Click(object sender, RoutedEventArgs e)
        {
            httpManager netManager = new httpManager(txb_url.Text);
            netManager.setAuth(txb_user.Text, txb_pass.Text);
            tb_serverversion.Text = netManager.testConnection();
        }
    }
}
