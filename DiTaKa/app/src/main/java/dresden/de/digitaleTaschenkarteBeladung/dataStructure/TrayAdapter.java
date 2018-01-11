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

package dresden.de.digitaleTaschenkarteBeladung.dataStructure;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;

public class TrayAdapter extends ArrayAdapter<TrayItem> {


    private static  final String LOG_TAG="TrayAdapter_LOG";

    public TrayAdapter(Activity context, ArrayList<TrayItem> items) {

        super(context,0,items);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TrayItem currentItem = getItem(position);

        TextView tvItemName = (TextView) listItemView.findViewById(R.id.listItem_tv_name);

        tvItemName.setText(currentItem.getName());

        TextView tvItemPosition = (TextView) listItemView.findViewById(R.id.listItem_tv_position);
        tvItemPosition.setText(currentItem.getDescription());

        //ImageView ausblenden
        ImageView previewImage = (ImageView) listItemView.findViewById(R.id.imageView);
        previewImage.setVisibility(View.GONE);

        //Beim ersten Start soll das Weiter Icon ausgeblendet werden

        if (currentItem.getId() == -1) {
            ImageView continueImage = (ImageView) listItemView.findViewById(R.id.imageView2);
            continueImage.setVisibility(View.GONE);
        }

        return listItemView;
    }
}
