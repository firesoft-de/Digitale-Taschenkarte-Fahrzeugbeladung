package dresden.de.digitaleTaschenkarteBeladung.fragments;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.ItemViewModel;

/**
 * {@link DetailFragment} bietet eine Detailansicht für ein einzelnes Item
 */
public class DetailFragment extends Fragment {

    ItemViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    EquipmentItem item;

    public DetailFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(ItemViewModel.class);

        if (this.getArguments() != null) {

            Bundle args = this.getArguments();
            int itemID = args.getInt(ItemFragment.BUNDLE_TAG_DETAIL);

            viewModel.getItem(itemID).observe(this, new Observer<EquipmentItem>() {
                @Override
                public void onChanged(@Nullable EquipmentItem equipmentItem) {
                    insertData(equipmentItem);
                }
            });

        }
        else {
            throw new IllegalArgumentException("Keine Argumente weitergegeben!");
        }

    }

    /**
     * Bestückt das Fragment
     * @param item
     */
    private  void insertData(EquipmentItem item) {

        Activity activity = getActivity();

        TextView tvName = activity.findViewById(R.id.detailName);
        TextView tvDescription = activity.findViewById(R.id.detailDescription);
        TextView tvSetName = activity.findViewById(R.id.detailSetName);
        TextView tvPosition = activity.findViewById(R.id.detailPosition);
        TextView tvNotes = activity.findViewById(R.id.detailAdditionalNotes);
        TextView tvSetNameStatic = activity.findViewById(R.id.detailSetNameStatic);
        TextView tvNotesStatic = activity.findViewById(R.id.detailAdditionalNotesStatic);

        tvName.setText(item.getName());
        tvDescription.setText(item.getDescription());

        if (item.getMSetName() != ""  &&  item.getMSetName() != null) {
            tvSetName.setText(item.getMSetName());
            tvSetName.setVisibility(View.VISIBLE);
            tvSetNameStatic.setVisibility(View.VISIBLE);}
        else {
            tvSetName.setVisibility(View.GONE);
            tvSetNameStatic.setVisibility(View.GONE);
        }

        if (item.getAdditionalNotes() != "" &&  item.getAdditionalNotes() != null) {
            tvNotes.setText(item.getMSetName());
            tvNotes.setVisibility(View.VISIBLE);
            tvNotesStatic.setVisibility(View.VISIBLE);}
        else {
            tvNotes.setVisibility(View.GONE);
            tvNotesStatic.setVisibility(View.GONE);
        }

        String position = item.getPosition();

        String[] positionParts = position.split(" - ");

        tvPosition.setText(positionParts[0]);

        for (int i = 1; i< positionParts.length; i++) {

            tvPosition.append("\n\n" + positionParts[i]);

        }

    }

}
