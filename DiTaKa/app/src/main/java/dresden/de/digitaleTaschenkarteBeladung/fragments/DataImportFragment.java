package dresden.de.digitaleTaschenkarteBeladung.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.ItemLoader;
import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.TrayLoader;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.DataFragViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataImportFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private static final String LOG_TAG="DataImportFragment_LOG";

    private static final String PREFS_NAME="dresden.de.digitaleTaschenkarteBeladung";
    private static final String PREFS_URL="dresden.de.digitaleTaschenkarteBeladung.url";
    private static final String PREFS_DBVERSION="dresden.de.digitaleTaschenkarteBeladung.dbversion";

    private static final String ARGS_URL="ARGS_URL";
    private static final String ARGS_VERSION="ARGS_VERSION";

    private static final int ITEM_LOADER = 1;
    private static final int TRAY_LOADER = 2;

    //Datenbankversion
    private int dbversion;
    private String url;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    DataFragViewModel viewModel;

    SharedPreferences settings;

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

        settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_data, container, false);

        //Hier wird das Viewmodel erstellt und durch die Factory mit Eigenschaften versehen
        viewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(DataFragViewModel.class);

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

        //URL aus Preference abrufen
        url = settings.getString(PREFS_URL,"");

        EditText editText = result.findViewById(R.id.text_url);
        editText.setText(url);

        //Datenbankinformaitonen laden
        viewModel.countItems().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                TextView tv =  getActivity().findViewById(R.id.dataTextViewItemCount);
                tv.setText(Integer.toString(integer));
            }
        });

        viewModel.countTrays().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                TextView tv =  getActivity().findViewById(R.id.dataTextViewTrayCount);
                tv.setText(Integer.toString(integer));
            }
        });

        //URL aus Preference abrufen
        dbversion = settings.getInt(PREFS_DBVERSION,0);

        TextView tvDBVersion = result.findViewById(R.id.dataTextViewDBVersion);
        tvDBVersion.setText(Integer.toString(dbversion));

        // Inflate the layout for this fragment
        return result;
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String url = args.getString(ARGS_URL);
        Integer version = args.getInt(ARGS_VERSION);

        switch (id) {
            case 1:
                //ID 1: Ein neuer ItemLoader wird gebraucht!
                return new ItemLoader(getContext(),url,version);

            case 2:
                //ID 2: Ein neuer TrayLoader wird gebraucht!
                return new TrayLoader(getContext(),url,version);

            default:
                //Irgendwas ist schief gegangen -> Falsche ID
                Log.e(LOG_TAG, "Fehler beim starten des Loaders! Konnte keine zu einem Loader passende ID finden!");
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader loader, Object data) {
        //Die Loader haben ihre Arbeit abgeschlossen
        //Runtergeladene Daten in die Datenbank schreiben
        switch (loader.getId()) {
            case ITEM_LOADER:
                viewModel.addItems((ArrayList<EquipmentItem>) data);
                break;

            case TRAY_LOADER:
                viewModel.addTrays((ArrayList<TrayItem>) data);
                break;
        }

        //Datenbankversion aktualiseren
        //TODO: Datenbankversion aktualiseren

    }

    @Override
    public void onLoaderReset(Loader loader) {
        //TODO: Hier die Dinge zurücksetzen die zurückgesetzt werden müssen

    }

    public void buttonAddClick() {
        //Ablauf
        //1. Netzwerküberprüfung
        //2. Versionsabfrage
        //3. -> Neue Version, dann Daten abfragen und verarbeiten
        //Ansonsten Ende


        //Netzwerkstatus überpüfen

        if (Util_Http.checkNetwork(getActivity(),getContext())) {
            //Netzwerkverbindung i.O.

            //Loader initialiseren
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).

            //TODO: DB Version abfragen -> Prüfen ob Download notwendig

            int netVersion = 1;

            if (dbversion < netVersion) {

                //Parameter für die Loader fertig machen
                EditText editText = getActivity().findViewById(R.id.text_url);
                SharedPreferences.Editor editor = settings.edit();
                url = editText.getText().toString();

                if (!url.contains("http://")) {
                    url = "http://" + url;
                }

                editor.putString(PREFS_URL, url);
                editor.apply();

                publishProgress(50);

                Bundle args = new Bundle();
                args.putString(ARGS_URL, url);
                args.putInt(ARGS_VERSION, dbversion);

                //Loader anwerfen TODO: TrayLoader implementieren und aktivieren
                loaderManager.initLoader(ITEM_LOADER, args, this);
       //         loaderManager.initLoader(TRAY_LOADER, args, this);

            }
            else {
                Toast.makeText(getContext(),R.string.app_nodbRefresh,Toast.LENGTH_LONG).show();
            }
        }
        else {
            //Keine Netzwerkverbindung -> Nachricht und Ende
            Toast.makeText(getContext(),R.string.app_noConnection,Toast.LENGTH_LONG).show();
        }
    }

    private void publishProgress(int progress) {
        ProgressBar progressBar = getActivity().findViewById(R.id.DataProgress);
        progressBar.setProgress(progress);
    }

}

