package dresden.de.digitaleTaschenkarteBeladung.fragments;


import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.ImageLoader;
import dresden.de.digitaleTaschenkarteBeladung.util.ItemLoader;
import dresden.de.digitaleTaschenkarteBeladung.util.TrayLoader;
import dresden.de.digitaleTaschenkarteBeladung.util.Util;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.DataFragViewModel;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util.LogError;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ConstantConditions")
public class DataImportFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private static final String LOG_TAG="DataImportFragment_LOG";

    private static final int ITEM_LOADER = 1;
    private static final int TRAY_LOADER = 2;
    private static final int IMAGE_LOADER = 3;

    //Datenbankversion
    private int dbversion;
    private String url;

    private boolean initLoaderAfterNetVersionRefresh;

    private int downloadsCompleted;

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

        downloadsCompleted = 0;

    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View result = inflater.inflate(R.layout.fragment_data, container, false);

        initLoaderAfterNetVersionRefresh = false;

        //Hier wird das Viewmodel erstellt und durch die Factory mit Eigenschaften versehen
        viewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(DataFragViewModel.class);


        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.fragment_title_data);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        url = this.getArguments().getString(Util.ARGS_URL);
        dbversion = this.getArguments().getInt(Util.ARGS_VERSION);

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

        final EditText editText = result.findViewById(R.id.text_url);

        if (url != "NO_URL_FOUND") {
            editText.setText(url);
        }
        else {
            editText.setText("Gib hier die Server-URL ein!");
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    EditText editTexter = (EditText) v;
                    if (url == "NO_URL_FOUND") {
                        editTexter.setText("");
                    }
                }
            });
        }

        //Die Observer initalisieren um die Datenbankinformationen anzuzeigen
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

        viewModel.countImages().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                TextView tv =  getActivity().findViewById(R.id.dataTextViewImageCount);
                tv.setText(Integer.toString(integer));
            }
        });

        MainActivity activity = (MainActivity) getActivity();

