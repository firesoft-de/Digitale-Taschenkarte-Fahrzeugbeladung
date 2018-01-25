using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Net;
using System.IO;

namespace ServerManager.util
{
    class httpManager
    {
        string url;
        string user;
        string pass;
        WebRequest webRequest;

        static string getdbversion = "getDBVersion.php";

        public httpManager(string url)
        {

            if (url.Substring(0,7) != "http://")
            {
                this.url = "http://" + url;
            }
            else
            {
                this.url = url;
            }

        }

        public void setAuth(string user, string pass)
        {
            this.user = user;
            this.pass = hashPasswort(pass);
        }

        public void send()
        {

        }

        private string hashPasswort(string pass)
        {
            SHA256 hash;
            hash = SHA256.Create();
            byte[] hashResult;
            hashResult = hash.ComputeHash(Encoding.UTF8.GetBytes(pass));

            StringBuilder stringBuilder = new StringBuilder();

            foreach (byte element in hashResult)
            {
                stringBuilder.Append(element.ToString());
            }

            return stringBuilder.ToString();
        }

        public string testConnection()
        {
            string url = mergeURL(getdbversion);
            return connect(url);
        }

        private string mergeURL(string query)
        {
            string result = this.url;
            if (result.Substring(url.Length - 1, 1) == "/") {
                result = result + query;
            }
            else
            {
                result = result + "/" + query;
            }
            
            return result;
        }

        private string connect(string serverURL)
        {
            webRequest = WebRequest.Create(serverURL);

            WebResponse response = webRequest.GetResponse();
            Stream responseStream = response.GetResponseStream();
            StreamReader reader = new StreamReader(responseStream);

            StringBuilder builder = new StringBuilder();

            while (!reader.EndOfStream)
            {
                builder.Append(reader.ReadLine());
            }

            string text = builder.ToString();
            response.Close();
            return text;
        }

    }
}
