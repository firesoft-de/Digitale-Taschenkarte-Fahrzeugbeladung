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
import java.util.List;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseEquipmentMininmal;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.dataStructure.ItemAdapter;
import dresden.de.digitaleTaschenkarteBeladung.R;
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

    private void insertData(@Nullable ArrayList<EquipmentItem> equipmentItems, @Nullable List<DatabaseEquipmentMininmal> minimalItem) {

        if (equipmentItems == null && minimalItem != null) {
            itemList = (ArrayList<DatabaseEquipmentMininmal>) minimalItem;

        }
        else {
            throw new IllegalArgumentException("Es darf nur ein Argument der Methode insertData null sein!");
        }

        itemAdapter = new ItemAdapter(this.getActivity(), (ArrayList) minimalItem);

        ListView lv = getActivity().findViewById(R.id.ListViewMain);

        lv.setAdapter(itemAdapter);

        //Click Listener für die ListViewItems setzen um Details anzeigen zu können
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

    @Override
    public void onPause() {
        ListView lv = getActivity().findViewById(R.id.ListViewMain);

        state = lv.onSaveInstanceState();
        super.onPause();
    }
}
