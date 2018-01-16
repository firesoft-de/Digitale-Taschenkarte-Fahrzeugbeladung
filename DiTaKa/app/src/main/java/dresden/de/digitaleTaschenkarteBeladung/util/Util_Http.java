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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ArrayAdapter;

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

import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util.LogError;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util.saveImage;

public class Util_Http {

    private static final String LOG_TRACE = "Util_Http";

    private static final String SERVER_QUERY_GET = "/getDatabase.php?";
    private static final String SERVER_QUERY_GET_VERSION = "dbversion=";
    private static final String SERVER_QUERY_GET_TABLE = "dbtable=";
    private static final String SERVER_QUERY_GET_GROUP = "groups=";
    private static final String SERVER_QUERY_GET_NEW_GROUP = "loadfullgroup=1&fullgroups=";

    private static final String SERVER_QUERY_VERSION = "/getDBVersion.php";

    private static final String SERVER_QUERY_IMAGE = "/getImageList.php";
    private static final String SERVER_QUERY_IMAGE_VERSION = "dbVersion=";

    private static final String SERVER_TABLE_ITEM = "equipment";
    private static final String SERVER_TABLE_TRAY = "tray";
    private static final String SERVER_TABLE_GROUP = "group";

    /**
     * Die Methode fragt die Ausrüstungsgegenstände vom Server ab
     * @param url Enthält die rohe Serverurl
     * @param dbVersion Enthält die Datenbankversion des Clients
     * @param group Enthält die abonnierten Gruppen
     * @return
     */
    public static ArrayList<EquipmentItem> requestItems(String url, int dbVersion, String group, String newGroups) {
        String httpResponse = null;

        String queryURL = url + SERVER_QUERY_GET + SERVER_QUERY_GET_VERSION +
                dbVersion + "&" + SERVER_QUERY_GET_TABLE + SERVER_TABLE_ITEM;

        if (!group.equals(GroupManager.NO_SUBSCRIBED_GROUPS)) {
            queryURL += "&" + SERVER_QUERY_GET_GROUP + group;
        }

        if (!newGroups.equals(GroupManager.NO_SUBSCRIBED_GROUPS)) {
            queryURL += "&" + SERVER_QUERY_GET_NEW_GROUP + newGroups;
        }

        URL urlV = generateURL(queryURL);

        //HTTP Abfrage durchführen
        if (urlV != null) {
            InputStream stream =  httpsRequester(urlV);
            httpResponse = readStream(stream);
        }

        //Antwort mittels JSON Parser verarbeiten
        if (httpResponse != null) {
            return jsonItemParsing(httpResponse);
        }

        return null;
    }

    /**
     * führt eine Datenabfrage mittels HTTP-Protokoll durch
     * @return Liste
     */
    public static ArrayList<TrayItem> requestTray(String url, int dbVersion, String group, String newGroups) {
        String httpResponse = null;

        //URL generieren, Util_HTTP_URL im Git nicht enthalten
        String queryURL = url + SERVER_QUERY_GET + SERVER_QUERY_GET_VERSION +
                dbVersion + "&" + SERVER_QUERY_GET_TABLE + SERVER_TABLE_TRAY;

        if (!group.equals(GroupManager.NO_SUBSCRIBED_GROUPS)) {
            queryURL += "&" + SERVER_QUERY_GET_GROUP + group;
        }

        if (!newGroups.equals(GroupManager.NO_SUBSCRIBED_GROUPS)) {
            queryURL += "&" + SERVER_QUERY_GET_NEW_GROUP + newGroups;
        }

        URL urlV = generateURL(queryURL);

        //HTTP Abfrage durchführen
        if (urlV != null) {
            InputStream stream =  httpsRequester(urlV);
            httpResponse = readStream(stream);
        }

        //Antwort mittels JSON Parser verarbeiten
        if (httpResponse != null) {
            return jsonTrayParsing(httpResponse);
        }

        return null;
    }

