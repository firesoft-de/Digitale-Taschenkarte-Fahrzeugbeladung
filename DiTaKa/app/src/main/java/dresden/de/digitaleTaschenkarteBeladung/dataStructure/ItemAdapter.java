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
import android.content.Context;
import android.os.Trace;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseEquipmentMininmal;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;

public class ItemAdapter extends ArrayAdapter<DatabaseEquipmentMininmal> {

    private static  final String LOG_TAG="ItemAdapter_LOG";


    public ItemAdapter(Activity context, ArrayList<DatabaseEquipmentMininmal> items) {

        super(context,0,items);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {

        Trace.beginSection("itemAdapter getView");

        Trace.beginSection("inflating View");
        if (view == null) {
            LayoutInflater inflater =LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.list_item, parent, false);
        }
        Trace.endSection();

        Trace.beginSection("Call ItemByPosition");
        DatabaseEquipmentMininmal currentItem = getItem(position);
        Trace.endSection();


        Trace.beginSection("Set Data");
        TextView tvItemName = (TextView) view.findViewById(R.id.listItem_tv_name);

        tvItemName.setText(currentItem.name);

        TextView tvItemPosition = (TextView) view.findViewById(R.id.listItem_tv_position);
        tvItemPosition.setText(currentItem.position);

        Trace.endSection();

//        //ImageView ausblenden
//        ImageView previewImage = (ImageView) listItemView.findViewById(R.id.imageView);
//        previewImage.setVisibility(View.GONE);

        Trace.endSection();

        return view;

    }


}
