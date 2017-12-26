package dresden.de.digitaleTaschenkarteBeladung.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.DebugViewModel;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_ExampleData;


/**
 * {@link DebugFragment} bietet verschiedene Optionen für das Debugging.
 */
public class DebugFragment extends Fragment {

    private final static String LOG_TAG="DebugFragment_LOG";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    DebugViewModel debugViewModel;

    public DebugFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Anweisung an Dagger, dass hier eine Injection vorgenommen wird ??
        ((ApplicationForDagger) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        debugViewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(DebugViewModel.class);

        final TextView tvCountItem = getActivity().findViewById(R.id.debug_db_info_item);

        debugViewModel.countItems().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvCountItem.setText(integer.toString());
            }
        });

        final TextView tvCountTray = getActivity().findViewById(R.id.debug_db_info_tray);

        debugViewModel.countTrays().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tvCountTray.setText(integer.toString());
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_debug, container, false);

        //ClickListener einbauen
        Button btDeleteItems = result.findViewById(R.id.btDeleteItem);
        btDeleteItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debugViewModel.deleteAllItem();
            }
        });

        Button btAddItems = result.findViewById(R.id.btAddDummyItem);
        btAddItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<EquipmentItem> items = Util_ExampleData.dummyDataEquipment();
                debugViewModel.addItems(items);
            }
        });

        Button btAddTrays = result.findViewById(R.id.btAddDummyTray);
        btAddTrays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<TrayItem> items = Util_ExampleData.dummyDataTray();
                debugViewModel.addTrays(items);
            }
        });

        Button btDeleteTray = result.findViewById(R.id.btDeleteTray);
        btDeleteTray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debugViewModel.deleteTrays();
            }
        });

        return result;
    }
}