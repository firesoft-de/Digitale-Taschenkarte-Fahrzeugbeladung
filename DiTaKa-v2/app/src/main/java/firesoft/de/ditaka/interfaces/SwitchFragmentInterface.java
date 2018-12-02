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

package firesoft.de.ditaka.interfaces;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Interface welches eine Methode zum Wechseln der aktuell angezeigten Fragments enthält
 */
public interface SwitchFragmentInterface {

    /**
     * Provides a method to switch Fragments in a single frame. In this project its abused to show the fragments.
     * @param id ID of the target view
     * @param fragment fragment to display
     * @param tag tag of the fragment defined in ExtendedActivity
     */
    void switchFragment(int id, @Nullable Fragment fragment, int tag);

}