    /**
     * Ruft die aktuelle Datenbankversion vom Server ab
     * @param url
     * @return Im Fehlerfall wird -1 zurück gegeben
     */
    public static int requestVersion(String url) {
            int result = -1;

            URL urlV = generateURL(url + SERVER_QUERY_VERSION);
            InputStream stream = null;

            if (urlV != null) {
                try {
                    stream = httpsRequester(urlV);
                } catch (Exception e) {
                    LogError(LOG_TRACE,"Fehler beim Konvertieren der Versionantwort nach Integer! Nachricht: "+e.getMessage());

                    //Alternative HTTP Verbindung aufbauen
                    try {
                        URL newURL = new URL("http", urlV.getHost(), urlV.getFile());
                        stream = httpRequester(newURL);
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                        return -1;
                    }
                }
            }

            //Prüfen ob ein Fehler beim Abrufen der Version passiert ist.
            String response = readStream(stream);
            if (response.equals("")) {
                return -1;
            }
            else {
                Integer integer = new Integer(response);
                result = integer;

                return result;
            }
        }

    /**
     * Diese Methode lädt Bilder vom Server und speichert sie lokal. Zum Zugriff wird eine Liste von ImageItems zurückgegeben.
     * @param url die Serverurl
     * @param dbVersion die lokale Datenbankversion
     * @param context Context der Ausführung
     * @param group String mit den abonnierten Gruppen
     * @return Liste der Bilder als Imageitems
     */
    public static ArrayList<ImageItem> requestImages(String url, int dbVersion, Context context, String group, String newGroups) {
        ArrayList<ImageItem> items = new ArrayList<>();

        //URL generieren, Util_HTTP_URL im Git nicht enthalten
        String queryURL = url + SERVER_QUERY_IMAGE + "?" + SERVER_QUERY_IMAGE_VERSION + dbVersion;

        if (!group.equals(GroupManager.NO_SUBSCRIBED_GROUPS)) {
            queryURL += "&" + SERVER_QUERY_GET_GROUP + group;
        }

        if (!newGroups.equals(GroupManager.NO_SUBSCRIBED_GROUPS)) {
            queryURL += "&" + SERVER_QUERY_GET_NEW_GROUP + newGroups;
        }

        URL urlV = generateURL(queryURL);

        Bitmap image = null;
        InputStream stream = httpsRequester(urlV);
        String response = readStream(stream);

        //Bildpfade aufschlüsseln
        ArrayList<String> path = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(response);

            JSONArray responseArray = baseJsonResponse.getJSONArray("OUTPUT");
            String returnPath = "";

            for (int i = 0; i < responseArray.length(); i ++) {

                //Json decodieren
                JSONObject object =  responseArray.getJSONObject(i);

                int id = object.getInt("id");
                String destination = object.getString("path");
                int catID = object.getInt("categoryId");

                if (!destination.equals("-1")) {

                    destination = destination.replace("#", "/");

                    URL urlX = generateURL(url + destination + Integer.toString(id) + ".jpg");
                    stream = httpsRequester(urlX);

                    //Das Bild herunterladen
                    try {
                        image = BitmapFactory.decodeStream(stream);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Bild speichern und das ImageItem erstellen
                    returnPath = saveImage(id,image, context);

                }
                else {
                    returnPath = "-1";
                }

                ImageItem item = new ImageItem(id,returnPath,catID);
                //Gruppenid setzen
                item.setGroup(object.getString("groupId"));
                items.add(item);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //TODO: Ordentliche Fehlerbehandlung
        }

        return items;
    }


    public static ArrayList<String> requestGroups(String url, int version) {
        ArrayList<String> list = new ArrayList<>();

        URL urlV = generateURL(url + SERVER_QUERY_GET + SERVER_QUERY_GET_VERSION +
                version + "&" + SERVER_QUERY_GET_TABLE + SERVER_TABLE_GROUP);

        InputStream stream = httpsRequester(urlV);
        String response = readStream(stream);

        list = jsonGroupParsing(response);

        return list;
    }

    /**
     * {@jsonItemParsing} verarbeitet den Antwortstring des Servers und generiert eine Ausrüstungsliste
     * @param response Die Serverantwort
     * @return ArrayListe mit den Ausrüstungsgegenständen
     */
    private static ArrayList<EquipmentItem> jsonItemParsing(String response) {

        ArrayList<EquipmentItem> equipmentList = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(response);

            JSONArray responseArray = baseJsonResponse.getJSONArray("OUTPUT");

            for (int i = 0; i < responseArray.length(); i ++) {
                JSONObject object =  responseArray.getJSONObject(i);

                EquipmentItem item = new EquipmentItem(object.getInt("id"),
                        object.getString("name"),
                        object.getString("description"),
                        object.getString("setName"),
                        object.getString("position"),
                        object.getInt("categoryId"),null);

                String keywords = object.getString("keywords");
                String[] keys = keywords.split(",");
                item.setKeywordsFromArray(keys);

                //Hinweise einfügen
                item.setAdditionalNotes(object.getString("notes"));

                //Den Positionsmarkierungsindex setzen
                item.setPositionIndex(object.getInt("positionID"));

                //Gruppenid setzen
                item.setGroup(object.getString("groupId"));

                equipmentList.add(item);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            //TODO: Ordentliche Fehlerbehandlung
        }

        return equipmentList;
    }

    /**
     * {@jsonTrayParsing} verarbeitet den Antwortstring des Servers und generiert eine ArrayListe mit den Behältern
     * @param response Die Serverantwort
     * @return Liste mit den Ausrüstungsgegenständen
     */
    private static ArrayList<TrayItem> jsonTrayParsing(String response) {

        ArrayList<TrayItem> trayList  = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(response);
            JSONArray responseArray = baseJsonResponse.getJSONArray("OUTPUT");

            for (int i = 0; i < responseArray.length(); i ++) {
                JSONObject object =  responseArray.getJSONObject(i);

                TrayItem item = new TrayItem(object.getInt("id"),
                        object.getString("name"),
                        object.getString("description"));

                item.positionCoordFromString(object.getString("positions"));

                //Gruppenid setzen
                item.setGroup(object.getString("groupId"));

                trayList.add(item);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            //TODO: Ordentliche Fehlerbehandlung
        }

        return trayList;
    }

    private static ArrayList<String> jsonGroupParsing(String response) {

        ArrayList<String> list  = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(response);
            JSONArray responseArray = baseJsonResponse.getJSONArray("OUTPUT");

            for (int i = 0; i < responseArray.length(); i ++) {
                JSONObject object =  responseArray.getJSONObject(i);
                list.add(object.getString("name"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //TODO: Ordentliche Fehlerbehandlung
        }
        return list;
    }

    /**
     * Diese Methode ist für den eigentliche Request zuständig. Es wird eine SSL Verbindung mittels HTTPS genutzt.
     * @param url die Server-URL
     * @return Antwort des Servers als String
     */
    private static InputStream httpsRequester(URL url) {

        //TODO: ACHTUNG! AUS PERFORMANCEGRÜNDEN WIRD DER HTTPS REQUEST HIER ÜBERSCHRIEBEN!!

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

                    //Alternative HTTP Verbindung aufbauen
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

                            //Alternative HTTP Verbindung aufbauen
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

        String response = null;
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

    public static boolean checkNetwork(Activity activity, Context context) {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Verbindung
            return true;
        } else {
            // Keine Verbindung
            return false;
        }
    }


    /**
     * Diese Methode bearbeitet die eingebene URL so, dass sie konform mit den nachfolgenden Arbeitschritte ist. Außerdem wird die URL in den PREFS gespeichert.
     * @param url Die zu bearbeitende URL
     * @return Die bearbietete URL
     */
    public static String handleURL(String url, Activity activity) {
        //Den PREF Manager initaliseren
        SharedPreferences.Editor editor = activity.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();

        //https einfügen falls nicht vorhanden
        if (!url.contains("http://") && !url.contains("https://")) {
            //TODO: ACHTUNG! AUS PERFORMANCEGRÜNDEN WIRD DER HTTPS REQUEST HIER ÜBERSCHRIEBEN!!
            url = "http://" + url;
        }

        //Prüfen ob als letztes Zeichen ein / vorhanden ist und dieses ggf. entfernen
        if ((url.charAt(url.length() - 2)) == '/') {
            url = (String) url.subSequence(0, url.length() - 3);
        }

        editor.putString(Util.PREFS_URL, url);
        editor.apply();

        return url;
    }
}