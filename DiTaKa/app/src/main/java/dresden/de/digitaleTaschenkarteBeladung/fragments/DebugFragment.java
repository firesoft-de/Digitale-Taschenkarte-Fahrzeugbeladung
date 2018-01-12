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

package dresden.de.digitaleTaschenkarteBeladung.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.Util;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.DebugViewModel;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_ExampleData;


/**
 * {@link DebugFragment} bietet verschiedene Optionen für das Debugging.
 */
public class DebugFragment extends Fragment {

    private final static String LOG_TAG="DebugFragment_LOG";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    DebugViewModel debugViewModel;

    public DebugFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Anweisung an Dagger, dass hier eine Injection vorgenommen wird ??
        ((ApplicationForDagger) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        debugViewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(DebugViewModel.class);

        final TextView tvCountItem = getActivity().findViewById(R.id.debug_db_info_item);

        debugViewModel.countItems().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvCountItem.setText(integer.toString());
            }
        });

        final TextView tvCountTray = getActivity().findViewById(R.id.debug_db_info_tray);

        debugViewModel.countTrays().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvCountTray.setText(integer.toString());
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_debug, container, false);

        //ClickListener einbauen
        Button btDeleteItems = result.findViewById(R.id.btDeleteItem);
        btDeleteItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debugViewModel.deleteAllItem();
            }
        });

        Button btAddItems = result.findViewById(R.id.btAddDummyItem);
        btAddItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<EquipmentItem> items = Util_ExampleData.dummyDataEquipment();
                debugViewModel.addItems(items);
            }
        });

        Button btAddTrays = result.findViewById(R.id.btAddDummyTray);
        btAddTrays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<TrayItem> items = Util_ExampleData.dummyDataTray();
                debugViewModel.addTrays(items);
            }
        });

        Button btDeleteTray = result.findViewById(R.id.btDeleteTray);
        btDeleteTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debugViewModel.deleteTrays();
            }
        });

        Button insertBigDummyItem = result.findViewById(R.id.btInsertBigDummyItem);
        insertBigDummyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<EquipmentItem> list = new ArrayList<>();
                for (int i = 0; i<100; i++) {
                    EquipmentItem item = new EquipmentItem(i+100,"Dummy Item " + i, "Dummy Beschreibung " + i,"Dummy Position " + i, 0);
                    list.add(item);
                }
                debugViewModel.addItems(list);
            }
        });

        Button btResetVersion = result.findViewById(R.id.btResetVersion);
        btResetVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();
                activity.dbVersion = 0;
                activity.liveNetDBVersion.postValue(0);
                activity.getNetDBState(null,true);
            }
        });

        Button btRevertVersion = result.findViewById(R.id.btRevertVersion);
        btRevertVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();
                activity.dbVersion -= 1;
            }
        });

        Button btdeletePrefs = result.findViewById(R.id.btDeletePrefs);
        btdeletePrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                SharedPreferences.Editor editor = activity.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();
                editor.remove(Util.PREFS_URL);
                editor.remove(Util.PREFS_DBVERSION);
                editor.remove(Util.PREFS_GROUPS);
                editor.remove(Util.PREFS_SORT);

                editor.apply();

                Toast.makeText(getContext(),"Prefs gelöscht!",Toast.LENGTH_SHORT).show();
            }
        });

        return result;
    }
}