//        TextView tvNetDBVersion = result.findViewById(R.id.dataTextViewDBNetVersion);

        activity.liveNetDBVersion.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (!initLoaderAfterNetVersionRefresh) {
                    updateNetVersion(integer);
                }
                else {
                    updateNetVersion(integer);
                  initateLoader(integer);
                }
            }
        });

        updateDBVersion(dbversion, result);

        // Inflate the layout for this fragment
        return result;
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        String url = args.getString(Util.ARGS_URL);
        Integer version = args.getInt(Util.ARGS_VERSION);

        switch (id) {
            case ITEM_LOADER:
                //ID 1: Ein neuer ItemLoader wird gebraucht!
                return new ItemLoader(getContext(),url,version);

            case TRAY_LOADER:
                //ID 2: Ein neuer TrayLoader wird gebraucht!
                return new TrayLoader(getContext(),url,version);

            case IMAGE_LOADER:
                //ID 3: Ein neuer ImageLoader wird gebraucht!
                return new ImageLoader(getContext(),url,version);

            default:
                //Irgendwas ist schief gegangen -> Falsche ID
                LogError(LOG_TAG, "Fehler beim starten des Loaders! Konnte keine zu einem Loader passende ID finden!");
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader loader, Object data) {
        //Die Loader haben ihre Arbeit abgeschlossen
        //Runtergeladene Daten in die Datenbank schreiben

        //Es ist etwas schief gelaufen
        boolean error = false;

        switch (loader.getId()) {
            case ITEM_LOADER:
                if (data != null) {
                    viewModel.addItems((ArrayList<EquipmentItem>) data);
                    downloadsCompleted += 1;
                } else {
                    error = true;
                }
                break;

            case TRAY_LOADER:
                if (data != null) {
                    viewModel.addTrays((ArrayList<TrayItem>) data);
                    downloadsCompleted += 1;
                } else {
                    error = true;
                }
                break;

            case IMAGE_LOADER:
                if (data != null) {
                    viewModel.addImage((ArrayList<ImageItem>) data);
                    downloadsCompleted += 1;
                } else {
                    error = true;
                }
                break;
        }

        if (error) {
            publishProgress(0);
            Toast.makeText(getContext(), "Es ist ein Fehler beim Herunterladen der Daten aufgetreten!", Toast.LENGTH_SHORT).show();
        } else {
            if (downloadsCompleted == 1) {
                publishProgress(40);
            } else if (downloadsCompleted == 2) {
                publishProgress(65);
            } else if (downloadsCompleted == 3) {
                //Datenbankversion aktualiseren
                MainActivity activity = (MainActivity) getActivity();
                publishProgress(90);

                //Variablen aktualisieren
                activity.dbVersion = activity.liveNetDBVersion.getValue();
                activity.dbState = MainActivity.dbstate.VALID;
                dbversion = activity.dbVersion;

                //Preferences aktualisieren
                SharedPreferences.Editor editor = activity.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();
                editor.putInt(Util.PREFS_DBVERSION, activity.dbVersion);
                editor.apply();

                //Angezeigte Datenbankversion aktualisieren
                updateDBVersion(dbversion, null);

                activity.FirstDownloadCompleted = true;

                publishProgress(100);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //TODO: Hier die Dinge zurücksetzen die zurückgesetzt werden müssen

    }

    public void updateNetVersion(Integer integer) {
        TextView tvNetDBVersion = getActivity().findViewById(R.id.dataTextViewDBNetVersion);
        tvNetDBVersion.setText(integer.toString());
    }

    public void buttonAddClick() {
        //Ablauf
        //1. Netzwerküberprüfung
        //2. Versionsabfrage
        //3. -> Neue Version, dann Daten abfragen und verarbeiten
        //Ansonsten Ende

        //URL speichern und für die Loader aufbereiten
        EditText editText = getActivity().findViewById(R.id.text_url);
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();
        url = editText.getText().toString();

        //https einfügen falls nicht vorhanden
        if (!url.contains("http://") && !url.contains("https://")) {
            url = "https://" + url;
        }

        //Prüfen ob als letztes Zeichen ein / vorhanden ist und dieses ggf. entfernen
        if ((url.charAt(url.length() - 2)) == '/') {
            url = (String) url.subSequence(0, url.length() - 3);
        }

        editor.putString(Util.PREFS_URL, url);
        editor.apply();

        //Netzwerkstatus überpüfen

        if (Util_Http.checkNetwork(getActivity(),getContext())) {
            //Netzwerkverbindung i.O.

            MainActivity activity = (MainActivity) getActivity();

            if (activity.dbState == MainActivity.dbstate.CLEAN) {
                activity.getNetDBState(url, false);

                //Marker für den Observer setzen. Mit diesem werden bei einer Änderung der Live-Variable netVersion die Loader gestartet.
                initLoaderAfterNetVersionRefresh = true;
            }
            else {
                int netVersion = activity.liveNetDBVersion.getValue();
                initateLoader(netVersion);
            }

            publishProgress(15);
        }
        else {
            //Keine Netzwerkverbindung -> Nachricht und Ende
            Toast.makeText(getContext(),R.string.app_noConnection,Toast.LENGTH_LONG).show();
        }
    }

    //Die Loader werden in einer eigenen Methode gestartet, damit auf Statusänderung der Livedata-Variable reagiert werden kann
    private void initateLoader(int netVersion) {

        if (netVersion != -1) {
            if (dbversion < netVersion) {

                //Loader initialiseren
                LoaderManager loaderManager = getLoaderManager();

                Bundle args = new Bundle();
                args.putString(Util.ARGS_URL, url);
                args.putInt(Util.ARGS_VERSION, dbversion);

                //Loader anwerfen
                if (loaderManager.getLoader(ITEM_LOADER) == null) {
                    loaderManager.initLoader(ITEM_LOADER, args, this);
                } else {
                    loaderManager.restartLoader(ITEM_LOADER, args, this);
                }

                if (loaderManager.getLoader(TRAY_LOADER) == null) {
                    loaderManager.initLoader(TRAY_LOADER, args, this);
                } else {
                    loaderManager.restartLoader(TRAY_LOADER, args, this);
                }

                if (loaderManager.getLoader(IMAGE_LOADER) == null) {
                    loaderManager.initLoader(IMAGE_LOADER, args, this);
                } else {
                    loaderManager.restartLoader(IMAGE_LOADER, args, this);
                }

            } else {
                Toast.makeText(getContext(), R.string.app_nodbRefresh, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void publishProgress(int progress) {
        ProgressBar progressBar = getActivity().findViewById(R.id.DataProgress);
        progressBar.setProgress(progress);
    }

    @SuppressLint("SetTextI18n")
    private void updateDBVersion(int version, @Nullable View view) {
        TextView tvDBVersion;
        if (view != null) {
            tvDBVersion = view.findViewById(R.id.dataTextViewDBVersion);
        }
        else {
            tvDBVersion = getActivity().findViewById(R.id.dataTextViewDBVersion);
        }
        if (version != -1) {
            tvDBVersion.setText(Integer.toString(version));
        }
        else {
            tvDBVersion.setText("0");
        }
    }

}

