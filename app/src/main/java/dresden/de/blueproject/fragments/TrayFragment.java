package dresden.de.blueproject.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import dresden.de.blueproject.dataStructure.EquipmentItem;
import dresden.de.blueproject.MainActivity;
import dresden.de.blueproject.R;
import dresden.de.blueproject.dataStructure.TrayAdapter;
import dresden.de.blueproject.dataStructure.TrayItem;
import util.Util_Data;

/**
 * Das {@link TrayFragment}Fragment zum Darstellen der Fächer in einem vordefinierten Listen Layout
 */
public class TrayFragment extends Fragment {
    fragmentCallbackListener masterCallback;

    private TrayAdapter trayAdapter;

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

        final ArrayList<TrayItem> trays = ((MainActivity) getActivity()).getTrayList();

        trayAdapter = new TrayAdapter(getActivity(), trays);

        ListView lv = (ListView) result.findViewById(R.id.ListViewMain);

        lv.setAdapter(trayAdapter);


        //ClickListener einrichten

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Clickhandling übernehmen

                TrayItem item = trays.get(position);

                ArrayList<EquipmentItem> mainItems = ((MainActivity) getActivity()).getEquipmentList();

                //Alle zum Behälter zugehörigen Gegenstände finden
                ArrayList<EquipmentItem> foundItems =  Util_Data.findItemsByTray(item,mainItems);

                ItemFragment itemFragment = new ItemFragment();

                Bundle args = new Bundle();
                args.putParcelableArrayList(ItemFragment.BUNDLE_TAG_ITEMS, ((ArrayList)foundItems));

                itemFragment.setArguments(args);

                masterCallback.switchFragment(R.id.MainFrame,itemFragment,MainActivity.FRAGMENT_LIST_ITEM);


            }
        });

        return result;

    }

}
