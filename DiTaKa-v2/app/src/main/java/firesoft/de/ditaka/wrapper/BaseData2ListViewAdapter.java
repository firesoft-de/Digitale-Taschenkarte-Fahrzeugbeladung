/*
 * Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
 * Copyright (C) 2018  David Schlossarczyk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * For the full license visit https://www.gnu.org/licenses/gpl-3.0.
 */

package firesoft.de.ditaka.wrapper;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import firesoft.de.ditaka.R;
import firesoft.de.ditaka.datamodels.BaseDataClass;
import firesoft.de.ditaka.datamodels.Item;
import firesoft.de.ditaka.datamodels.Tray;


/**
 * Adapterklasse um Daten die von BaseDataClass abgeleitet sind in einem ListView darzustellen.
 * Basierend auf https://stackoverflow.com/q/10584606
 */
public class BaseData2ListViewAdapter extends ArrayAdapter<BaseDataClass> {

    // region Variablen

    private ArrayList<BaseDataClass> data;

    // endregion

    public BaseData2ListViewAdapter(Activity context, ArrayList<BaseDataClass> items) {

        super(context,0,items);
        data = items;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public BaseDataClass getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        BaseDataClass currentElement = data.get(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.listview_item, parent, false);
        }

        // Titel setzen
        ((TextView) view.findViewById(R.id.itemTitle)).setText(currentElement.getName());

        // Position setzen. Hierzu wird geprüft, ob es sich um ein Tray oder ein Item handelt, da diese beiden Klassen über Felder zur Positionsbeschreibung verfügen. Für andere von BaseDataClass abgeleitete Klassen wird die Beschreibung eingesetzt.
        if (currentElement instanceof Tray) {
            ((TextView) view.findViewById(R.id.itemLocation)).setText(((Tray) currentElement).getLocation());
        }
        else if (currentElement instanceof Item) {
            ((TextView) view.findViewById(R.id.itemLocation)).setText(((Item) currentElement).getLocation());
        }
        else {
            ((TextView) view.findViewById(R.id.itemLocation)).setText(currentElement.getDescription());
        }

        return view;
    }
}
