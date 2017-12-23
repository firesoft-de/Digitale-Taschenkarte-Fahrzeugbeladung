package dresden.de.blueproject;

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
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TrayFragment.fragmentCallbackListener {

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

    //Zentrale Datenvariablen
    public ArrayList<TrayItem> trays;
    public ArrayList<EquipmentItem> equipmentItems;



    //Override Methoden

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ft = this.getSupportFragmentManager().beginTransaction();

        //Erstes Fragment einfügen
        Fragment trayFragment = new TrayFragment();
        switchFragment(R.id.MainFrame,trayFragment,FRAGMENT_MAIN);


        trays = Util_ExampleData.dummyDataTray();
        equipmentItems = Util_ExampleData.dummyDataEquipment();

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

        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
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


    //Methoden

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
     * @switchFragment bietet die Möglichkeit das aktive Fragment zu wechseln
     * @param id Die Resourcenid des Zielviews
     * @param fragment das anzuzeigende Fragment
     * @param tag Fragmenttag für die Behandlung der Zurückoperationen in der MainActivity Klasse
     */
    public void switchFragment(int id, @Nullable Fragment fragment, String tag) {

        ft = getSupportFragmentManager().beginTransaction();

/*        if (args != null && tag == FRAGMENT_LIST_ITEM) {

            fragment = new ItemFragment();

        }*/


        ft.replace (id, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();

    }
}
