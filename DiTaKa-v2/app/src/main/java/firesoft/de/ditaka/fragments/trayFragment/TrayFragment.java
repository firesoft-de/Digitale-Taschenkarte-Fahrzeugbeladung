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
import android.widget.ListView;

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

    private ListView lv;

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

        // Um Fehler (java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.) zu verhindern, muss attachToRoot = false gesetzt werden!!
        // https://stackoverflow.com/a/47064065
        View view = inflater.inflate(R.layout.listview_layout,container, false);
        lv = view.findViewById(R.id.ListViewMain);

        return view;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);

        try {
            setAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // region

    // Erledigt: Componentmethoden rauschmeißen. Listen werden mit Adaptern gemacht.
    // Erledigt: Methoden einfügen um die Adapter an die Listen anzuhängen -> siehe DiTaKa v1
    // TODO: Für Item-Liste eine Möglichkeit schaffen beim Klicken das ausgewählte Item in das von Dagger verwaltete Itemmodel zu schreiben und dann das Detailfragment anzuzeigen.

    private void setAdapter() throws Exception{

        if (getActivity() == null) {
            throw new Exception("Activity equals null! Thrown by TrayFragment");
        }

        BaseData2ListViewAdapter adapter = new BaseData2ListViewAdapter(getActivity(), ArrayListCoverter.convertToBaseData(trays));

        lv.setAdapter(adapter);

    }

    // endregion

}
