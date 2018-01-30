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
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace ServerManager.util
{
    class AppSettings
    {
        private int version = 1;

        private String url;
        private String user;

        public string Url { get => url; set => url = value; }
        public string User { get => user; set => user = value; }

        public AppSettings()
        {

        }

        public void save()
        {
            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Indent = true;

            File.Delete("settings.xml");

            XmlWriter writer = XmlWriter.Create("settings.xml",settings);

            writer.WriteStartDocument();
            writer.WriteStartElement("settings");
            
            writer.WriteStartElement("url");
            writer.WriteAttributeString("version", version.ToString());
            writer.WriteString(url);
            writer.WriteEndElement();

            writer.WriteStartElement("user");
            writer.WriteAttributeString("version", version.ToString());
            writer.WriteString(user);
            writer.WriteEndElement();

            writer.WriteEndDocument();
            writer.Close();
        }

        public void load()
        {
            if (File.Exists("settings.xml"))
            {
                XmlReader reader = XmlReader.Create("settings.xml");
                if (!reader.EOF) { 
                    reader.MoveToContent();
                    while (reader.Read())
                    {
                        if (reader.NodeType == XmlNodeType.Element && reader.Name != "settings")
                        {
                            switch (reader.GetAttribute("version"))
                            {
                                case "1":
                                    load_v1(reader.Name, reader.ReadElementContentAsString());
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                reader.Close();
            }
            else
            {
                defaultsettings();
            }
        }

        private void load_v1(String att_name, String att_content)
        {
            switch (att_name)
            {
                case "url":
                    url = att_content;
                    break;
                case "user":
                    user = att_content;
                    break;
            }
        }

        private void defaultsettings()
        {
            url = "";
            user = "";
        }


    }
}
