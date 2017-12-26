package dresden.de.digitaleTaschenkarteBeladung;


import android.app.SearchManager;
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

import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.fragments.DataImportFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.DebugFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.ItemFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.TrayFragment;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_ExampleData;

public class MainActivity extends AppCompatActivity implements TrayFragment.fragmentCallbackListener, SearchView.OnQueryTextListener {

    //DEBUG Konstanten
    private final static String LOG_TAG="MainActivity_LOG";

    //Konstanten für die Fragmenterkennung
//    public static final String FRAGMENT_MAIN = "100";
    public static final String FRAGMENT_DATA  = "101";
    public static final String FRAGMENT_LIST_TRAY = "102";
    public static final String FRAGMENT_LIST_ITEM = "103";
    public static final String FRAGMENT_DETAIL = "104";
    public static final String FRAGMENT_DEBUG = "105";


    //Globale Variablen
    private FragmentManager manager;

    //Zentrale Datenvariablen
    public ArrayList<TrayItem> trays;
    public ArrayList<EquipmentItem> equipmentItems;

    //Override Methoden

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = this.getSupportFragmentManager();

        //Erstes Fragment einfügen
        Fragment trayFragment = new TrayFragment();
        switchFragment(R.id.MainFrame,trayFragment,FRAGMENT_LIST_TRAY);


        trays = Util_ExampleData.dummyDataTray();

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

//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (hasFocus) {
//                    showInputMethod(view);
//                }
//            }
//
//            private void showInputMethod(View view) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (imm != null) {
//                    imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
//                }
//            }
//        });

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
            super.onBackPressed();
        }

    }

//    /**
//     * Reicht die Gegenstandsdaten an eine andere Klasse weiter
//     * @return
//     */
//    public ArrayList<EquipmentItem> getEquipmentList() {return equipmentItems;}

    /**
     * Reicht die Behälterdaten an eine andere Klasse weiter
     * @return Liste der Trays
     */
    public ArrayList<TrayItem> getTrayList() {return trays;}


    //Interfacemethoden

    /**
     * switchFragment bietet die Möglichkeit das aktive Fragment nach TAG oder per Objekt zu wechseln
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


        try {
            switch (tag) {

                case FRAGMENT_DATA:
                    if (newFragment) {
                        fragment = new DataImportFragment();
                    }
                    this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    this.getSupportActionBar().setTitle(R.string.fragment_title_data);
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
            Log.e(LOG_TAG,"Fehler in der Methode switchFragment!");
        }

        ft.replace (id, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();


    }



}
