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


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.util.PreferencesManager;

public class SettingsFragment extends Fragment{

    PreferencesManager preferencesManager;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        MainActivity activity = (MainActivity) getActivity();
        preferencesManager = activity.pManager;

        EditText etColorMark = view.findViewById(R.id.et_colorPositionMark);
        etColorMark.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.hasFocus() && !((EditText) view).getText().equals("")) {
                    getColor(view);
                }
            }
        });

        EditText etColorText = view.findViewById(R.id.et_colorPositionText);
        etColorText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.hasFocus() && !((EditText) view).getText().equals("")) {
                    getColor(view);
                }
            }
        });

        etColorText.setText(String.format("#%06X", (0xFFFFFF & preferencesManager.getPositionTextColor())));
        etColorMark.setText(String.format("#%06X", (0xFFFFFF & preferencesManager.getPositionMarkColor())));

        Drawable drawable = getResources().getDrawable(android.R.drawable.dialog_holo_light_frame);

        CardView card = view.findViewById(R.id.settings_card);
        card.setBackground(drawable);

        refreshColorViews(view);

        return view;
    }

    @Override
    public void onDetach() {
        preferencesManager.save();
        super.onDetach();
    }

    //=================================================================
    //==========================Arbeitsmethoden========================
    //=================================================================

    private void getColor(View view) {

        EditText editText = (EditText) view;
        String tmpstring;
        tmpstring = editText.getText().toString();
        final String pattern = "#[A-Fa-f0-9]{6}";


        if (tmpstring.matches(pattern)) {

            switch (editText.getId()) {
                case R.id.et_colorPositionMark:
                    preferencesManager.setPositionMarkColor(Color.parseColor(tmpstring));
                    break;

                case R.id.et_colorPositionText:
                    preferencesManager.setPositionTextColor(Color.parseColor(editText.getText().toString()));
                    break;
            }
            refreshColorViews(null);
        }
        else {
            Toast.makeText(getContext(),"Fehlerhafte Eingabe!",Toast.LENGTH_SHORT).show();
        }
    }


    private void refreshColorViews(@Nullable View view) {

        View colorPositionMark;
        View colorPositionText;

        if (view == null) {
            MainActivity activity = (MainActivity) getActivity();
            colorPositionMark = activity.findViewById(R.id.vw_colorPositionMark);
            colorPositionText = activity.findViewById(R.id.vw_colorPositionText);
        }
        else {
            colorPositionMark = view.findViewById(R.id.vw_colorPositionMark);
            colorPositionText = view.findViewById(R.id.vw_colorPositionText);
        }

        if (preferencesManager.getPositionMarkColor() != 0) {
            colorPositionMark.setBackgroundColor(preferencesManager.getPositionMarkColor());
        }

        if (preferencesManager.getPositionTextColor() != 0) {
            colorPositionText.setBackgroundColor(preferencesManager.getPositionTextColor());
       }
    }
}
