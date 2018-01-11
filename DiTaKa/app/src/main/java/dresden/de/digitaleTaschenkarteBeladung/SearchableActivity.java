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
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.ApplicationForDagger;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseEquipmentMininmal;
import dresden.de.digitaleTaschenkarteBeladung.fragments.DetailFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.ItemFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.SearchResultListFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.TrayFragment;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.SearchViewModel;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util.FRAGMENT_DETAIL;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util.FRAGMENT_LIST_ITEM;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util.LogError;

public class SearchableActivity extends AppCompatActivity  implements TrayFragment.fragmentCallbackListener{

    private static final String LOG_TAG="SearchableActivity_LOG";

    private static final String FRAGMENT_SEARCH_LIST = "201";
    private static final String FRAGMENT_SEARCH_DETAIL = "202";

    ArrayList<EquipmentItem> equipmentItems;

    SearchViewModel viewModel;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    //Diese Variable wird zum Speichern des Scroll Index der ListView gebraucht
    Parcelable state;

    public MutableLiveData<List<DatabaseEquipmentMininmal>> liveItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        //liveItemList initalisieren
        liveItemList = new MutableLiveData<>();

        //Titel setzen und zurückbutton einschalten
        this.getSupportActionBar().setTitle(R.string.search_title);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Anweisung an Dagger, dass hier eine Injection vorgenommen wird ??
        ((ApplicationForDagger) this.getApplication())
                .getApplicationComponent()
                .inject(this);

        viewModel = ViewModelProviders.of(this,viewModelFactory)
                .get(SearchViewModel.class);

        //Search-Intent abrufen und auswerten
        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            Bundle bundle = intent.getExtras();

            try {
                equipmentItems =  bundle.getParcelableArrayList("EL");
            } catch (NullPointerException e) {
                e.printStackTrace();
                LogError(LOG_TAG, "Fehler beim Abrufen des Bundles in der SearchActivity! Cause: "+e.getCause() + " | Nachricht: " + e.getMessage());
            }

            SearchResultListFragment fragment = new SearchResultListFragment();

            android.support.v4.app.FragmentManager fragmentManager = this.getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //List anzeigen
            fragmentTransaction.replace(R.id.searchFrame,fragment,FRAGMENT_SEARCH_LIST);
            fragmentTransaction.addToBackStack(FRAGMENT_SEARCH_LIST);
            fragmentTransaction.commit();


            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            search(searchQuery);
        }
    }

    @Override
    public void switchFragment(int id, Fragment fragment, String tag) {

        android.support.v4.app.FragmentManager fManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft;
        ft = fManager.beginTransaction();


        boolean newFragment = false;

        if (fragment == null) {
            fragment = fManager.findFragmentByTag(tag);

            if (fragment == null) {
                newFragment = true;
            }
        }

        switch (tag) {

            case FRAGMENT_DETAIL:
                if (newFragment) {
                    fragment = new DetailFragment();
                    Toast.makeText(getApplicationContext(), "Hier muss noch ein DetailFragment gebaut werden!",Toast.LENGTH_LONG).show();
                }
                this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;

            case FRAGMENT_LIST_ITEM:
                if (newFragment) {
                    fragment = new ItemFragment();
                }
                this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
        }

        ft.replace(id, fragment, tag);
        ft.addToBackStack(tag);
        ft.commit();

    }

    //Hier werden die Click-Events für die Menuitems gehandelt
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                handleBackButton();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * handleBackButton bearbeitet das Zurückspringen auf ein vorheriges Fragment und nimmt notwendige Veränderungen an der ActionBar vor
     */
    private void handleBackButton() {

        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        Fragment currentFragment =  manager.findFragmentById(R.id.searchFrame);

        if (manager.getBackStackEntryCount() > 0) {
            //Sind im BackStack Einträge vorhanden?

            //Im BackStack einen Schritt zurück gehen
//            manager.popBackStackImmediate();

            //Herausfinden welches Fragment angezeigt wurde um dann entsprechend die Titelleiste einzustellen
            switch (currentFragment.getTag()) {

                case (FRAGMENT_DETAIL):
                    this.getSupportActionBar().setTitle(R.string.search_title);
                    manager.popBackStackImmediate(FRAGMENT_SEARCH_LIST, 0);
                    break;

                case (FRAGMENT_SEARCH_LIST):
                    //Zurückspringen in die ListView
                    returnToMainActivity();
                    break;

                default:
                    //Überprüfen ob das nächste Fragment das TrayFragment ist. Die Überprüfung der backStackCount Größe ist notwendig um zu verhindern, dass ein Index < 0 abgerufen wird
                    int backStackCount = manager.getBackStackEntryCount();
                    String backStackTag;
                    if (backStackCount >= 2) {
                        backStackTag = manager.getBackStackEntryAt(backStackCount - 2).getName();
                        if (backStackTag == FRAGMENT_LIST_ITEM) {
                            //Nichts tun
                            this.getSupportActionBar().setTitle(R.string.search_title);
                        } else {
                            //Den passenden Namen zuweisen
                            setTitle(backStackTag);
                        }
                    }
                    break;
            }
        }
    }

    private void search(String query) {

        //Suchanfrange aufbereiten
        String[] keys = (query.toLowerCase()).split(" ");

        for (int i = 0; i<keys.length; i++) {
            String searchQuery = "%" + keys[i] + "%";

            //Suchanfrage über das viewModel an die Datenbank schicken
            viewModel.searchItems(searchQuery).observe(this, new Observer<List<DatabaseEquipmentMininmal>>() {
                @Override
                public void onChanged(@Nullable List<DatabaseEquipmentMininmal> databaseEquipmentMininmals) {
                    //Durch das suchen nach mehreren Suchbegriffen kann diese Methode mehrfach aufgerufen werden
                    //Die liveItemList muss daher auch mehrere Listen kombinieren können -> Dies wird im SearchListFragment durchgeführt
                   liveItemList.postValue(databaseEquipmentMininmals);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        Fragment currentFragment =  manager.findFragmentById(R.id.searchFrame);

        if (currentFragment.getTag() == FRAGMENT_SEARCH_LIST) {
            returnToMainActivity();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        ListView lv = this.findViewById(R.id.ListViewSearch);

        state = lv.onSaveInstanceState();
        super.onPause();
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);
    }
}
