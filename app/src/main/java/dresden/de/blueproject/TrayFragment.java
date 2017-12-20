package dresden.de.blueproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Das {@link TrayFragment}Fragment zum Darstellen der Fächer in einem vordefinierten Listen Layout
 */
public class TrayFragment extends Fragment {


//    public FachBeladung() {
//        // Required empty public constructor
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_layout,parent,false);
    }

}
