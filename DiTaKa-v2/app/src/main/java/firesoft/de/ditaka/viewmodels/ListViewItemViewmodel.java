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

package firesoft.de.ditaka.viewmodels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.BaseAdapter;

import firesoft.de.ditaka.datamodels.BaseDataClass;
import firesoft.de.ditaka.datamodels.Item;
import firesoft.de.ditaka.datamodels.Tray;

/**
 * Viewmodel für die angepassten ListViewItems.
 */
public class ListViewItemViewmodel extends ViewModel {

    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////
    //               Klasse wird nicht mehr benötigt
    ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////

    // region UI-Livedata

    public MutableLiveData<String> title;
    public MutableLiveData<String> location;

    // endregion

    // region Datenvariablen

    /**
     * Die Daten werden als BaseDataClass bereitgestellt, da von dieser Klasse alle anzuzeigenden Datentypen abgeleitet werden.
     */
    BaseDataClass data;

    // endregion

    /**
     * Erstellt ein neues Viewmodel für ein ListViewItem. Für jedes Item wird ein eigenes Viewmodel benötigt.
     * @param data
     */
    public ListViewItemViewmodel(BaseDataClass data) {

        this.data = data;

        title = new MutableLiveData<>();
        location = new MutableLiveData<>();

        title.postValue(data.getName());

        // Falls ein Item oder ein Tray übergeben wurde, die Position abfragen.
        // Es sollte eigentlich immer ein Tray oder ein Item übergeben werden. Das ganze ist daher eher eine Sicherheitsmaßnahme
        if (data instanceof Item ) {
            location.postValue(((Item) data).getLocation());
        }
        else if (data instanceof Tray) {
            location.postValue(((Tray) data).getLocation());
        }


    }

    public void onClick() {

        // Todo: Wenn das Item angeklickt wird, muss entweder die Liste der im Tray enthaltenen Items geöffnet werden oder die Beschreibungsseite für das Item geöffnet werden.
        // Dazu muss das ListView im übergeordneten Fragment darüber informiert werden eine andere Liste zu laden.
        // -> Drei Fragments erstellen. Eins für die Fahrzeuge, eins für die Trays, eins für die Items. Jedes verwendet aber das gleiche Layout.

    }


}
