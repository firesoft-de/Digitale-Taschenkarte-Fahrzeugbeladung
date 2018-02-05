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


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

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

        //UI in den Standardzustand bringen

        TextView errorTV = view.findViewById(R.id.settings_color_error);
        errorTV.setVisibility(View.GONE);

        etColorText.setText(String.format("#%06X", (0xFFFFFF & preferencesManager.getPositionTextColor())));
        etColorMark.setText(String.format("#%06X", (0xFFFFFF & preferencesManager.getPositionMarkColor())));

        CheckBox cb = view.findViewById(R.id.settings_cb_network_autocheck);
        cb.setChecked(preferencesManager.isCheckForUpdateAllowed());

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkStateChangeAutocheck(null, b);
            }
        });

        //Elevationeffekt für die Cards erzeugen
        Drawable drawable = getResources().getDrawable(android.R.drawable.dialog_holo_light_frame);

        CardView card = view.findViewById(R.id.settings_card_display);
        card.setBackground(drawable);

        card = view.findViewById(R.id.settings_card_network);
        card.setBackground(drawable);

        refreshColorViews(view);
        checkStateChangeAutocheck(view, cb.isChecked());

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
        TextView errorTV = getActivity().findViewById(R.id.settings_color_error);

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

            errorTV.setVisibility(View.GONE);
        }
        else {
            errorTV.setVisibility(View.VISIBLE);
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

    private void checkStateChangeAutocheck(@Nullable View view, boolean checked) {
        CheckBox cb;
        preferencesManager.setCheckForUpdateAllowed(checked);

        if (view == null) {
            cb = getActivity().findViewById(R.id.settings_cb_network_autocheck);
        }
        else {
            cb = view.findViewById(R.id.settings_cb_network_autocheck);
        }

        if (checked) {
            cb.setText(R.string.settings_network_check_enabled);
        }
        else {
            cb.setText(R.string.settings_network_check_disabled);
        }
    }
}
