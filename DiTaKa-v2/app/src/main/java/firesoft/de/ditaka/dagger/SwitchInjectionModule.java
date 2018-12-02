/*
 * Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
 * Copyright (C) 2018  David Schlossarczyk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * For the full license visit https://www.gnu.org/licenses/gpl-3.0.
 */

package firesoft.de.ditaka.dagger;

import android.app.Application;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import firesoft.de.ditaka.interfaces.SwitchFragmentInterface;

/**
 * Dieses Modul wird verwendet, um den SwitchOperator der in den Fragments benötigt wird, bereitzustellen.
 */
@Module
public class SwitchInjectionModule {

    private SwitchFragmentInterface switchFragmentOperator;

    SwitchInjectionModule() //
    {
        // Ein leeres Interface erstellen, um die Abhängigkeit solange zu befriedigen bis die MainActivity sich selbst hier reinschreiben kann.
        switchFragmentOperator = new SwitchFragmentInterface() {
            @Override
            public void switchFragment(int id, @Nullable Fragment fragment, int tag) {

            }
        };
    }

    @Provides
    @Singleton
    SwitchFragmentInterface provideSwitchFragmentOperator() {return switchFragmentOperator;}

}
