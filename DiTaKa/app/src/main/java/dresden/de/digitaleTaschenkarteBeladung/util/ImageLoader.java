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


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;

public class ImageLoader extends AsyncTaskLoader<List<ImageItem>> {

    private static final String LOG_TAG = "ImageLoader_LOG";

    private String url;
    private int version;

    public ImageLoader(Context context, String url, int version){
            super(context);
            this.url = url;
            this.version = version;
    }

    //Hauptmethode der Klasse. Bewältigt die Hintergrundarbeit
    @Override
    public List<ImageItem> loadInBackground() {
        return Util_Http.requestImages(url, version, getContext());
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


}
