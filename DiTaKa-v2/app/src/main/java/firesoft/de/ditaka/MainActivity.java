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

package firesoft.de.ditaka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import javax.inject.Inject;
import javax.inject.Singleton;

import firesoft.de.ditaka.dagger.CustomViewmodelFactory;
import firesoft.de.ditaka.dagger.InjectableApplication;
import firesoft.de.ditaka.interfaces.SwitchFragmentInterface;
import firesoft.de.ditaka.wrapper.ExtendedActivity;

import static firesoft.de.ditaka.util.Definitions.TRAY_FRAGMENT;

public class MainActivity extends ExtendedActivity {

    @Inject
    CustomViewmodelFactory factory;

    @Inject
    SwitchFragmentInterface switchFragmentOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Application für Dagger bereitstellen
        ((InjectableApplication) getApplication()).getComponent().inject(this);

        displayFirstFragment();
    }

    // region Hilfsmethoden

    /**
     * Zeigt das erste Fragment an. Beim ersten Start soll ein Informationsbildschirm über die App angezeigt werden.
     * Bei allen weiteren Starts anfänglich das Fragment mit der Trayliste und später dann die Fahrzeug- oder Gruppenliste
     */
    private void displayFirstFragment() {

        switchFragment(R.id.MainFrame,null,TRAY_FRAGMENT);

    }

    // endregion


}
