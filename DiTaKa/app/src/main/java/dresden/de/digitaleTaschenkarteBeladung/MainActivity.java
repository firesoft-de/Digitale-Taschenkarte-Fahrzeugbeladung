package dresden.de.digitaleTaschenkarteBeladung;


import android.app.SearchManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.fragments.DataImportFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.DebugFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.ItemFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.TrayFragment;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;
import dresden.de.digitaleTaschenkarteBeladung.util.VersionLoader;
import dresden.de.digitaleTaschenkarteBeladung.util.util;

import static dresden.de.digitaleTaschenkarteBeladung.util.util.LogDebug;
import static dresden.de.digitaleTaschenkarteBeladung.util.util.LogError;

public class MainActivity extends AppCompatActivity implements TrayFragment.fragmentCallbackListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks {



    //DEBUG Konstanten
    private final static String LOG_TAG="MainActivity_LOG";
    //TODO: Debug ausschalten
    public final static Boolean DEBUG_ENABLED = true;

    //Konstanten für die Fragmenterkennung
//    public static final String FRAGMENT_MAIN = "100";
    public static final String FRAGMENT_DATA  = "101";
    public static final String FRAGMENT_LIST_TRAY = "102";
    public static final String FRAGMENT_LIST_ITEM = "103";
    public static final String FRAGMENT_DETAIL = "104";
    public static final String FRAGMENT_DEBUG = "105";

    public static final String ARGS_URL = "ARGS_URL";
    public static final String ARGS_VERSION = "ARGS_VERSION";
    public static final String ARGS_DBSTATE = "ARGS_DBSTATE";
    public static final String ARGS_CALLFORUSER = "ARGS_CALLFORUSER";

    public static final String PREFS_NAME="dresden.de.digitaleTaschenkarteBeladung";
    public static final String PREFS_URL="dresden.de.digitaleTaschenkarteBeladung.url";
    public static final String PREFS_DBVERSION="dresden.de.digitaleTaschenkarteBeladung.dbversion";

    //Globale Variablen
    private FragmentManager fManager;
    private LoaderManager lManager;

    public int dbVersion;
//    public int netDBVersion;
    public String url;
    public dbstate dbState;

    public MutableLiveData<Integer> liveNetDBVersion;

    public enum dbstate {
        VALID,
        EXPIRED,
        CLEAN,
        UNKNOWN
    }

    private boolean NetDBVersionCallForUser;
    public boolean FirstDownloadCompleted;

    //Zentrale Datenvariablen
//    public ArrayList<TrayItem> trays;
//    public ArrayList<EquipmentItem> equipmentItems;

    //Override Methoden

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fManager = this.getSupportFragmentManager();

        lManager = this.getSupportLoaderManager();

        dbVersion = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(PREFS_DBVERSION,-1);
        url = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(PREFS_URL,"NO_URL_FOUND");

        //Default Zustand -1 -> Keine Internetverbindung, noch keine Daten empfangen oder ein unbekannter Fehler ist aufgetreten!
        liveNetDBVersion = new MutableLiveData<>();
        liveNetDBVersion.setValue(-1);

        if (url == "NO_URL_FOUND") {
            //Kein SERVER-URL gefunden (App wird das erste Mal gestartet) -> Keine internen Datenbankabfragen durchführen sondern Dummy Tray mit Hinweisen für die Erstbenutzung anzeigen!
            dbState = dbstate.CLEAN;
            dbVersion = 0;
        }
        else if (dbVersion == -1) {
            dbState = dbstate.CLEAN;
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
        switchFragment(R.id.MainFrame,trayFragment,FRAGMENT_LIST_TRAY);
    }

    //Festlegen was passiert wenn der BackButton gedrückt wird
    @Override
    public void onBackPressed() {

        LogDebug(LOG_TAG,"onBackPressed called!");
        handleBackButton();

    }

    //Hier werden alle Menüelement des Optionsmenüs eingefügt
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        // Suchfunktionalität mittels SearchManager hinzufügen
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);

        //Verhindern das beim Starten der App die Tastatur angezeigt wird
        searchView.clearFocus();

        //Tastatur anzeigen wenn SearchView expandiert wird
        MenuItem mItem = menu.findItem(R.id.search);

        searchView.setFocusable(true);

        mItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                searchView.requestFocus();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
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

        return super.onCreateOptionsMenu(menu);
    }

    //Hier werden die Click-Events für die Menuitems gehandelt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.OptionMenuItemData:
                switchFragment(R.id.MainFrame,null,FRAGMENT_DATA);
                return true;

            case R.id.OptionMenuDebug:
                switchFragment(R.id.MainFrame,null, FRAGMENT_DEBUG);
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

