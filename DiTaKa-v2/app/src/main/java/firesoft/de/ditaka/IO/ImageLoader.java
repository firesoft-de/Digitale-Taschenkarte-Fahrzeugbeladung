/*
 *     Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
 *     Copyright (C) 2018  David Schlossarczyk
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     For the full license visit https://www.gnu.org/licenses/gpl-3.0.
 */

package firesoft.de.ditaka.IO;

import android.accounts.NetworkErrorException;
import android.content.AsyncTaskLoader;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;

import firesoft.de.ditaka.wrapper.ImageResultWrapper;
import firesoft.de.libfirenet.http.HttpWorker;
import firesoft.de.libfirenet.method.GET;

/**
 * iese Klasse erweitert einen AsyncTaskLoader. Sie stellt Methoden zum Download von Bildern bereit.
 */
public class ImageLoader extends AsyncTaskLoader<ImageResultWrapper> {

    // region Variablen

    private String url;
    private int id;

    // endregion

    // region Konstruktor

    /**
     * Erstellt eine neue Instanz des ImageLoaders
     * @param url URL des Bilds
     * @param id ID des Bilds
     */
    public ImageLoader(Context context, String url, int id) {
        super(context);
        this.url = url;
    }

    // endregion

    // region Arbeitsmethoden

    @Override
    public ImageResultWrapper loadInBackground() {

        HttpWorker httpWorker;

        // Tausend und eine Möglichkeit für eine Exception :/

        try {
            httpWorker = new HttpWorker(url, GET.class, this.getContext(), null, null, false, null);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return new ImageResultWrapper(e);
        }

        // Netzwerk überprüfen
        if (!httpWorker.checkNetwork(getContext())) {
            return new ImageResultWrapper(new NetworkErrorException("Network not available"));
        }

        try {

            httpWorker.execute();
            FileHelper.saveImage(getContext(), id, httpWorker.extractBitmapFromResponse(true));

        } catch (Exception e) {
            e.printStackTrace();
            return new ImageResultWrapper(e);
        }

        return new ImageResultWrapper(true);

    }

    // endregion
}
