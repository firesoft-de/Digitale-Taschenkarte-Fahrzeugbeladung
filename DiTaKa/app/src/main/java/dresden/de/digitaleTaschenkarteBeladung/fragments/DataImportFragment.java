/*  Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
    Copyright (C) 2017  David Schlossarczyk

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    For the full license visit https://www.gnu.org/licenses/gpl-3.0.*/

package dresden.de.digitaleTaschenkarteBeladung.fragments;


import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.GroupLoader;
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
    private static final int GROUP_LOADER = 4;

    //Datenbankversion
    private int dbversion;
    private String url;

    //Dieser Marker wird beim ersten Start der App verwendet. Wenn er TRUE ist, wird der Observer für das LiveData Objekt netDBVersion die Loader initalisieren, sobald eine Änderung der Version erkannt wird
    private boolean initLoaderAfterNetVersionRefresh;

    //Zählt wie viele Downloads schon fertig sind. Wird verwendet um den Status per Progressbar auszugeben und festzustellen wann alle Downloads fertig sind
    private int downloadsCompleted;

    //Gibt an ob die Gruppen schon abgefragt wurden, also als nächstes der richtige Download stattfinden soll
    private boolean groupSelectionCompleted = false;

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
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        progressBar.setIndeterminate(false);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        progressBar.setVisibility(View.VISIBLE);

        //Textfeld einrichten
        final EditText editText = result.findViewById(R.id.text_url);
        if (url != "NO_URL_FOUND") {
            editText.setText(url);
        }
        else {
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

                    activity.url = url;

                    if (!initLoaderAfterNetVersionRefresh) {
                        updateNetVersion(integer, null);
                    } else {
                        updateNetVersion(integer, null);
                        initateLoader(integer);
                    }
                }
                else {
                    if (activity.dbState != Util.DbState.CLEAN) {
                        //Es ist ein Fehler beim Datenabruf aufgetreten!
                        toggleURLError(true);
                        publishProgress(true,true);
                    }
                }
            }
        });

        updateDBVersion(dbversion, result);

        FloatingActionButton fab = result.findViewById(R.id.flActBt);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonAddClick();
            }
        });

        // Inflate the layout for this fragment
        return result;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();

        //addGroupToSelection(activity.groups_subscribed,true);

        //URL Fehler ausblenden
        toggleURLError(false);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //Diese Methode wird aufgerufen wenn der LoadManager seine Loader initalisiert
        //Es werden je nach eingegebener ID die unterschiedlichen Loader zurückgegeben

        String url = args.getString(Util.ARGS_URL);
        Integer version = args.getInt(Util.ARGS_VERSION);
        String group = Util.getGroupQuery(getActivity());

        switch (id) {
            case ITEM_LOADER:
                //ID 1: Ein neuer ItemLoader wird gebraucht!
                return new ItemLoader(getContext(),url,version,group);

            case TRAY_LOADER:
                //ID 2: Ein neuer TrayLoader wird gebraucht!
                return new TrayLoader(getContext(),url,version,group);

            case IMAGE_LOADER:
                //ID 3: Ein neuer ImageLoader wird gebraucht!
                return new ImageLoader(getContext(),url,version,group);

            case GROUP_LOADER:
                //ID 3: Ein neuer ImageLoader wird gebraucht!
                return new GroupLoader(getContext(),url,version);

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

            case GROUP_LOADER:
                if (data != null) {
                    if (((ArrayList<String>) data).size() != 0) {
                        addGroupToSelection((ArrayList<String>) data,false);
                        publishProgress(true,false);
                        groupSelectionCompleted = true;
                    }
                    downloadsCompleted += 1;
                } else {
                    error = true;
                }
                break;
        }

        if (error) {
            publishProgress(true,true);

            Snackbar.make(getActivity().findViewById(R.id.MainFrame),"Es ist ein Fehler beim Herunterladen der Daten aufgetreten!",Snackbar.LENGTH_LONG)
                    .show();
        } else {
            if (downloadsCompleted == 4) {
                //Datenbankversion aktualiseren
                MainActivity activity = (MainActivity) getActivity();

                //Variablen aktualisieren
                activity.dbVersion = activity.liveNetDBVersion.getValue();
                activity.dbState = Util.DbState.VALID;
                dbversion = activity.dbVersion;

                //Preferences aktualisieren
                SharedPreferences.Editor editor = activity.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();
                editor.putInt(Util.PREFS_DBVERSION, activity.dbVersion);
                editor.apply();

                //Angezeigte Datenbankversion aktualisieren
                updateDBVersion(dbversion, null);

                //Wird nur benötigt, falls der erste Download abgeschlossen wurde. Wird aber trotzdem zur Sicherheit immer true gesetzt
                activity.FirstDownloadCompleted = true;

                //Gruppen speichern
                activity.gManager.setActiveGroup(null);
                activity.invalidateOptionsMenu();

                activity.gManager.saveGroupsToPref();

                transformFAB(2);

                //Vollzug melden
                publishProgress(true,false);
                Snackbar.make(activity.findViewById(R.id.MainFrame),"Die Datenbank wurde erfolgreich heruntergeladen.",Snackbar.LENGTH_LONG)
                        .show();

                activity.manageActionBar(Util.FRAGMENT_DATA);

            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //Hier wird nichts gemacht :/
    }

    //=======================================================
    //===================WICHTIGE METHODEN===================
    //=======================================================

    private void buttonAddClick() {
        //Ablauf
        //1. Netzwerküberprüfung
        //2. Versionsabfrage
        //3.    -> Neue Version
        //      -> Daten abfragen
        //      -> Daten verarbeiten
        //Ansonsten Ende

        MainActivity activity = (MainActivity) getActivity();

        if(!groupSelectionCompleted) {
            //Gruppenauswahl hat noch nicht stattgefunden


            //URL Fehlermeldung ausblenden
            toggleURLError(false);

            publishProgress(false,false);

            EditText editText = getActivity().findViewById(R.id.text_url);
            if (editText.getText().toString().equals("")) {
                publishProgress(true,true);
                Snackbar.make(activity.findViewById(R.id.MainFrame),R.string.data_url_error,Snackbar.LENGTH_LONG)
                        .show();
                return;
            }

            url = Util_Http.handleURL(editText.getText().toString(), getActivity());

            //Netzwerkstatus überpüfen
            if (Util_Http.checkNetwork(getActivity(),getContext())) {
                //Netzwerkverbindung i.O.

                //Serverdatenbankversion abrufen
                activity.getNetDBState(url, false);

                //Marker für den Observer setzen. Mit diesem werden bei einer Änderung der Live-Variable netVersion die Loader gestartet.
                initLoaderAfterNetVersionRefresh = true;

                //Auf das Ergebniss des LiveData Objects warten

            }
            else {
                //Keine Netzwerkverbindung -> Nachricht und Ende
                publishProgress(true,true);
                Snackbar.make(activity.findViewById(R.id.MainFrame),R.string.app_noConnection,Snackbar.LENGTH_LONG)
                        .show();
            }
        }
        else {
            //Gruppenauswahl ist abgeschlossen. Als nächstes soll der richtige Download stattfinden
            publishProgress(false,false);

            //Überprüfen welche Gruppen und überhaupt welche ausgewählt wurden
            ArrayList<String> list = checkSelectedGroups();
            if (list.size() == 0) {
                publishProgress(true,true);
                Snackbar.make(activity.findViewById(R.id.MainFrame),"Bitte wähle mindestens eine Gruppe aus",Snackbar.LENGTH_LONG)
                        .show();
            }
            else {
                activity.gManager.setSubscribedGroups(list);

                //TODO: Prüfen ob Gruppen neu dazugekommen sind oder gelöscht wurden


                //Loader starten
                initateLoader(activity.liveNetDBVersion.getValue());
            }
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

                if (groupSelectionCompleted) {
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
                }
                else {
                    if (loaderManager.getLoader(GROUP_LOADER) == null) {
                        loaderManager.initLoader(GROUP_LOADER, args, this);
                    } else {
                        loaderManager.restartLoader(GROUP_LOADER, args, this);
                    }
                }

            } else {
//                Toast.makeText(getContext(), R.string.app_nodbRefresh, Toast.LENGTH_LONG).show();
                Snackbar.make(getActivity().findViewById(R.id.MainFrame),R.string.app_nodbRefresh,Snackbar.LENGTH_LONG)
                        .show();

                transformFAB(2);
                publishProgress(true,false);

            }
        }
    }

    //=======================================================
    //=====================HILFSMETHODEN=====================
    //=======================================================

    /**
     * Die Methode aktualisiert die Progressbar mit dem gegebenen Wert
     */
    private void publishProgress(boolean finished, boolean error) {

        ProgressBar progressBar = getActivity().findViewById(R.id.DataProgress);

        if (error) {
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.MULTIPLY);
            progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.MULTIPLY);
        }
        else {
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }

        if (finished) {
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(100);
        }
        else {
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        }

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

    private void toggleURLError(boolean enabled) {

        EditText editText = getActivity().findViewById(R.id.text_url);
        TextView errorTV = getActivity().findViewById(R.id.DataURLError);

        if (enabled)  {

            Drawable textback = editText.getBackground();
            textback.setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_ATOP);
            editText.setBackground(textback);
            errorTV.setText(R.string.data_url_error);
            errorTV.setVisibility(View.VISIBLE);

            //Drawables für Elevation neu setzen, um einen Anzeigefehler zu verhindern
            Drawable drawable = getResources().getDrawable(android.R.drawable.dialog_holo_light_frame);

            CardView card = getActivity().findViewById(R.id.card1);
            card.setBackground(drawable);

            card = getActivity().findViewById(R.id.card2);
            card.setBackground(drawable);

            card = getActivity().findViewById(R.id.cardGroup);
            card.setBackground(drawable);

        }
        else {
            editText.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            errorTV.setVisibility(View.INVISIBLE);
        }
    }

    private void addGroupToSelection(ArrayList<String> groups, boolean fragmentStart)  {

        if (groups.size() > 0) {

            MainActivity activity = (MainActivity) getActivity();

            CardView card = activity.findViewById(R.id.cardGroup);

            card.setVisibility(View.VISIBLE);
            card.setMinimumHeight(30);

            ViewGroup viewGroup = getActivity().findViewById(R.id.data_llayout);

            for (String group : groups
                    ) {
                ViewGroupSelector groupSelector = new ViewGroupSelector(LayoutInflater.from(getContext()), getContext(), viewGroup);
                groupSelector.setGroupName(group);

                if (activity.gManager.contains(group)) {
                    groupSelector.setCheckState(true);
                }

                View view = groupSelector.getOwnView();
                viewGroup.addView(view);
            }

            if (!fragmentStart) {
                //Design des FAB anpassen
                transformFAB(1);
            }
        }
    }

    private ArrayList<String> checkSelectedGroups() {

        MainActivity activity = (MainActivity) getActivity();
        ViewGroup viewGroup = getActivity().findViewById(R.id.data_llayout);

        ArrayList<String> activeGroups = new ArrayList<>();

        for (int x = 1; x < viewGroup.getChildCount(); x++) {

            View view = viewGroup.getChildAt(x);
             ViewGroupSelector vgs = new ViewGroupSelector(view,getContext());
             if (vgs.getCheckState()) {
                 activeGroups.add(vgs.getGroupName());
             }
        }
        return activeGroups;
    }

    /**
     *
     * @param state 1 = Gruppenauswahl, 2 = Download abgeschlossen
     */
    private void transformFAB(int state) {
        switch (state) {
            case 1:
                FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.flActBt);
                Drawable draw = floatingActionButton.getBackground();
                draw.setColorFilter(getResources().getColor(R.color.fab_highlight), PorterDuff.Mode.SRC_ATOP);
                floatingActionButton.setBackground(draw);
                floatingActionButton.setImageResource(R.drawable.ic_cloud_download);
                floatingActionButton.invalidate();
                break;

            case 2:
                floatingActionButton = getActivity().findViewById(R.id.flActBt);
                draw = floatingActionButton.getBackground();
                draw.setColorFilter(getResources().getColor(R.color.fab_completed), PorterDuff.Mode.SRC_ATOP);
                floatingActionButton.setBackground(draw);
                floatingActionButton.setImageResource(R.drawable.ic_cloud_done);
                floatingActionButton.invalidate();
                break;

            default:
        }
    }
}

