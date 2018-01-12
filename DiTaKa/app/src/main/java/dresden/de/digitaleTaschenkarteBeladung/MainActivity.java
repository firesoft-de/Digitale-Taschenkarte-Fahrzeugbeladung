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

package dresden.de.digitaleTaschenkarteBeladung;


import android.app.SearchManager;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import dresden.de.digitaleTaschenkarteBeladung.fragments.AboutFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.DataImportFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.DebugFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.ItemFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.LicenseFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.SettingsFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.TrayFragment;
import dresden.de.digitaleTaschenkarteBeladung.util.Util;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;
import dresden.de.digitaleTaschenkarteBeladung.util.VersionLoader;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util.FRAGMENT_LIST_ITEM;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util.FRAGMENT_LIST_TRAY;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util.LogDebug;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util.LogError;

public class MainActivity extends AppCompatActivity implements TrayFragment.fragmentCallbackListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks {

    //=======================================================
    //======================KONSTANTEN=======================
    //=======================================================

    //DEBUG Konstanten
    private final static String LOG_TAG="MainActivity_LOG";
    //TODO: Debug ausschalten
    public final static Boolean DEBUG_ENABLED = true;

    //Globale Variablen
    private FragmentManager fManager;
    private LoaderManager lManager;

    public int dbVersion;
//    public int netDBVersion;
    public String url;
    public Util.DbState dbState;

    public MutableLiveData<Integer> liveNetDBVersion;

    public MutableLiveData<Util.Sort> liveSort;

    private Menu xMenu;

    private boolean NetDBVersionCallForUser;
    public boolean FirstDownloadCompleted;

    //=======================================================
    //===================OVERRIDEMETHODEN====================
    //=======================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fManager = this.getSupportFragmentManager();

        lManager = this.getSupportLoaderManager();

