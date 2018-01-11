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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseEquipmentMininmal;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.dataStructure.ItemAdapter;
import dresden.de.digitaleTaschenkarteBeladung.util.Util;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.ItemViewModel;

/**
 * Das {@link ItemFragment}Fragment zum Darstellen der Fächer in einem vordefinierten Listen Layout
 */
public class ItemFragment extends Fragment {
    TrayFragment.fragmentCallbackListener masterCallback;

    private final static String LOG_TAG="ItemFragment_LOG";

    public final static  String BUNDLE_TAG_ITEMS="bundleItems";
    public final static  String BUNDLE_TAG_DETAIL="bundleDetail";

    private ItemAdapter itemAdapter;

    private ArrayList<DatabaseEquipmentMininmal> itemList;

    //Diese Variable wird zum Speichern des Scroll Index der ListView gebraucht
    Parcelable state;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ItemViewModel itemViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Anweisung an Dagger, dass hier eine Injection vorgenommen wird ??
        ((ApplicationForDagger) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Hier wird das Viewmodel erstellt und durch die Factory mit Eigenschaften versehen
        itemViewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(ItemViewModel.class);

        if (this.getArguments() != null) {

            Bundle args = this.getArguments();

            int catID =  args.getInt(BUNDLE_TAG_ITEMS);
            //Observer einrichten
            itemViewModel.getItemsByCatID(catID).observe(this, new Observer<List<DatabaseEquipmentMininmal>>() {
                @Override
                public void onChanged(@Nullable List<DatabaseEquipmentMininmal> items) {
//                    if (ItemFragment.this.itemList == null) {
                        insertData(null,items);
//                    }
                }
            });

        }
        else {
            throw new IllegalArgumentException("Keine Behälter-ID angegeben!");
        }

        MainActivity activity = (MainActivity) getActivity();
        activity.liveSort.observe(this, new Observer<Util.Sort>() {
            @Override
            public void onChanged(@Nullable Util.Sort sort) {
                changeSorting(sort);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.list_layout,parent,false);

        return result;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            masterCallback = (TrayFragment.fragmentCallbackListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onDetach() {
        itemList.clear();
        itemAdapter.clear();
        super.onDetach();
    }

    @Override
    public void onPause() {
        ListView lv = getActivity().findViewById(R.id.ListViewMain);

        state = lv.onSaveInstanceState();
        super.onPause();
    }

    private void insertData(@Nullable ArrayList<EquipmentItem> equipmentItems, @Nullable List<DatabaseEquipmentMininmal> minimalItem) {

        if (equipmentItems == null && minimalItem != null) {
            itemList = (ArrayList<DatabaseEquipmentMininmal>) minimalItem;

        }
        else {
            throw new IllegalArgumentException("Es darf nur ein Argument der Methode insertData null sein!");
        }

        changeSorting(((MainActivity) getActivity()).liveSort.getValue());

        //Click Listener für die ListViewItems setzen um Details anzeigen zu können

        ListView lv = (ListView) getActivity().findViewById(R.id.ListViewMain);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                DatabaseEquipmentMininmal item = itemList.get(i);

                DetailFragment detailFragment = new DetailFragment();
                Bundle bundle = new Bundle();

                bundle.putInt(BUNDLE_TAG_DETAIL, item.getId());

                detailFragment.setArguments(bundle);

                masterCallback.switchFragment(R.id.MainFrame,detailFragment, Util.FRAGMENT_DETAIL);

            }
        });

        // Restore previous state (including selected item index and scroll position)
        // https://stackoverflow.com/questions/3014089/maintain-save-restore-scroll-position-when-returning-to-a-listview/5688490#5688490
        if(state != null) {
            lv.onRestoreInstanceState(state);
        }

    }

    private void setData(List<DatabaseEquipmentMininmal> items, @Nullable ListView lv) {

        itemList = (ArrayList<DatabaseEquipmentMininmal>) items;

        itemAdapter = new ItemAdapter(getActivity(),(ArrayList) items);

        if (lv == null) {
            lv = (ListView) getActivity().findViewById(R.id.ListViewMain);
        }

        lv.setAdapter(itemAdapter);
    }

    /**
     * Sortiert die Liste entsprechend des Nutzerwunsches
     * @param sort
     */
    private void changeSorting(Util.Sort sort) {

        if (itemList != null) {

            switch (sort) {

                case AZ:
                    Collections.sort(itemList, new Comparator<DatabaseEquipmentMininmal>() {
                        @Override
                        public int compare(DatabaseEquipmentMininmal o1, DatabaseEquipmentMininmal o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    setData(itemList, null);
                    break;

                case ZA:
                    Collections.sort(itemList, new Comparator<DatabaseEquipmentMininmal>() {
                        @Override
                        public int compare(DatabaseEquipmentMininmal o1, DatabaseEquipmentMininmal o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    Collections.reverse(itemList);
                    setData(itemList, null);
                    break;

                default:
                    //Entspricht PRESET
                    Collections.sort(itemList, new Comparator<DatabaseEquipmentMininmal>() {
                        @Override
                        public int compare(DatabaseEquipmentMininmal o1, DatabaseEquipmentMininmal o2) {
                            Integer x1 = o1.getId();
                            Integer x2 = o2.getId();
                            return x1.compareTo(x2);
                        }
                    });
                    setData(itemList, null);
                    break;
            }
        }
    }

}
