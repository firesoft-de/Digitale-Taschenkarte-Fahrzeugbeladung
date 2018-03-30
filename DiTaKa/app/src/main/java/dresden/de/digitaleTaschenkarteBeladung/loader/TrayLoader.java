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

package dresden.de.digitaleTaschenkarteBeladung.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.GroupManager;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_GROUP;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_NEW_GROUP;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_TABLE;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_VERSION;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_TABLE_TRAY;

/**
 * Loaderklasse für die Behälterelemente
 */
public class TrayLoader extends AsyncTaskLoader<List<TrayItem>> {

    private static final String LOG_TAG = "TrayLoader_LOG";

    private String url;
    private int version;
    private String group;
    private String newGroup;

    public TrayLoader(Context context, String url, int version, String group, String newGroup){
        super(context);
        this.url = url;
        this.version = version;
        this.group = group;
        this.newGroup = newGroup;
    }

    //Hauptmethode der Klasse. Bewältigt die Hintergrundarbeit
    @Override
    public List<TrayItem> loadInBackground() {
        return requestTray(url, version, group, newGroup);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * Lädt die Datenbankeinträge des Servers für die Trays herunter.
     * @return Liste
     */
    private static ArrayList<TrayItem> requestTray(String url, int dbVersion, String group, String newGroups) {
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

        httpResponse = Util_Http.request(queryURL);

        //Antwort mittels JSON Parser verarbeiten
        if (httpResponse != null) {
            return jsonTrayParsing(httpResponse);
        }

        return null;
    }

    /**
     * Die Methode verarbeitet den Antwortstring des Servers und generiert eine ArrayListe mit den Behältern
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

                item.setPositionCoordinates(object.getString("positions"));

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


}