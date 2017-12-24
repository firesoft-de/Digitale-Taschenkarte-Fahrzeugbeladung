package dresden.de.blueproject;


import android.app.SearchManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import dresden.de.blueproject.data.DatabaseEquipment;
import dresden.de.blueproject.data.DatabaseEquipmentDAO;
import dresden.de.blueproject.data.DatabaseEquipmentMininmal;
import dresden.de.blueproject.dataStructure.EquipmentItem;
import dresden.de.blueproject.dataStructure.TrayItem;
import dresden.de.blueproject.fragments.DataImportFragment;
import dresden.de.blueproject.fragments.ItemFragment;
import dresden.de.blueproject.fragments.TrayFragment;
import util.Util_ExampleData;

public class MainActivity extends AppCompatActivity implements TrayFragment.fragmentCallbackListener, SearchView.OnQueryTextListener {

    //DEBUG Konstanten
    private final static String LOG_TAG="MainActivity_LOG";

    //Konstanten für die Fragmenterkennung
    public static final String FRAGMENT_MAIN = "100";
    public static final String FRAGMENT_DATA  = "101";
    public static final String FRAGMENT_LIST_TRAY = "102";
    public static final String FRAGMENT_LIST_ITEM = "103";
    public static final String FRAGMENT_DETAIL = "104";


    //Globale Variablen
    private FragmentTransaction ft;
    private FragmentManager manager;

    //Zentrale Datenvariablen
    public ArrayList<TrayItem> trays;
    public ArrayList<EquipmentItem> equipmentItems;

    public DatabaseEquipment databaseEquipment;

    //Override Methoden

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchFragment(R.id.MainFrame,null,FRAGMENT_MAIN);

        manager = this.getSupportFragmentManager();
        ft = manager.beginTransaction();

        //Erstes Fragment einfügen
        Fragment trayFragment = new TrayFragment();
        switchFragment(R.id.MainFrame,trayFragment,FRAGMENT_MAIN);


        trays = Util_ExampleData.dummyDataTray();
        equipmentItems = Util_ExampleData.dummyDataEquipment();

        databaseEquipment = Room.databaseBuilder(getApplicationContext(),DatabaseEquipment.class,"datenbank").build();

        DatabaseEquipmentDAO daoAgent = databaseEquipment.equipmentDAO();

        // daoAgent.insertItem(equipmentItems.get(0).toDatabaseObject());

        daoAgent.insertItem(equipmentItems.get(0).toDatabaseObject());

