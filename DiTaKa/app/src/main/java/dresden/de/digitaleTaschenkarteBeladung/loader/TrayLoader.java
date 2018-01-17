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

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;

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
        return Util_Http.requestTray(url, version, group, newGroup);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


}