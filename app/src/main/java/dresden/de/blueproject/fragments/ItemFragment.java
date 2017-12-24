package dresden.de.blueproject.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dresden.de.blueproject.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.blueproject.data.DatabaseEquipmentObject;
import dresden.de.blueproject.dataStructure.EquipmentItem;
import dresden.de.blueproject.dataStructure.ItemAdapter;
import dresden.de.blueproject.MainActivity;
import dresden.de.blueproject.R;
import dresden.de.blueproject.viewmodels.ItemViewModel;

/**
 * Das {@link ItemFragment}Fragment zum Darstellen der Fächer in einem vordefinierten Listen Layout
 */
public class ItemFragment extends Fragment {

    private final static String LOG_TAG="ItemFragment_LOG";

    public final static  String BUNDLE_TAG_ITEMS="bundleItems";

    private ItemAdapter itemAdapter;

    private List<DatabaseEquipmentObject> itemList;

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

        //Observer einrichten
        itemViewModel.getItems().observe(this, new Observer<List<DatabaseEquipmentObject>>() {
            @Override
            public void onChanged(@Nullable List<DatabaseEquipmentObject> databaseEquipmentObjects) {
                if (ItemFragment.this.itemList == null) {

                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.list_layout,parent,false);

        ArrayList<EquipmentItem> equipmentItems = new ArrayList<>();

        if (this.getArguments() != null) {

            Bundle args = this.getArguments();

            try {
                equipmentItems = (ArrayList<EquipmentItem>) args.get(BUNDLE_TAG_ITEMS);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOG_TAG,"Fehler beim Casten des Arguments in eine ArrayList!");
            }

        }
        else {

            equipmentItems = ((MainActivity) getActivity()).getEquipmentList();

        }



        return result;

    }

    private void insertData(ArrayList<EquipmentItem> equipmentItems) {

        itemAdapter = new ItemAdapter(this.getActivity(), equipmentItems);

        ListView lv = (ListView) getActivity().findViewById(R.id.ListViewMain);

        lv.setAdapter(itemAdapter);

    }

}
