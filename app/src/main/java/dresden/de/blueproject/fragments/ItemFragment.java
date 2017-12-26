package dresden.de.blueproject.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dresden.de.blueproject.MainActivity;
import dresden.de.blueproject.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.blueproject.dataStructure.DatabaseEquipmentMininmal;
import dresden.de.blueproject.data.EquipmentItem;
import dresden.de.blueproject.dataStructure.ItemAdapter;
import dresden.de.blueproject.R;
import dresden.de.blueproject.viewmodels.ItemViewModel;

/**
 * Das {@link ItemFragment}Fragment zum Darstellen der Fächer in einem vordefinierten Listen Layout
 */
public class ItemFragment extends Fragment {
    TrayFragment.fragmentCallbackListener masterCallback;

    private final static String LOG_TAG="ItemFragment_LOG";

    public final static  String BUNDLE_TAG_ITEMS="bundleItems";
    public final static  String BUNDLE_TAG_DETAIL="bundleDetail";

    private ItemAdapter itemAdapter;

    private ArrayList<EquipmentItem> itemList;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.list_layout,parent,false);

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

    private void insertData(@Nullable ArrayList<EquipmentItem> equipmentItems, @Nullable List<DatabaseEquipmentMininmal> minimalItem) {

        if (equipmentItems == null && minimalItem != null) {
            //minimal zu equipment Item konvertieren
            equipmentItems = new ArrayList<>();

            itemList = new ArrayList<>();

            for (int i = 0; i < minimalItem.size(); i++) {
                EquipmentItem item = new EquipmentItem();
                item.fromMinimal(minimalItem.get(i));
                itemList.add(item);
                equipmentItems.add(item);
            }
        }
//        else if (equipmentItems != null && minimalItem == null) {
//
//        }
        else {
            throw new IllegalArgumentException("Es darf nur ein Argument der Methode insertData null sein!");
        }

        itemAdapter = new ItemAdapter(this.getActivity(), equipmentItems);

        ListView lv = getActivity().findViewById(R.id.ListViewMain);

        lv.setAdapter(itemAdapter);

        //Click Listener für die ListViewItems setzen um Details anzeigen zu können
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                EquipmentItem item = itemList.get(i);

                DetailFragment detailFragment = new DetailFragment();
                Bundle bundle = new Bundle();

                bundle.putInt(BUNDLE_TAG_DETAIL, item.getId());

                detailFragment.setArguments(bundle);

                masterCallback.switchFragment(R.id.MainFrame,detailFragment, MainActivity.FRAGMENT_DETAIL);

            }
        });

    }

}
