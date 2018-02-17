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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import dresden.de.digitaleTaschenkarteBeladung.R;

/**
 * Diese Klasse erzeugt zusammen mit dem group_selector.xml Layout eine Auswahloption für die Gruppen.
 * Diese werden im Datenimportfragment angezeigt
 */
public class ViewGroupSelector extends View{

    View ownView;

    public ViewGroupSelector(LayoutInflater inflater, Context context, ViewGroup viewGroup) {
        super(context);

        ownView = inflater.inflate(R.layout.group_selector,viewGroup,false);
    }

    public ViewGroupSelector(View view, Context context) {
        super(context);

        ownView = view;
    }

    public View getOwnView() {
        return ownView;
    }

    public void setGroupName(String name) {
        CheckBox tv = ownView.findViewById(R.id.group_selector);
        tv.setText(name);
    }

    public String getGroupName() {
        CheckBox tv = ownView.findViewById(R.id.group_selector);
        return tv.getText().toString();
    }

    public boolean getCheckState() {
        CheckBox tv = ownView.findViewById(R.id.group_selector);
        return tv.isChecked();
    }

    public void setCheckState(boolean checkState) {
        CheckBox tv = ownView.findViewById(R.id.group_selector);
        tv.setChecked(checkState);
    }

    /**
     * Ändert den Anzeigestatus des Passwortfeldes
     * @param viewState 0 = VISIBLE, 4 = INVISIBLE, 8 = GONE
     */
    public void setPasswordFieldVisibility(int viewState) {
        EditText et = ownView.findViewById(R.id.group_password);
        et.setVisibility(viewState);
    }
}
