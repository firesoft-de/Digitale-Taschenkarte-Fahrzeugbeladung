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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import firesoft.de.ditaka.wrapper.ExtendedResultWrapper;
import firesoft.de.libfirenet.http.HttpWorker;
import firesoft.de.libfirenet.method.GET;

/**
 * iese Klasse erweitert einen AsyncTaskLoader. Sie stellt Methoden zum Download von Bildern bereit.
 */
public class ImageLoader extends AsyncTaskLoader<ExtendedResultWrapper> {

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
    public ExtendedResultWrapper loadInBackground() {

        HttpWorker httpWorker;

        try {
            httpWorker = new HttpWorker(url, GET.class, this.getContext(), null, null, false, null);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return new ExtendedResultWrapper(e);
        }

        // Netzwerk überprüfen
        if (!httpWorker.checkNetwork(getContext())) {
            return new ExtendedResultWrapper(new NetworkErrorException("Network not available"));
        }

        try {
            httpWorker.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return new ExtendedResultWrapper(e);
        }


        try {
            FileHelper.saveImage(getContext(), id, httpWorker.extractBitmapFromResponse(true));
        } catch (Exception e) {
            e.printStackTrace();
            return new ExtendedResultWrapper(e);
        }

    }

    // endregion
}
