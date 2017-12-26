package dresden.de.digitaleTaschenkarteBeladung.fragments;


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

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.dataStructure.TrayAdapter;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.TrayViewModel;

/**
 * Das {@link TrayFragment}Fragment zum Darstellen der Fächer in einem vordefinierten Listen Layout
 */
public class TrayFragment extends Fragment {
    fragmentCallbackListener masterCallback;

    private TrayAdapter trayAdapter;

    private TrayViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ArrayList<TrayItem> trays;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Anweisung an Dagger, dass hier eine Injection vorgenommen wird ??
        ((ApplicationForDagger) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

    }

    //Interfacedefinition um schneller mit der Mainactivity zu kommunizieren
    public  interface fragmentCallbackListener {
        void switchFragment(int id, Fragment fragment, String tag);

        //void sendToFragment(int fragmentID, Object message);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            masterCallback = (fragmentCallbackListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.list_layout,parent,false);

        viewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(TrayViewModel.class);

        viewModel.getTrays().observe(this, new Observer<List<TrayItem>>() {
            @Override
            public void onChanged(@Nullable List<TrayItem> trayItems) {
                setData(trayItems);
            }
        });

        ListView lv = (ListView) result.findViewById(R.id.ListViewMain);

        //ClickListener einrichten
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Clickhandling übernehmen

                TrayItem item = trays.get(position);

//                ArrayList<EquipmentItem> mainItems = ((MainActivity) getActivity()).getEquipmentList();

                //Alle zum Behälter zugehörigen Gegenstände finden
//                ArrayList<EquipmentItem> foundItems =  Util_Data.findItemsByTray(item,mainItems);

                ItemFragment itemFragment = new ItemFragment();

                Bundle args = new Bundle();
                args.putInt(ItemFragment.BUNDLE_TAG_ITEMS, item.getId());

                itemFragment.setArguments(args);

                masterCallback.switchFragment(R.id.MainFrame,itemFragment,MainActivity.FRAGMENT_LIST_ITEM);


            }
        });

        return result;

    }

    private void setData(List<TrayItem> trayItems) {

        trays = (ArrayList<TrayItem>) trayItems;

        trayAdapter = new TrayAdapter(getActivity(), trays);

        ListView lv = (ListView) getActivity().findViewById(R.id.ListViewMain);

        lv.setAdapter(trayAdapter);

    }

}
