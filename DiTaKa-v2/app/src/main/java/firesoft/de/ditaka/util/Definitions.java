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

package firesoft.de.ditaka.util;

/**
 * Diese Klasse enthält verschiedenen Definitionen und Konstanten
 */
public class Definitions {

    // region Loader

    // Diese Konstanten definieren die verschiedenen Loader-ID's
    public static final int HTTP_LOADER = 1;
    public static final int DATA_LOADER = 2;

    // endregion


    // region Preferences

    // Hier sind alle Konstanten zur Zuordnungen der Preferences-Variablen abgelegt.
    public static final String PREFS = "firesoft.de.ditaka";
    public static final String URL = "url";
    public static final String USER = "user";
    public static final String PASSWORD = "zulu";

    // endregion


    // region Filesystem

    public static final String FILE_DESTINATION_IMAGE = "image";

    // endregion

}
