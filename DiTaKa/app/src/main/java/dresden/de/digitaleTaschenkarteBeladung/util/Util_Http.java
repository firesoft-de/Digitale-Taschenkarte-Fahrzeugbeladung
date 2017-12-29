package dresden.de.digitaleTaschenkarteBeladung.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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

import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;

public class Util_Http {
    //TODO: Feedback für AsnycLoader einfügen

    private static final String LOG_TRACE = "Util_Http";

    public static final String SERVER_QUERY_GET = "/getDatabase.php?dbVersion=";

        /**
         * @return Liste
         */
        public ArrayList<EquipmentItem> requestItems(String url, int dbVersion) {

            String httpResponse = null;

            //URL generieren, Util_HTTP_URL im Git nicht enthalten
            URL urlV = generateURL(url + SERVER_QUERY_GET + dbVersion);

            //HTTP Abfrage durchführen
            if (url != null) {
                httpResponse = httpRequester(urlV);
            }

            //Antwort mittels JSON Parser verarbeiten
            if (httpResponse != null) {
                return jsonItemParsing(httpResponse);
            }

            return null;
        }

        /**
         *{@requestItems} führt eine Datenabfrage mittels HTTP-Protokoll durch
         * @return Liste
         */
        public ArrayList<TrayItem> requestTray(String url, int dbVersion) {

            String httpResponse = null;

            //URL generieren, Util_HTTP_URL im Git nicht enthalten
            URL urlV = generateURL(url + SERVER_QUERY_GET+ dbVersion);

            //HTTP Abfrage durchführen
            if (url != null) {
                httpResponse = httpRequester(urlV);
            }

            //Antwort mittels JSON Parser verarbeiten
            if (httpResponse != null) {
                return jsonTrayParsing(httpResponse);
            }

            return null;
        }

        /**
         * {@jsonItemParsing} verarbeitet den Antwortstring des Servers und generiert eine Ausrüstungsliste
         * @param response Die Serverantwort
         * @return ArrayListe mit den Ausrüstungsgegenständen
         */
        private ArrayList<EquipmentItem> jsonItemParsing(String response) {

            ArrayList<EquipmentItem> equipmentList = new ArrayList<>();

            //TODO JSON Verarbeitung für die Gegenstände implementieren!


            try {
                JSONObject baseJsonResponse = new JSONObject(response);


                JSONArray responseArray = baseJsonResponse.getJSONArray("Equipment");

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
        private ArrayList<TrayItem> jsonTrayParsing(String response) {

            ArrayList<TrayItem> trayList = null;

            //TODO JSON Verarbeitung für die Behälter implementieren!

            return trayList;
        }


        /**
         * {@httpRequester} führt den HTTP Request durch
         * @param url die Server-URL
         * @return Antwort des Servers als String
         */
        private String httpRequester(URL url) {

            //Response Variable
            String response = null;

            //Interne Variablen
            HttpURLConnection connection = null;

            //HTTP Verbindung aufbauen
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
                        response = readStream( connection.getInputStream());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(LOG_TRACE, "Fehler während der Verbindungsherstellung! Meldung: " + e.getMessage());
                }
            }
            return response;
        }

        /**
         * Hilfsmethode um die Streamverarbeitung der HTTP-Verbindung zu übernehmen
         * @param input Der Stream des Servers als InputStream
         * @return die vom Server gesendeten Daten als String
         */
        private String readStream(InputStream input) {

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
                    Log.e(LOG_TRACE,"IOException während des auslesens des InputStreams im BufferedReader! Meldung: " + e.getMessage());
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
        private URL generateURL(String url) {

            URL generatedUrl = null;

            try {

                generatedUrl = new URL(url);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(LOG_TRACE, "Konnte URL für den Loader nicht erstellen! Nachricht: " + e.getMessage());
            }

            return generatedUrl;
        }

        public static boolean checkNetwork(Activity activity, Context context) {
            // Get a reference to the ConnectivityManager to check state of network connectivity
            ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(context.CONNECTIVITY_SERVICE);

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

}