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
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.SearchableActivity;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseEquipmentMininmal;
import dresden.de.digitaleTaschenkarteBeladung.dataStructure.ItemAdapter;
import dresden.de.digitaleTaschenkarteBeladung.util.Util;

import static dresden.de.digitaleTaschenkarteBeladung.fragments.ItemFragment.BUNDLE_TAG_DETAIL;


public class SearchResultListFragment extends Fragment {

    TrayFragment.fragmentCallbackListener masterCallback;

    ArrayList<DatabaseEquipmentMininmal> itemList;

    //Diese Variable wird zum Speichern des Scroll Index der ListView gebraucht
    Parcelable state;


    public SearchResultListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_search_result_list, container, false);

        ListView lv = result.findViewById(R.id.ListViewSearch);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Detailansicht anzeigen

                //TODO: Detailansicht aktivieren
                showDetail(i);
            }
        });

        SearchableActivity activity = (SearchableActivity) getActivity();
        //Sollte die itemList irgendwo mal null gesetzt worden sein, dann wird jetzt aus der liveItemList ein Wert abgerufen
        if (itemList != null && itemList.size() == 0) {
            try {
                itemList = (ArrayList<DatabaseEquipmentMininmal>) activity.liveItemList.getValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (itemList == null) {
            itemList = new ArrayList<>();
        }
        else if (itemList.size() != 0) {
            refreshData(result);
        }

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SearchableActivity activity = (SearchableActivity) getActivity();

        //Beobachtung der liveItemList aktivieren um mitzubekommen, wenn die SQL Query ein Ergebnis generiert
        activity.liveItemList.observe(this, new Observer<List<DatabaseEquipmentMininmal>>() {
            @Override
            public void onChanged(@Nullable List<DatabaseEquipmentMininmal> databaseEquipmentMininmals) {
                presentData(databaseEquipmentMininmals);
            }
        });

        //Callback attachen
        try {
            masterCallback = (TrayFragment.fragmentCallbackListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        itemList.clear();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        ListView lv = getActivity().findViewById(R.id.ListViewSearch);

        state = lv.onSaveInstanceState();
        super.onPause();
    }

    private void showDetail(int i) {

        DatabaseEquipmentMininmal item = itemList.get(i);

        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();

        bundle.putInt(BUNDLE_TAG_DETAIL, item.getId());

        detailFragment.setArguments(bundle);

        masterCallback.switchFragment(R.id.searchFrame,detailFragment, Util.FRAGMENT_DETAIL);

    }

    private void presentData(List<DatabaseEquipmentMininmal> result) {
        for (int i = 0; i<result.size(); i++) {
            DatabaseEquipmentMininmal item = result.get(i);
            if (!itemList.contains(item)) {
                itemList.add(item);
            }
        }

        ItemAdapter searchAdapter = new ItemAdapter(getActivity(),(ArrayList<DatabaseEquipmentMininmal>) itemList);

        ListView lv = (ListView) getActivity().findViewById(R.id.ListViewSearch);

        lv.setAdapter(searchAdapter);

        searchAdapter.notifyDataSetChanged();
    }

    private void refreshData(View view) {
        ItemAdapter searchAdapter = new ItemAdapter(getActivity(),(ArrayList<DatabaseEquipmentMininmal>) itemList);

        ListView lv = (ListView) view.findViewById(R.id.ListViewSearch);

        lv.setAdapter(searchAdapter);

        searchAdapter.notifyDataSetChanged();

        // Restore previous state (including selected item index and scroll position)
        // https://stackoverflow.com/questions/3014089/maintain-save-restore-scroll-position-when-returning-to-a-listview/5688490#5688490
        if(state != null) {
            lv.onRestoreInstanceState(state);
        }

    }

}
