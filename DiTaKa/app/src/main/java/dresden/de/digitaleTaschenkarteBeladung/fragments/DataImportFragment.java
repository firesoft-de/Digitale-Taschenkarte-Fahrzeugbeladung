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

    //Dieser Marker wird beim ersten Start der App verwendet. Wenn er TRUE ist, wird der Observer für das LiveData Objekt netDBVersion die Loader initalisieren, sobald eine Änderung der Version erkannt wird
    private boolean initLoaderAfterNetVersionRefresh;

    //Zählt wie viele Downloads schon fertig sind. Wird verwendet um den Status per Progressbar auszugeben und festzustellen wann alle Downloads fertig sind
    private int downloadsCompleted;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    DataFragViewModel viewModel;

    public DataImportFragment() {
        // Required empty public constructor
    }

    //=======================================================
    //===================OVERRIDE METHODEN===================
    //=======================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Anweisung an Dagger, dass hier eine Injection vorgenommen wird ??
        ((ApplicationForDagger) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        //Variable zurücksetzen
        downloadsCompleted = 0;

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View result = inflater.inflate(R.layout.fragment_data, container, false);


        //Marker zurücksetzen
        initLoaderAfterNetVersionRefresh = false;

        //Hier wird das Viewmodel erstellt und durch die Factory mit Eigenschaften versehen
        viewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(DataFragViewModel.class);

        //URL und Datenbankversion aus den mitgelieferten Argumenten abrufen
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

        //Progressbar einrichten
        ProgressBar progressBar = result.findViewById(R.id.DataProgress);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        //Textfeld einrichten
        final EditText editText = result.findViewById(R.id.text_url);
        if (url != "NO_URL_FOUND") {
            editText.setText(url);
        }
        else {
            editText.setText(R.string.data_first_time_url);
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    EditText editTexter = (EditText) v;
                    if (url == "NO_URL_FOUND") {
                        editTexter.setText("");
                    }
                }
            });

            //Es ist davon auszugehen, dass dies der erste Start ist
            updateDBVersion(0,result);
            updateNetVersion(-1,result);
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

        //Einen Observer für das LiveData Objekt in der MainActivity initaliseren, welches die Version der Serverdatenbank enthält
        final MainActivity activity = (MainActivity) getActivity();

        activity.liveNetDBVersion.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {

                if (integer != -1) {

                    if (!initLoaderAfterNetVersionRefresh) {
                        updateNetVersion(integer, null);
                    } else {
                        updateNetVersion(integer, null);
                        initateLoader(integer);
                    }
                }
                else {
                    //Es ist ein Fehler beim Datenabruf aufgetreten!
                    Toast.makeText(getContext(),"Die Server-URL ist wahrscheinlich fehlerhaft!",Toast.LENGTH_LONG).show();
                }
            }
        });

        updateDBVersion(dbversion, result);

        // Inflate the layout for this fragment
        return result;
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //Diese Methode wird aufgerufen wenn der LoadManager seine Loader initalisiert
        //Es werden je nach eingegebener ID die unterschiedlichen Loader zurückgegeben

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

    //=======================================================
    //===================WICHTIGE METHODEN===================
    //=======================================================

    public void buttonAddClick() {
        //Ablauf
        //1. Netzwerküberprüfung
        //2. Versionsabfrage
        //3.    -> Neue Version
        //      -> Daten abfragen
        //      -> Daten verarbeiten
        //Ansonsten Ende


        EditText editText = getActivity().findViewById(R.id.text_url);
        url = handleURL(editText.getText().toString());

        //Netzwerkstatus überpüfen
        if (Util_Http.checkNetwork(getActivity(),getContext())) {
            //Netzwerkverbindung i.O.

            MainActivity activity = (MainActivity) getActivity();

            if (activity.dbState == MainActivity.dbstate.CLEAN) {
                //Die App wurde das erste Mal gestartet. D.h. es ist noch keine Serverdatenbankversion abgerufen worden

                //Serverdatenbankversion abrufen
                activity.getNetDBState(url, false);

                //Marker für den Observer setzen. Mit diesem werden bei einer Änderung der Live-Variable netVersion die Loader gestartet.
                initLoaderAfterNetVersionRefresh = true;
            }
            else {
                int netVersion = activity.liveNetDBVersion.getValue();
                initateLoader(netVersion);
            }

            //Einen Wert für den Benutzer ausgeben
            publishProgress(15);
        }
        else {
            //Keine Netzwerkverbindung -> Nachricht und Ende
            publishProgress(0);
            Toast.makeText(getContext(),R.string.app_noConnection,Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Die Methode startet die Loader. Es wird eine eigene Methode verwendet, damit flexibler auf Statusänderungen in den LiveData-Variablen reagiert werden kann.
     * @param netVersion
     */
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

    //=======================================================
    //=====================HILFSMETHODEN=====================
    //=======================================================

    /**
     * Die Methode aktualisiert die Progressbar mit dem gegebenen Wert
     * @param progress Fortschrittswert
     */
    private void publishProgress(int progress) {
        ProgressBar progressBar = getActivity().findViewById(R.id.DataProgress);
        progressBar.setProgress(progress);
    }

    /**
     * Diese Methode bearbeitet die eingebene URL so, dass sie konform mit den nachfolgenden Arbeitschritte ist. Außerdem wird die URL in den PREFS gespeichert.
     * @param url Die zu bearbeitende URL
     * @return Die bearbietete URL
     */
    private String handleURL(String url) {
        //Den PREF Manager initaliseren
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();

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

        return url;
    }

    /**
     * Die Methode bearbeitet die Ausgabe der lokalen Datenbankversion
     * @param version die lokale Datenbankversion die ausgegeben werden soll
     * @param view falls ein View vorhanden ist, kann dieser hier mitgegeben werden
     */
    @SuppressLint("SetTextI18n")
    private void updateDBVersion(int version, @Nullable View view) {
        TextView tvDBVersion;
        if (view != null) {
            tvDBVersion = view.findViewById(R.id.dataTextViewDBVersion);
        }
        else {
            tvDBVersion = getActivity().findViewById(R.id.dataTextViewDBVersion);
        }

        // Falls als Versionsnummer -1 eingegeben wird, wird der Wert hier überschrieben (optische Gründe)
        if (version != 0) {
            tvDBVersion.setText(Integer.toString(version));
        }
        else {
            tvDBVersion.setText(getResources().getString(R.string.data_no_db));
        }
    }

    /**
     * Die Methode aktualisiert die im Fragment dargestellte Serverdatenbankversion
     * @param version die darzustellende Version
     */
    @SuppressLint("SetTextI18n")
    private void updateNetVersion(int version, @Nullable View view) {
        TextView tvNetDBVersion;

        if (view != null) {
            tvNetDBVersion = view.findViewById(R.id.dataTextViewDBNetVersion);
        }
        else {
            tvNetDBVersion = getActivity().findViewById(R.id.dataTextViewDBNetVersion);
        }

        //Version überschreiben
        if (version != -1) {
            tvNetDBVersion.setText(((Integer) version).toString());
        }
        else {
            tvNetDBVersion.setText(getResources().getString(R.string.data_no_net_db));
        }
    }

}

