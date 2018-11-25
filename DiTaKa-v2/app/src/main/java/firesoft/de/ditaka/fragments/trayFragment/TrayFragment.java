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

package firesoft.de.ditaka.fragments.trayFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import firesoft.de.ditaka.R;
import firesoft.de.ditaka.dagger.InjectableApplication;
import firesoft.de.ditaka.datamodels.Tray;
import firesoft.de.ditaka.util.ArrayListCoverter;
import firesoft.de.ditaka.wrapper.BaseData2ListViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrayFragment extends Fragment {

    @Inject
    ArrayList<Tray> trays;

    BaseData2ListViewAdapter adapter;

    public TrayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ((InjectableApplication) getActivity().getApplication())
                .getComponent()
                .inject(this);

        View view = inflater.inflate(R.layout.listview_layout,container);

        return view;

        //return inflater.inflate(R.layout.listview_layout, container, false);
    }

    // region

    // Erledigt: Componentmethoden rauschmeißen. Listen werden mit Adaptern gemacht.
    // TODO: Methoden einfügen um die Adapter an die Listen anzuhängen -> siehe DiTaKa v1
    // TODO: Für Item-Liste eine Möglichkeit schaffen beim Klicken das ausgewählte Item in das von Dagger verwaltete Itemmodel zu schreiben und dann das Detailfragment anzuzeigen.

    private void setAdapter() {

        BaseData2ListViewAdapter adapter = new BaseData2ListViewAdapter(getActivity(), ArrayListCoverter.convertToBaseData(trays));

    }

    // endregion

}