        dbVersion = this.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).getInt(Util.PREFS_DBVERSION,-1);
        url = this.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).getString(Util.PREFS_URL,"NO_URL_FOUND");

        //Default Zustand -1 -> Keine Internetverbindung, noch keine Daten empfangen oder ein unbekannter Fehler ist aufgetreten!
        liveNetDBVersion = new MutableLiveData<>();
        liveNetDBVersion.setValue(-1);

        liveSort = new MutableLiveData<>();
        liveSort.setValue(Util.loadSortPref(this));

        if (url == "NO_URL_FOUND") {
            //Kein SERVER-URL gefunden (App wird das erste Mal gestartet) -> Keine internen Datenbankabfragen durchführen sondern Dummy Tray mit Hinweisen für die Erstbenutzung anzeigen!
            dbState = Util.DbState.CLEAN;
            dbVersion = 0;
        }
        else if (dbVersion == -1) {
            dbState = Util.DbState.CLEAN;
            dbVersion = 0;
            if (Util_Http.checkNetwork(this,this)) {
                getNetDBState(null,true);
            }
            else {
                //Keine Netzwerkverbindung -> Nachricht und Ende
                Toast.makeText(this,R.string.app_noConnection,Toast.LENGTH_LONG).show();
            }
        }
        else {
            if (Util_Http.checkNetwork(this,this)) {
                getNetDBState(null, true);
            }
            else {
                //Keine Netzwerkverbindung -> Nachricht und Ende
                Toast.makeText(this,R.string.app_noConnection,Toast.LENGTH_LONG).show();
            }
        }

        FirstDownloadCompleted = false;

        //Erstes Fragment einfügen
        Fragment trayFragment = new TrayFragment();
        switchFragment(R.id.MainFrame,trayFragment, FRAGMENT_LIST_TRAY);
    }

    //Festlegen was passiert wenn der BackButton gedrückt wird
    @Override
    public void onBackPressed() {

        LogDebug(LOG_TAG,"onBackPressed called!");
        handleBackButton();

    }

    //Hier werden alle Menüelement des Optionsmenüs eingefügt
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        xMenu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        //Die verschiedenen Menüeinträge anzeigenoder verstecken
        if (DEBUG_ENABLED) {
            menu.findItem(R.id.OptionMenuDebug).setVisible(true);
        }
        else {
            menu.findItem(R.id.OptionMenuDebug).setVisible(false);
        }

        //TODO: Einstellungsdialog wieder einschalten
        menu.findItem(R.id.OptionMenuSettings).setVisible(false);

        // Suchfunktionalität mittels SearchManager hinzufügen
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(true);
        searchView.setFocusable(true);

        //Verhindern das beim Starten der App die Tastatur angezeigt wird
        searchView.clearFocus();

        //Tastatur anzeigen wenn SearchView expandiert wird
        MenuItem mItem = menu.findItem(R.id.search);


        //Mit diesen Befehlen wird das ActionItem "Sortieren" ausgeblendet, sobald die Suche geöffnet wird.
        mItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                menu.findItem(R.id.ActionSorting).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                menu.findItem(R.id.search).setVisible(false);
                searchView.setIconified(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                menu.findItem(R.id.ActionSorting).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                searchView.setIconified(true);
                invalidateOptionsMenu();
                return true;
            }
        });

        //http://nlopez.io/how-to-style-the-actionbar-searchview-programmatically/
        int closeButtonId = getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButtonImage = searchView.findViewById(closeButtonId);
        closeButtonImage.setImageResource(R.drawable.ic_close_black_24dp);

        int searchButtonId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView searchButtonImage = searchView.findViewById(searchButtonId);
        searchButtonImage.setImageResource(R.drawable.ic_search_black_24dp);

        int searchButtonId2 = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView searchButtonImage2 = searchView.findViewById(searchButtonId2);
        searchButtonImage2.setImageResource(R.drawable.ic_search_black_24dp);

        //https://stackoverflow.com/questions/20323990/remove-the-searchicon-as-hint-in-the-searchview
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchPlate = searchView.findViewById(searchPlateId);
        searchPlate.setHint(R.string.search_hint);

        //Den SearchView Listener aktivieren um eine eigene Intent Auslösung durchzuführen
        searchView.setOnQueryTextListener(this);

        //Debug Menüs ausblenden
        if (!DEBUG_ENABLED) {
            menu.findItem(R.id.OptionMenuDebug).setVisible(false);
        }

        switch (liveSort.getValue()) {
            case AZ:
                menu.findItem(R.id.SortAZ).setChecked(true);
                break;
            case ZA:
                menu.findItem(R.id.SortZA).setChecked(true);
                break;
            case PRESET:
                menu.findItem(R.id.SortXY).setChecked(true);
                break;

        }

        return super.onCreateOptionsMenu(menu);
    }

    //Hier werden die Click-Events für die Menuitems gehandelt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.OptionMenuItemData:
                switchFragment(R.id.MainFrame,null, Util.FRAGMENT_DATA);
                return true;

            case R.id.OptionMenuDebug:
                switchFragment(R.id.MainFrame,null, Util.FRAGMENT_DEBUG);
                return true;

            case R.id.OptionMenuAbout:
                switchFragment(R.id.MainFrame,null, Util.FRAGMENT_ABOUT);
                return true;

            case R.id.OptionMenuLicense:
                switchFragment(R.id.MainFrame,null, Util.FRAGMENT_LICENSE);
                return true;

            case R.id.OptionMenuSettings:
                switchFragment(R.id.MainFrame,null, Util.FRAGMENT_SETTINGS);
                return true;

            case R.id.SortAZ:
                liveSort.postValue(Util.Sort.AZ);
                Util.saveSortPref(Util.Sort.AZ,this);
                item.setChecked(true);
                return true;

            case R.id.SortZA:
                liveSort.postValue(Util.Sort.ZA);
                Util.saveSortPref(Util.Sort.ZA,this);
                item.setChecked(true);
                return true;

            case R.id.SortXY:
                liveSort.postValue(Util.Sort.PRESET);
                Util.saveSortPref(Util.Sort.PRESET,this);
                item.setChecked(true);
                return true;

            case android.R.id.home:
                handleBackButton();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Suchanfrage selbst starten um noch die ArrayListe anzuhängen
    @Override
    public boolean onQueryTextSubmit(String query) {

        Intent searchIntent = new Intent(this, SearchableActivity.class);
        searchIntent.putExtra(SearchManager.QUERY, query);

        searchIntent.setAction(Intent.ACTION_SEARCH);

        startActivity(searchIntent);

        return true;

    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    //=======================================================
    //====================LOADERMETHODEN=====================
    //=======================================================

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String url = args.getString(Util.ARGS_URL);

        return new VersionLoader(this,url);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        liveNetDBVersion.postValue((int) data);
        int netDBVersion = (int) data;

        if ((int) data != -1) {
            if (netDBVersion > dbVersion) {
                //Eine neue Datenbankversion ist verfügbar!
                dbState = Util.DbState.EXPIRED;

                if (NetDBVersionCallForUser) {
                    Snackbar.make(this.findViewById(R.id.MainFrame), R.string.app_db_update_available, Snackbar.LENGTH_LONG)
                            .show();
                }
            } else if (netDBVersion == dbVersion) {
                dbState = Util.DbState.VALID;
            } else {
                dbState = Util.DbState.UNKNOWN;
                LogError(LOG_TAG, "Datenbankstatus ist unbekannt! Irgendwas stimmt hier nicht o.O");
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //Hier gibt es nichts zu tun, die Methode muss aber vorhanden sein
    }

    //=======================================================
    //====================KLASSENMETHODEN====================
    //=======================================================

    /**
     * handleBackButton bearbeitet das Zurückspringen auf ein vorheriges Fragment und nimmt notwendige Veränderungen an der ActionBar vor
     */
    private void handleBackButton() {

        FragmentManager manager = getSupportFragmentManager();

        Fragment currentFragment =  manager.findFragmentById(R.id.MainFrame);

        if (manager.getBackStackEntryCount() > 0 && currentFragment.getTag() != FRAGMENT_LIST_TRAY) {
            //Sind im BackStack Einträge vorhanden?

            //Im BackStack einen Schritt zurück gehen
            manager.popBackStack();

            if (FirstDownloadCompleted) {
                Fragment newFrag = manager.findFragmentByTag(FRAGMENT_LIST_TRAY);
                TrayFragment trayFragment = (TrayFragment) newFrag;
                Bundle args = new Bundle();
                args.putString(Util.ARGS_DBSTATE,dbState.toString());
                trayFragment.setArguments(args);
                FirstDownloadCompleted = false;
            }

            //Überprüfen welches Fragment als nächstes kommt. Dazu wird zunächst geschaut ob der Stack größer gleich 2 ist.
            //Die Überprüfung ist hier etwas umständlich, da der aktuelle Eintrag (also backStackCount - 1) nicht den nächsten Eintrag im Backstack darstellt, sondern den aktuell angezeigten.
            int backStackCount = manager.getBackStackEntryCount();
            if (backStackCount >= 2) {
                String backStackTag;
                backStackTag = manager.getBackStackEntryAt(backStackCount-2).getName();
                manageActionBar(backStackTag);
            }
        } else if (currentFragment.getTag() != FRAGMENT_LIST_TRAY) {
            //Wenn zum ersten Mal Daten heruntergeladen wurden, muss das Trayfragment mit einem neuen Satz Argumente versorgt werden, da der alte noch den falschen dbState enthält
            super.onBackPressed();
        }
    }

    /**
     * Ermittelt die Versionsnummer der Online-Datenbank
     * @param url Server-URL
     * @param callForUser Soll dem Nutzer eine Nachricht ausgegeben werden?
     */
    public void getNetDBState(@Nullable String url, boolean callForUser) {

        Bundle args = new Bundle();

        if (url == null) {
        args.putString(Util.ARGS_URL,this.url);
        }
        else {
            args.putString(Util.ARGS_URL,url);
        }

        //Soll mit dem Wert eine Anzeige für den Benutzer gefüttert werden?
        NetDBVersionCallForUser = callForUser;

        if (lManager.getLoader(0) == null) {
            lManager.initLoader(0, args, this);
        }
        else {
            lManager.restartLoader(0,args,this);
        }
    }

    //Interfacemethoden

    /**
     * switchFragment bietet die Möglichkeit das aktive Fragment nach TAG oder per Objekt zu wechseln
     * @param id Die Resourcenid des Zielviews
     * @param fragment das anzuzeigende Fragment
     * @param tag Fragmenttag für die Behandlung der Zurückoperationen in der MainActivity Klasse
     */
    public void switchFragment(int id, @Nullable Fragment fragment, String tag) { //FragmentManager fManager,

        boolean newFragment = false;

        FragmentTransaction ft;

        ft = fManager.beginTransaction();

        if (fragment == null) {
            fragment = fManager.findFragmentByTag(tag);

            if (fragment == null) {
                newFragment = true;
            }
        }

        try {

            boolean displaySearchIcon = true;

            //Hier wird anhand des gesendeten Tags das passende Fragment gesucht und angezeigt. Gleichzeitig wird der BackButton in den passenden Status gesetzt.
            switch (tag) {

                case Util.FRAGMENT_DATA:
                    if (newFragment) {
                       fragment = new DataImportFragment();
                    }
                    if (url != "" || dbVersion == -1) {
                        Bundle args = new Bundle();
                        args.putString(Util.ARGS_URL,url);
                        args.putInt(Util.ARGS_VERSION,dbVersion);
                        fragment.setArguments(args);
                    }
                    else {
                        Toast.makeText(this, "Fehler beim Erstellen des Import-Fragmentes!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;

                case Util.FRAGMENT_DETAIL:
                    if (newFragment) {
                        //fragment = new DataImportFragment();
                        Toast.makeText(getApplicationContext(), "Hier muss noch ein DetailFragment gebaut werden!",Toast.LENGTH_LONG).show();
                    }
                    break;

                case FRAGMENT_LIST_TRAY:
                    if (newFragment) {
                        fragment = new TrayFragment();
                    }
                    Bundle args = new Bundle();
                    if (dbState != null) {
                        args.putString(Util.ARGS_DBSTATE,dbState.toString()); }
                    else {
                        args.putString(Util.ARGS_DBSTATE,""); }
                    fragment.setArguments(args);
                    break;

                case FRAGMENT_LIST_ITEM:
                    if (newFragment) {
                        fragment = new ItemFragment();
                    }
                    break;

                case Util.FRAGMENT_DEBUG:
                    if (newFragment) {
                        fragment = new DebugFragment();
                    }
                    break;

                case Util.FRAGMENT_SETTINGS:
                    if (newFragment) {
                        fragment = new SettingsFragment();
                    }
                    break;

                case Util.FRAGMENT_ABOUT:
                    if (newFragment) {
                        fragment = new AboutFragment();
                    }
                    break;

                case Util.FRAGMENT_LICENSE:
                    if (newFragment) {
                        fragment = new LicenseFragment();
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Kein passendes Fragment gefunden!");

            }

            if (xMenu != null) {
                xMenu.findItem(R.id.search).setVisible(displaySearchIcon);
            }

            //Diverse Anzeigen entsprechend dem anzuzeigenden Fragment einstellen
            manageActionBar(tag);

        } catch (NullPointerException e) {
            e.printStackTrace();
            LogError(LOG_TAG,"Fehler in der Methode switchFragment!");
        }

        ft.replace(id, fragment, tag);
        ft.addToBackStack(tag);
        ft.commit();

    }

    private void manageActionBar(String tag) {
        setBackButton(tag);
        setTitle(tag);
        setSortButton(tag);
        setSearchButton(tag);
    }

    /**
     * Diese Methode managt die Anzeige des Zurück-Buttons
     * @param tag
     */
    private void setBackButton(String tag) {

        if (tag.equals(FRAGMENT_LIST_TRAY)) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        else {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Diese Methode managt die Anzeige des Suchen-Buttons
     * @param tag
     */
    private void setSearchButton(String tag) {

        if (tag.equals(Util.FRAGMENT_LIST_TRAY) || tag.equals(Util.FRAGMENT_LIST_ITEM)) {
            if (xMenu != null) {
                xMenu.findItem(R.id.search).setVisible(true);
            }
        }
        else {
            if (xMenu != null) {
                xMenu.findItem(R.id.search).setVisible(false);
            }
        }
    }

    /**
     * Diese Methode managt die Anzeige des Sortieren-Buttons
     * @param tag
     */
    private void setSortButton(String tag) {

        if (tag.equals(Util.FRAGMENT_LIST_TRAY) || tag.equals(Util.FRAGMENT_LIST_ITEM)) {
            if (xMenu != null) {
                xMenu.findItem(R.id.ActionSorting).setVisible(true);
            }
        }
        else {
            if (xMenu != null) {
                xMenu.findItem(R.id.ActionSorting).setVisible(false);
            }
        }

    }

    private void setTitle(String tag) {
        switch (tag) {
            case Util.FRAGMENT_DATA:
                this.getSupportActionBar().setTitle(R.string.fragment_title_data);
                break;

            case Util.FRAGMENT_DETAIL:
                this.getSupportActionBar().setTitle(R.string.fragment_title_detail);
                break;

            case FRAGMENT_LIST_TRAY:
                this.getSupportActionBar().setTitle(R.string.fragment_title_tray);
                break;

            case FRAGMENT_LIST_ITEM:
                this.getSupportActionBar().setTitle(R.string.fragment_title_item);
                break;

            case Util.FRAGMENT_DEBUG:
                this.getSupportActionBar().setTitle(R.string.fragment_title_debug);
                break;

            case Util.FRAGMENT_SETTINGS:
                this.getSupportActionBar().setTitle(R.string.fragment_title_settings);
                break;

            case Util.FRAGMENT_LICENSE:
                this.getSupportActionBar().setTitle(R.string.fragment_title_license);
                break;

            case Util.FRAGMENT_ABOUT:
                String title = this.getString(R.string.fragment_title_about)  + " " + this.getString(R.string.app_name);
                this.getSupportActionBar().setTitle(title);
                break;

            default:
                throw new IllegalArgumentException("Kein passendes Fragment gefunden!");

        }
    }
}
