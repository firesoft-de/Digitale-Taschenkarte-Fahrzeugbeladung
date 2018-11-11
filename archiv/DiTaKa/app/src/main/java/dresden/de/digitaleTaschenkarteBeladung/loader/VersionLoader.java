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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import dresden.de.digitaleTaschenkarteBeladung.util.Util;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util.LogError;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_VERSION;

public class VersionLoader extends AsyncTaskLoader<Integer> {

    private static final String LOG_TAG = "VersionLoader_LOG";

    private String url;
    private int version;
    private Context context;

    public VersionLoader(Context context, String url) {
        super(context);
        this.url = url;
        this.context = context;
    }

    @Override
    public Integer loadInBackground() {
        version = requestVersion(url, context);
        return version;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Ruft die aktuelle Datenbankversion vom Server ab
     * @param url Die Serverurl
     * @return Im Fehlerfall wird -1 zurück gegeben
     */
    private static int requestVersion(String url,Context sContext) {
        // Prüfen ob eine Internetverbindung besteht
        if (Util_Http.checkNetwork(null, sContext)) {
            String response = Util_Http.request(url + SERVER_QUERY_VERSION);

            //Prüfen ob ein Fehler beim Abrufen der Version passiert ist.
            if (response.equals("")) {
                return -1;
            }
            else {
                return Integer.valueOf(response);
            }
        }
        else {
            return -1;
        }
    }

}
