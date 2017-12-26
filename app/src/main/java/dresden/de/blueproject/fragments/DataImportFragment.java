package dresden.de.blueproject.fragments;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.ArrayList;

import javax.inject.Inject;

import dresden.de.blueproject.ItemLoader;
import dresden.de.blueproject.R;
import dresden.de.blueproject.TrayLoader;
import dresden.de.blueproject.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.blueproject.data.EquipmentItem;
import dresden.de.blueproject.viewmodels.DataFragViewModel;
import dresden.de.blueproject.util.Util_ExampleData;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataImportFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private static  final String LOG_TAG="DataImportFragment_LOG";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    DataFragViewModel viewModel;

    public DataImportFragment() {
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
        //Hier wird das Viewmodel erstellt und durch die Factory mit Eigenschaften versehen
        viewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(DataFragViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_data, container, false);

        //Den Backbutton in der Actionbar hinzufügen
/*        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.fragment_title_data);


        //ClickListener für das Hinzufügen der Daten einbauen

        Button dataButton = result.findViewById(R.id.DataButton);
        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAddClick();
            }
        });

        ProgressBar progressBar = result.findViewById(R.id.DataProgress);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        // Inflate the layout for this fragment
        return result;
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        switch (id) {
            case 1:
                //ID 1: Ein neuer ItemLoader wird gebraucht!
                return new ItemLoader(getContext());

            case 2:
                //ID 2: Ein neuer TrayLoader wird gebraucht!
                return new TrayLoader(getContext());

            default:
                //Irgendwas ist schief gegangen -> Falsche ID
                Log.e(LOG_TAG, "Fehler beim starten des Loaders! Konnte keine zu einem Loader passende ID finden!");
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader loader, Object data) {
        //TODO: Implementieren was passiert wenn der Loader fertig geworden ist! -> Schreiben der Daten in eine Datenbank und weiterreichen an die Liste im RAM
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //TODO: Hier die Dinge zurücksetzen die zurückgesetzt werden müssen
    }

    public void buttonAddClick() {

        publishProgress(50);

    }

    private void publishProgress(int progress) {
        ProgressBar progressBar = getActivity().findViewById(R.id.DataProgress);
        progressBar.setProgress(progress);
    }

}

