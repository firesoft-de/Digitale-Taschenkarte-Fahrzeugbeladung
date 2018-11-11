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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.util.GroupManager;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util.saveImage;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_GROUP;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_NEW_GROUP;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_IMAGE;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_IMAGE_VERSION;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.request;

public class ImageLoader extends AsyncTaskLoader<List<ImageItem>> {

    private static final String LOG_TAG = "ImageLoader_LOG";

    private String url;
    private int version;
    private String group;
    private String newGroup;

    public ImageLoader(Context context, String url, int version, String group, String newGroup){
            super(context);
            this.url = url;
            this.version = version;
            this.group = group;
            this.newGroup = newGroup;
    }

    //Hauptmethode der Klasse. Bewältigt die Hintergrundarbeit
    @Override
    public List<ImageItem> loadInBackground() {
        return requestImages(url, version, getContext(), group, newGroup);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
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
        Bitmap image = null;

        //URL generieren, Util_HTTP_URL im Git nicht enthalten
        String queryURL = url + SERVER_QUERY_IMAGE + "?" + SERVER_QUERY_IMAGE_VERSION + dbVersion;

        if (!group.equals(GroupManager.NO_SUBSCRIBED_GROUPS)) {
            queryURL += "&" + SERVER_QUERY_GET_GROUP + group;
        }

        if (!newGroups.equals(GroupManager.NO_SUBSCRIBED_GROUPS)) {
            queryURL += "&" + SERVER_QUERY_GET_NEW_GROUP + newGroups;
        }

        String response = request(queryURL);

        // Zurückgelieferten JSON String mit den Bildpfaden dekodieren
        try {
            JSONObject baseJsonResponse = new JSONObject(response);

            JSONArray responseArray = baseJsonResponse.getJSONArray("OUTPUT");
            String returnPath;

            for (int i = 0; i < responseArray.length(); i ++) {

                //Json decodieren
                JSONObject object =  responseArray.getJSONObject(i);

                int id = object.getInt("id");
                String destination = object.getString("path");
                int catID = object.getInt("categoryId");

                if (!destination.equals("-1")) {

                    // Ersatzsymbol # mit dem Zielsymbol / austauschen
                    destination = destination.replace("#", "/");

                    InputStream stream = Util_Http.streamRequest(url + destination + Integer.toString(id) + ".jpg");

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
}
