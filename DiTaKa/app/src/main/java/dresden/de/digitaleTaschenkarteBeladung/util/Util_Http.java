/*  Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
    Copyright (C) 2017  David Schlossarczyk

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    For the full license visit https://www.gnu.org/licenses/gpl-3.0.*/

package dresden.de.digitaleTaschenkarteBeladung.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.Group;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util.LogError;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util.saveImage;

/**
 * Stellt Methoden zum Verbinden mit einem HTTP-Server zur Verfügung. Gleichzeitig werden die benötigten Serverquerys in dieser Klasse vorgehalten.
 */
public class Util_Http {

    private static final String LOG_TRACE = "Util_Http";

    public static final String SERVER_QUERY_GET = "/getDatabase.php?";
    public static final String SERVER_QUERY_GET_VERSION = "dbversion=";
    public static final String SERVER_QUERY_GET_TABLE = "dbtable=";
    public static final String SERVER_QUERY_GET_GROUP = "groups=";
    public static final String SERVER_QUERY_GET_NEW_GROUP = "loadfullgroup=1&fullgroups=";

    public static final String SERVER_QUERY_VERSION = "/getDBVersion.php";

    public static final String SERVER_QUERY_IMAGE = "/getImageList.php";
    public static final String SERVER_QUERY_IMAGE_VERSION = "dbVersion=";

    public static final String SERVER_TABLE_ITEM = "equipment";
    public static final String SERVER_TABLE_TRAY = "tray";
    public static final String SERVER_TABLE_GROUP = "group";

    /**
     * Führt eine HTTP-Abfrage unter der mitgelieferten URL durch.
     * @param url Die Zieladresse
     * @return Antwort des Ziels als String
     */
    public static String request(String url) {
        URL urlV = generateURL(url);

        InputStream stream = httpsRequester(urlV);
        String response = readStream(stream);

        return response;
    }

    /**
     * Führt eine HTTP-Abfrage unter der mitgelieferten URL durch.
     * @param url Die Zieladresse
     * @return Antwort des Ziels als InputStream
     */
    public static InputStream streamRequest(String url) {
        URL urlV = generateURL(url);
        InputStream stream = httpsRequester(urlV);
        return stream;
    }

