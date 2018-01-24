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


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.util.Util;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.DataFragViewModel;


public class AboutFragment extends Fragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    DataFragViewModel viewModel;

    public AboutFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        //Hier wird das Viewmodel erstellt und durch die Factory mit Eigenschaften versehen
        viewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(DataFragViewModel.class);

        //Link setzen
        TextView tv = view.findViewById(R.id.about_tv2);

        //Aktuelle Version setzen
        String version = "";
        TextView tvVersion = view.findViewById(R.id.about_version);

        try {
            PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (!version.equals("")) {
            tvVersion.setText(version);
        }
        else {
            tvVersion.setText("Version unbekannt");
        }

        final Button resetButton = view.findViewById(R.id.buttonReset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetApp();
            }
        });

        return view;
    }

    private void resetApp() {

        viewModel.deleteAll();
        MainActivity activity = (MainActivity) getActivity();
        activity.gManager.delete();
        activity.pManager.reset();
        activity.dbState = Util.DbState.CLEAN;

        Snackbar.make(getActivity().findViewById(R.id.MainFrame), "Die App wurde erfolgreich zurückgesetzt", Snackbar.LENGTH_SHORT)
                .show();

    }
}