        DatabaseEquipmentMininmal item = databaseEquipment.equipmentDAO().findMinimalItemByID(0);

/*        //Test um ein anderes Fragment darzustellen
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        Fragment beladungFragment = new TrayFragment();
        ft.replace(R.id.MainFrame, beladungFragment);
        ft.commit();*/

    }

    //Festlegen was passiert wenn der BackButton gedrückt wird
    @Override
    public void onBackPressed() {

        Log.d(LOG_TAG,"onBackPressed called!");
        handleBackButton();

    }

    //Hier werden alle Menüelement des Optionsmenüs eingefügt
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        // Suchfunktionalität mittels SearchManager hinzufügen
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);

        //Verhindern das beim Starten der App die Tastatur angezeigt wird
        searchView.clearFocus();

        //http://nlopez.io/how-to-style-the-actionbar-searchview-programmatically/
        int closeButtonId = getResources().getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButtonImage = (ImageView) searchView.findViewById(closeButtonId);
        closeButtonImage.setImageResource(R.drawable.ic_close_black_24dp);

        int searchButtonId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView searchButtonImage = (ImageView) searchView.findViewById(searchButtonId);
        searchButtonImage.setImageResource(R.drawable.ic_search_black_24dp);

        int searchButtonId2 = getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView searchButtonImage2 = (ImageView) searchView.findViewById(searchButtonId2);
        searchButtonImage2.setImageResource(R.drawable.ic_search_black_24dp);

        //https://stackoverflow.com/questions/20323990/remove-the-searchicon-as-hint-in-the-searchview
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText searchPlate = (EditText) searchView.findViewById(searchPlateId);
        searchPlate.setHint(R.string.search_hint);

        //Den SearchView Listener aktivieren um eine eigene Intent Auslösung durchzuführen
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    //Hier werden die Click-Events für die Menuitems gehandelt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.OptionMenuItemData:
                showDataImport();
                return true;

            case android.R.id.home:
                handleBackButton();

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //Suchanfrage selbst starten um noch die ArrayListe anzuhängen
    @Override
    public boolean onQueryTextSubmit(String query) {

        Intent searchIntent = new Intent(this, SearchableActivity.class);
        searchIntent.putExtra(SearchManager.QUERY, query);

        Bundle bundle = new Bundle();

        bundle.putParcelableArrayList("EL",equipmentItems);
        searchIntent.putExtras(bundle); // pass the search context data
        searchIntent.setAction(Intent.ACTION_SEARCH);

        startActivity(searchIntent);

        return true; // we start the search activity manually

    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    //
//    @Override
//    public void startActivity(Intent intent) {
//        // check if search intent
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            Bundle bundle = new Bundle();
//
//            bundle.putParcelableArrayList("EL",(ArrayList) equipmentItems);
//
//            intent.putExtras(bundle);
//        }
//
//        super.startActivity(intent);
//    }


    //Klassen-Methoden

    /**
     * @handleBackButton bearbeitet das Zurückspringen auf ein vorheriges Fragment und nimmt notwendige Veränderungen an der ActionBar vor
     */
    private void handleBackButton() {

        FragmentManager manager = getSupportFragmentManager();

        if (manager.getBackStackEntryCount() > 0) {
            //Sind im BackStack Einträge vorhanden?

            //Im BackStack einen Schritt zurück gehen
            manager.popBackStack();

            Fragment currentFragment =  manager.findFragmentById(R.id.MainFrame);

            //Herausfinden welches Fragment angezeigt wurde um dann entsprechend die Titelleiste einzustellen
            switch (currentFragment.getTag()) {

                case (FRAGMENT_DATA):
                    this.getSupportActionBar().setTitle(R.string.app_name);
                    //Zurückanzeige ausblenden
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    break;

                default:
                    //Nichts tun
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    break;
            }

        } else {
            super.onBackPressed();
        }

    }

    /**
     * @showDataImport bereitet das Anzeigen des Datenimportfragments vor und führt dieses durch
     */
    private void showDataImport() {

//        Toast.makeText(MainActivity.this,"Du hast auf den Datenimport geklickt!",Toast.LENGTH_LONG).show();
        Fragment datenFragment = new DataImportFragment();
        switchFragment(R.id.MainFrame, datenFragment, FRAGMENT_DATA);

    }

    /**
     * Reicht die Gegenstandsdaten an eine andere Klasse weiter
     * @return
     */
    public ArrayList<EquipmentItem> getEquipmentList() {return equipmentItems;}

    /**
     * Reicht die Behälterdaten an eine andere Klasse weiter
     * @return
     */
    public ArrayList<TrayItem> getTrayList() {return trays;}


    //Interfacemethoden

    /**
     * @switchFragment bietet die Möglichkeit das aktive Fragment nach TAG oder per Objekt zu wechseln
     * @param id Die Resourcenid des Zielviews
     * @param fragment das anzuzeigende Fragment
     * @param tag Fragmenttag für die Behandlung der Zurückoperationen in der MainActivity Klasse
     */
    public void switchFragment(int id, @Nullable Fragment fragment, String tag) { //FragmentManager manager,

        boolean newFragment = false;

        FragmentTransaction ft;

        ft = manager.beginTransaction();

        if (fragment == null) {
            fragment = manager.findFragmentByTag(tag);

            if (fragment == null) {
                newFragment = true;
            }
        }

        switch (tag) {

            case FRAGMENT_DATA:
                if (newFragment == true) {
                    fragment = new DataImportFragment();
                }
                this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;

            case FRAGMENT_DETAIL:
                if (newFragment == true) {
                    //fragment = new DataImportFragment();
                    Toast.makeText(getApplicationContext(), "Hier muss noch ein DetailFragment gebaut werden!",Toast.LENGTH_LONG).show();
                }
                this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;

            case FRAGMENT_LIST_TRAY:
                if (newFragment == true) {
                    fragment = new TrayFragment();
                }
                this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;

            case FRAGMENT_LIST_ITEM:
                if (newFragment == true) {
                    fragment = new ItemFragment();
                }
                this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;

        }

        ft.replace (id, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();


    }



}
