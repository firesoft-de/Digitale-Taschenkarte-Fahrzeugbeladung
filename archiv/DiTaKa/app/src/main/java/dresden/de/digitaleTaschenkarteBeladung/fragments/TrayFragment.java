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
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.CustomApplication;
import dresden.de.digitaleTaschenkarteBeladung.dataStructure.TrayAdapter;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.GroupManager;
import dresden.de.digitaleTaschenkarteBeladung.util.Util;
import dresden.de.digitaleTaschenkarteBeladung.util.VariableManager;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.TrayViewModel;

/**
 * Das {@link TrayFragment}Fragment zum Darstellen der Fächer in einem vordefinierten Listen Layout
 */
public class TrayFragment extends Fragment {
    IFragmentCallbacks caller;

    private TrayAdapter trayAdapter;

    private TrayViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    VariableManager vManager;

    @Inject
    GroupManager gManager;

    ArrayList<TrayItem> trays;

    //=======================================================
    //===================OVERRIDEMETHODEN====================
    //=======================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Anweisung an Dagger, dass hier eine Injection vorgenommen wird ??
        ((CustomApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            caller = (IFragmentCallbacks) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle args) {

        View result = inflater.inflate(R.layout.list_layout,parent,false);

        viewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(TrayViewModel.class);

        final ListView lv = result.findViewById(R.id.ListViewMain);

        if (vManager.dbState == Util.DbState.VALID || vManager.dbState == Util.DbState.EXPIRED) {
            viewModel.getTrays(gManager.getActiveGroup().getName()).observe(this, new Observer<List<TrayItem>>() {
                @Override
                public void onChanged(@Nullable List<TrayItem> trayItems) {
                    trays = (ArrayList<TrayItem>) trayItems;
                    changeSorting(vManager.liveSort.getValue());
                }
            });

            //ClickListener einrichten
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    //Clickhandling übernehmen
                    TrayItem item = trays.get(position);

                    Bundle args = new Bundle();
                    args.putInt(ItemFragment.BUNDLE_TAG_ITEMS, item.getId());

                    ItemFragment itemFragment = new ItemFragment();
                    itemFragment.setArguments(args);

                    caller.switchFragment(R.id.MainFrame,itemFragment, Util.FRAGMENT_LIST_ITEM);
                }
            });
        }
        else {
            displayFirstRun(lv);
        }

        vManager.liveSort.observe(this, new Observer<Util.Sort>() {
            @Override
            public void onChanged(@Nullable Util.Sort sort) {
                changeSorting(sort);
            }
        });

        return result;

    }

    //=======================================================
    //====================KLASSENMETHODEN====================
    //=======================================================

    private void setData(List<TrayItem> trayItems, @Nullable ListView lv) {

        trays = (ArrayList<TrayItem>) trayItems;

        trayAdapter = new TrayAdapter(getActivity(), trays);

        if (lv == null) {
            lv = (ListView) getActivity().findViewById(R.id.ListViewMain);
        }

        lv.setAdapter(trayAdapter);
    }

    private void displayFirstRun(ListView lv) {
        TrayItem trayItem;
        ArrayList<TrayItem> list = new ArrayList<>();

        trayItem = new TrayItem(-1,
                "Erster Start",
                "Bitte starte über das Optionsmenü den Datenimport.");


        list.add(trayItem);
        setData(list,lv);

        final Snackbar bar;

        bar = Snackbar.make(getActivity().findViewById(R.id.MainFrame), R.string.app_license,Snackbar.LENGTH_INDEFINITE);
        bar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.dismiss();
            }
        });
        bar.show();

    }

    /**
     * Sortiert die Liste entsprechend des Nutzerwunsches
     * @param sort
     */
    private void changeSorting(Util.Sort sort) {

        if (trays != null) {

            switch (sort) {

                case AZ:
                    Collections.sort(trays, new Comparator<TrayItem>() {
                        @Override
                        public int compare(TrayItem o1, TrayItem o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    setData(trays, null);
                    break;

                case ZA:
                    Collections.sort(trays, new Comparator<TrayItem>() {
                        @Override
                        public int compare(TrayItem o1, TrayItem o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    Collections.reverse(trays);
                    setData(trays, null);
                    break;

                default:
                    //Entspricht PRESET
                    Collections.sort(trays, new Comparator<TrayItem>() {
                        @Override
                        public int compare(TrayItem o1, TrayItem o2) {
                            Integer x1 = o1.getId();
                            Integer x2 = o2.getId();
                            return x1.compareTo(x2);
                        }
                    });
                    setData(trays, null);
                    break;
            }
        }
    }

    public void changeGroup(String activeGroup) {
        viewModel.getTrays(activeGroup).observe(this, new Observer<List<TrayItem>>() {
            @Override
            public void onChanged(@Nullable List<TrayItem> trayItems) {
                trays = (ArrayList<TrayItem>) trayItems;
                changeSorting(vManager.liveSort.getValue());
            }
        });
    }

}
