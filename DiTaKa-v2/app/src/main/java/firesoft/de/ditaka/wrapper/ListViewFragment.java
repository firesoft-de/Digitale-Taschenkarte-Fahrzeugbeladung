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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import java.util.ArrayList;

import javax.inject.Inject;

import firesoft.de.ditaka.R;
import firesoft.de.ditaka.datamodels.BasicData;
import firesoft.de.ditaka.interfaces.SwitchFragmentInterface;
import firesoft.de.ditaka.wrapper.BaseDataListViewAdapter;

/**
 * Diese Erweiterung der Fragment-Klasse stellt Methoden zum Arbeiten mit Listviews bereit. Hier wird vom MVVM-Pattern abgewichen, da die Befüllung des Listviews mittels Adapter besser funktioniert und am Ende auf einen ähnlichen Aufbau erzeugt.
 */
public class ListViewFragment extends Fragment {

    // region Variablen
    /**
     * Adapter zum Anzeigen der Daten in der ListView
     */
    BaseDataListViewAdapter adapter;

    /**
     * Variable zum Speichern der ListView
     */
    private ListView lv;

    @Inject
    SwitchFragmentInterface switchOperator;

    // endregion

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState, View view) {

        if (view == null) {
            // Um Fehler (java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.) zu verhindern, muss attachToRoot = false gesetzt werden!!
            // https://stackoverflow.com/a/47064065
            view = inflater.inflate(R.layout.listview_layout,container, false);
        }
        lv = view.findViewById(R.id.ListViewMain);
        return view;
    }

    // region Methoden zum Anzeigen und Verwenden von Daten

    // Erledigt: Componentmethoden rauschmeißen. Listen werden mit Adaptern gemacht.
    // Erledigt: Methoden einfügen um die Adapter an die Listen anzuhängen -> siehe DiTaKa v1
    // TODO: Für Item-Liste eine Möglichkeit schaffen beim Klicken das ausgewählte Item in das von Dagger verwaltete Itemmodel zu schreiben und dann das Detailfragment anzuzeigen.

    /**
     * Erstellt ein Adapter zum Anzeigen von Daten im BasicData-Format
     * @param data Die anzuzeigenden Daten
     * @throws Exception Falls keine Activity vorhanden ist, wird eine Fehlermeldung erzeugt.
     */
    protected void setAdapter(ArrayList<BasicData> data) throws Exception{

        if (getActivity() == null) {
            throw new Exception("Activity equals null! Thrown by TrayFragment");
        }

        BaseDataListViewAdapter adapter = new BaseDataListViewAdapter(getActivity(), data);

        lv.setAdapter(adapter);

    }

    /**
     * Richtet den ItemClickListener für die ListView ein
     */
    protected void setupClickListener() {

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClick(parent,view,position,id);
            }
        });

    }

    /**
     * Definiert die Aktion die ausgeführt wird, wenn ein Item angeklickt wird
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switchOperator.switchFragment(0,null,0);
    }

    // endregion

}
