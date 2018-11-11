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

package firesoft.de.ditaka.manager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import firesoft.de.libfirenet.http.HttpLoader;
import firesoft.de.libfirenet.util.ResultWrapper;

/**
 * Verwaltet die im Hintergrund durchzuführenden Aufgaben in Form von Ladern.
 */
public class AsyncTaskManager implements LoaderManager.LoaderCallbacks<ResultWrapper>{

    /**
     * Erstellt neue Loaderinstanzen
     * @param id ID des zu erstellenden Loaders
     * @param args Startargumente
     * @return Instanz des erstellten Loaders
     */
    @NonNull
    @Override
    public Loader<ResultWrapper> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id) {
            case HTTP_LOADER:
                // TODO: Bundle auspacken und dem Loader die benötigten Parameter mitgeben
                return new HttpLoader();
                break;

            default:
            throw new IllegalArgumentException("Couldn't match a Loader to given Loader-id");

        }


        throw new IllegalArgumentException("Couldn't match a Loader to given Loader-id");

    }

    @Override
    public void onLoadFinished(@NonNull Loader<ResultWrapper> loader, ResultWrapper data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ResultWrapper> loader) {

    }

}
