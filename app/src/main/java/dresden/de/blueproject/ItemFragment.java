package dresden.de.blueproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Das {@link ItemFragment}Fragment zum Darstellen der Fächer in einem vordefinierten Listen Layout
 */
public class ItemFragment extends Fragment {

    private final static String LOG_TAG="ItemFragment_LOG";

    public final static  String BUNDLE_TAG_ITEMS="bundleItems";

    private ItemAdapter itemAdapter;

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
                Log.e(LOG_TAG,"Fehler beim casten des Arguments in eine ArrayList!");
            }

        }
        else {

            equipmentItems = ((MainActivity) getActivity()).getEquipmentList();

        }

        itemAdapter = new ItemAdapter(getActivity(), equipmentItems);

        ListView lv = (ListView) result.findViewById(R.id.ListViewMain);

        lv.setAdapter(itemAdapter);

        return result;

    }

}
