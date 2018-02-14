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
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.util.GroupManager;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_GROUP;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_NEW_GROUP;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_TABLE;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_VERSION;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_TABLE_ITEM;


/**
 * Loader für die Gegenstände
 */
public class ItemLoader extends AsyncTaskLoader<List<EquipmentItem>> {

    private static final String LOG_TAG = "ItemLoader_LOG";

    private String url;
    private int version;
    private String group;
    private String newGroup;

    public ItemLoader(Context context, String url, int version, String group, String newGroup){

        super(context);
        this.url = url;
        this.version = version;
        this.group = group;
        this.newGroup = newGroup;
    }

    //Hauptmethode der Klasse. Bewältigt die Hintergrundarbeit
    @Override
    public List<EquipmentItem> loadInBackground() {
        return requestItems(url, version, group, newGroup);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * Die Methode fragt die Ausrüstungsgegenstände vom Server ab
     * @param url Enthält die rohe Serverurl
     * @param dbVersion Enthält die Datenbankversion des Clients
     * @param group Enthält die abonnierten Gruppen
     * @return Liste der abgefragten EquipmentItems
     */
    private static ArrayList<EquipmentItem> requestItems(String url, int dbVersion, String group, String newGroups) {
        String response = null;

        // URL zusammenbauen und dabei berücksichtigen, dass manche Gruppe komplett und andere nur partiell heruntergeladen werden müssen
        String queryURL = url + SERVER_QUERY_GET + SERVER_QUERY_GET_VERSION +
                dbVersion + "&" + SERVER_QUERY_GET_TABLE + SERVER_TABLE_ITEM;

        if (!group.equals(GroupManager.NO_SUBSCRIBED_GROUPS)) {
            queryURL += "&" + SERVER_QUERY_GET_GROUP + group;
        }

        if (!newGroups.equals(GroupManager.NO_SUBSCRIBED_GROUPS)) {
            queryURL += "&" + SERVER_QUERY_GET_NEW_GROUP + newGroups;
        }

        response = Util_Http.request(queryURL);

        //Antwort mittels JSON Parser verarbeiten
        if (response != null) {
            return jsonItemParsing(response);
        }
        else {
            //TODO: Ordentliche Fehlerbehandlung
        }

        return null;
    }

    /**
     * Die Methode verarbeitet den Antwortstring des Servers und generiert eine Ausrüstungsliste
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

}