    /**
     * Diese Methode ist für den eigentliche Request zuständig. Es wird eine SSL Verbindung mittels HTTPS genutzt.
     * @param url die Server-URL
     * @return Antwort des Servers als String
     */
    public static InputStream httpsRequester(URL url) {

        //=======================================================
        //===========================ACHTUNG=====================
        //=======================================================

        // Da es teilweise zu Problemen mit dem Erkennen von Zertifikatspfaden kam, wird an dieser Stelle HTTPS übergangen.
        // Stattdessen wird eine normale HTTP Verbindung aufgebaut

        if (true) {
            return httpRequester(url);
        }
        else {
            //Response Variable
            InputStream response = null;

            //Interne Variablen
            HttpsURLConnection connection = null;

            String protocol = url.getProtocol();
            if (!protocol.contains("s")) {
                response = httpRequester(url);
            } else {
                //HTTPS Verbindung aufbauen
                try {
                    connection = (HttpsURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();

                    //Alternative HTTP Verbindung aufbauen, da die HTTPS-Verbindung fehlgeschlagen ist
                    URL newURL = null;
                    try {
                        newURL = new URL("http", url.getHost(), url.getFile());
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    }
                    response = httpRequester(newURL);
                }

                if (connection != null) {

                    try {
                        //Verbindungseinstellungen
                        connection.setConnectTimeout(10000);
                        connection.setReadTimeout(10000);
                        connection.setRequestMethod("GET");

                        //Verbindung herstellen
                        connection.connect();

                        //Verbindungsantwort prüfen
                        if (connection.getResponseCode() == 200) {
                            //Verbindung erfolgreich hergestellt

                            //Übergabe des InputStreams zur Verarbeitung
                            response = connection.getInputStream();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e.getMessage().contains("java.security.cert.CertPathValidatorException")) {

                            //Alternative HTTP Verbindung aufbauen, da die HTTPS-Verbindung fehlgeschlagen ist
                            URL newURL = null;
                            try {
                                newURL = new URL("http", url.getHost(), url.getFile());
                            } catch (MalformedURLException e1) {
                                e1.printStackTrace();
                            }
                            response = httpRequester(newURL);
                        }
                        LogError(LOG_TRACE, "Fehler während der Verbindungsherstellung! Meldung: " + e.getMessage());
                    }
                }
            }
            return response;
        }
    }

    /**
     * Diese Methode ist für den eigentliche Request zuständig. Sie wird genutzt wenn keine SSL Verbindung aufgebaut werden kann
     * @param url die Server-URL
     * @return Antwort des Servers als String
     */
    private static InputStream httpRequester(URL url) {

        //Response Variable
        InputStream response = null;

        //Interne Variablen
        HttpURLConnection connection = null;

        //HTTPS Verbindung aufbauen
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (connection != null) {
            try {
                //Verbindungseinstellungen
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestMethod("GET");

                //Verbindung herstellen
                connection.connect();

                //Verbindungsantwort prüfen
                if (connection.getResponseCode() == 200) {
                    //Verbindung erfolgreich hergestellt

                    //Übergabe des InputStreams zur Verarbeitung
                    response = connection.getInputStream();
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogError(LOG_TRACE, "Fehler während der Verbindungsherstellung! Meldung: " + e.getMessage());
            }
        }
        return response;
    }

    /**
     * Hilfsmethode um die Streamverarbeitung der HTTP-Verbindung zu übernehmen
     * @param input Der Stream des Servers als InputStream
     * @return die vom Server gesendeten Daten als String
     */
    private static String readStream(InputStream input) {

        String response;
        StringBuilder builder = new StringBuilder();

        if (input != null) {

            //reader erstellen und diesen buffern
            InputStreamReader reader = new InputStreamReader(input, Charset.forName("UTF-8"));
            BufferedReader bReader = new BufferedReader(reader);


            try {
                String line = bReader.readLine();

                while (line != null) {
                    builder.append(line);
                    line = bReader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogError(LOG_TRACE,"IOException während des auslesens des InputStreams im BufferedReader! Meldung: " + e.getMessage());
            }

        }

        response = builder.toString();

        return response;
    }

    /**
     * Hilfsmethode um schnell eine URL zu erzeugen
     * @param url URL als String
     * @return URL als URL-Objekt
     */
    private static URL generateURL(String url) {

        URL generatedUrl = null;

        try {

            generatedUrl = new URL(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            LogError(LOG_TRACE, "Konnte URL für den Loader nicht erstellen! Nachricht: " + e.getMessage());
        }

        return generatedUrl;
    }

    public static boolean checkNetwork(Activity activity) {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        if (connMgr != null) {
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // If there is a network connection, fetch data
            return networkInfo != null && networkInfo.isConnected();

        }
        else {
            return false;
        }
    }


    /**
     * Diese Methode bearbeitet die eingebene URL so, dass sie konform mit den nachfolgenden Arbeitschritte ist. Außerdem wird die URL in den PREFS gespeichert.
     * @param url Die zu bearbeitende URL
     * @return Die bearbietete URL
     */
    public static String handleURL(String url) {
        //https einfügen falls nicht vorhanden
        if (!url.contains("http://") && !url.contains("https://")) {
            //TODO: ACHTUNG! AUS PERFORMANCEGRÜNDEN WIRD DER HTTPS REQUEST HIER ÜBERSCHRIEBEN!!
            url = "http://" + url;
        }

        //Prüfen ob als letztes Zeichen ein / vorhanden ist und dieses ggf. entfernen
        if ((url.charAt(url.length() - 2)) == '/') {
            url = (String) url.subSequence(0, url.length() - 3);
        }

        return url;
    }
}