//        Bundle bundle = new Bundle();
//
//        bundle.putParcelableArrayList("EL",equipmentItems);
//        searchIntent.putExtras(bundle); // pass the search context data
        searchIntent.setAction(Intent.ACTION_SEARCH);

        startActivity(searchIntent);

        return true; // we start the search activity manually

    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String url = args.getString(ARGS_URL);

        return new VersionLoader(this,url);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        liveNetDBVersion.postValue((int) data);
        int netDBVersion = (int) data;

        if ((int) data != -1) {
            if (netDBVersion > dbVersion) {
                //Eine neue Datenbankversion ist verfügbar!
                dbState = dbstate.EXPIRED;

                //TODO: Bessere Userinfo hinzufügen
                if (NetDBVersionCallForUser) {
                Toast.makeText(this, "Es ist eine neue Datenbankversion verfügbar!", Toast.LENGTH_LONG).show(); }
            } else if (netDBVersion == dbVersion) {
                dbState = dbstate.VALID;
            } else {
                dbState = dbstate.UNKNOWN;
                LogError(LOG_TAG, "Datenbankstatus ist unbekannt! Irgendwas stimmt hier nicht o.O");
            }
        }
        else {
            //TODO: Irgendwas ist schief gelaufen
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    //Klassen-Methoden

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
                args.putString(ARGS_DBSTATE,dbState.toString());
                trayFragment.setArguments(args);
                FirstDownloadCompleted = false;
            }

            //Herausfinden welches Fragment angezeigt wurde um dann entsprechend die Titelleiste einzustellen
            switch (currentFragment.getTag()) {

/*                case (FRAGMENT_DATA):
                    this.getSupportActionBar().setTitle(R.string.fragment_title_tray);
                    //Zurückanzeige ausblenden
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    break;

                case (FRAGMENT_LIST_ITEM):
                    this.getSupportActionBar().setTitle(R.string.fragment_title_tray);
                    //Zurückanzeige einblenden
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    break;*/

                case (FRAGMENT_DETAIL):
                    this.getSupportActionBar().setTitle(R.string.fragment_title_item);
                    //Zurückanzeige einblenden
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    break;

                default:
                    //Nichts tun
                    this.getSupportActionBar().setTitle(R.string.fragment_title_tray);
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    break;
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
        args.putString(ARGS_URL,this.url); }
        else {
            args.putString(ARGS_URL,url); }

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

            //Hier wird anhand des gesendeten Tags das passende Fragment gesucht und angezeigt. Gleichzeitig wird der BackButton in den passenden Status gesetzt.
            switch (tag) {

                case FRAGMENT_DATA:
                    if (newFragment) {
                       fragment = new DataImportFragment();
                    }
                    if (url != "" || dbVersion == -1) {
                        Bundle args = new Bundle();
                        args.putString(ARGS_URL,url);
                        args.putInt(ARGS_VERSION,dbVersion);
                        fragment.setArguments(args);

                        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        this.getSupportActionBar().setTitle(R.string.fragment_title_data);
                    }
                    else {
                        Toast.makeText(this, "Fehler beim Erstellen des Import-Fragmentes!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    break;

                case FRAGMENT_DETAIL:
                    if (newFragment) {
                        //fragment = new DataImportFragment();
                        Toast.makeText(getApplicationContext(), "Hier muss noch ein DetailFragment gebaut werden!",Toast.LENGTH_LONG).show();
                    }
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    this.getSupportActionBar().setTitle(R.string.fragment_title_detail);
                    break;

                case FRAGMENT_LIST_TRAY:
                    if (newFragment) {
                        fragment = new TrayFragment();
                    }
                    Bundle args = new Bundle();
                    if (dbState != null) {
                        args.putString(ARGS_DBSTATE,dbState.toString()); }
                    else {
                        args.putString(ARGS_DBSTATE,""); }
                    fragment.setArguments(args);
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    this.getSupportActionBar().setTitle(R.string.fragment_title_tray);
                    break;

                case FRAGMENT_LIST_ITEM:
                    if (newFragment) {
                        fragment = new ItemFragment();
                    }
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    this.getSupportActionBar().setTitle(R.string.fragment_title_item);
                    break;

                case FRAGMENT_DEBUG:
                    if (newFragment) {
                        fragment = new DebugFragment();
                    }
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    this.getSupportActionBar().setTitle(R.string.fragment_title_debug);
                    break;

                default:
                    throw new IllegalArgumentException("Kein passendes Fragment gefunden!");

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            LogError(LOG_TAG,"Fehler in der Methode switchFragment!");
        }

        ft.replace (id, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();

    }




}
