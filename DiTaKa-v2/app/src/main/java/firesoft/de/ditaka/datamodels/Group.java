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

package firesoft.de.ditaka.datamodels;

/**
 * Stellt eine Datenstruktur für eine Gruppe bereit.
 */
public class Group extends BaseDataClass{

    // #region Konstruktoren

    /**
     * Erstellt eine neue Instanz vom Typ Group
     */

    /**
     * Erstellt eine neue Instanz vom Typ Group
     * @param id ID der Gruppe
     * @param name Name der Gruppe
     * @param tags Tags (als String mit Trennzeichen, bspw. "tag1;tag2;tag3")
     * @param tagDelimiter Trennzeichen der Tags(";")
     * @param description Beschreibung zur Gruppe
     */
    public Group(int id, String name, String tags, String tagDelimiter, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        setTags(tags,tagDelimiter);

    }

    // #endregion

    // #region Getter / Setter für Variablen

    // #endregion

}
