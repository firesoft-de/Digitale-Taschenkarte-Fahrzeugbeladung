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

import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;

public class VersionLoader extends AsyncTaskLoader<Integer> {

    private static final String LOG_TAG = "VersionLoader_LOG";

    private String url;
    private int version;

    public VersionLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public Integer loadInBackground() {
        version = Util_Http.requestVersion(url);
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
}
