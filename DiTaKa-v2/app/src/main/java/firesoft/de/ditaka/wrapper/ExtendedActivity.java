/*
 *     Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
 *     Copyright (C) 2018  David Schlossarczyk
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     For the full license visit https://www.gnu.org/licenses/gpl-3.0.
 */

package firesoft.de.ditaka.wrapper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import firesoft.de.ditaka.fragments.mainFragment.MainFragment;
import firesoft.de.ditaka.fragments.trayFragment.TrayFragment;
import firesoft.de.ditaka.util.Definitions;

import static firesoft.de.ditaka.util.Definitions.ITEM_FRAGMENT;
import static firesoft.de.ditaka.util.Definitions.TRAY_FRAGMENT;

/**
 * Stellt erweitere Funktion für Acitivities bereit
 */
public class ExtendedActivity extends AppCompatActivity {

    public FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createFragmentManager();
    }

    private void createFragmentManager() {
        fManager = this.getSupportFragmentManager();
    }

    /**
     * Provides a method to switch Fragments in a single frame. In this project its abused to show the fragments.
     * @param id ID of the target view
     * @param fragment fragment to display
     * @param tag tag of the fragment defined in ExtendedActivity
     */
    public void switchFragment(int id, @Nullable Fragment fragment, int tag) {

        FragmentTransaction ft = fManager.beginTransaction();
        boolean newFragment = false;

        if (fragment == null) {
            fragment = fManager.findFragmentByTag(String.valueOf(tag));

            if (fragment == null) {
                newFragment = true;
            }
        }
        try {
            switch (tag) {

                case TRAY_FRAGMENT:
                    if (newFragment) {
                        fragment = new TrayFragment();
                    }
                    break;

//                case ITEM_FRAGMENT:
//                    if (newFragment) {
//                        fragment = new MainFragment();
//                    }
//                    break;

                default:
                    throw new IllegalArgumentException("Unable to parse given tag to fragment!");

            }

        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }

        ft.replace(id, fragment, String.valueOf(tag));
        ft.addToBackStack(String.valueOf(tag));
        ft.commit();

    }

}
