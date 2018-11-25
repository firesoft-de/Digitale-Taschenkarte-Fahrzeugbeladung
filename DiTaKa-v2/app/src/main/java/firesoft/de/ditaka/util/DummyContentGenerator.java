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

package firesoft.de.ditaka.util;

import java.util.ArrayList;

import firesoft.de.ditaka.datamodels.Item;
import firesoft.de.ditaka.datamodels.Tray;

/**
 * Diese Klasse enthält Methoden um beispielhafte Inhalte zu generieren
 **/
public class DummyContentGenerator {

    /**
     * Erzeugt eine Beipielliste mit Items
     */
    public static ArrayList<Item> generateDummyItemList() {

        ArrayList<Item> list = new ArrayList<>();
        list.add(new Item(0, 0, "Item1","Beispielbeschreibung1", "Beispielfach 1 - Mitte", "A,B,C",",", null,null,null));
        list.add(new Item(1, 0, "Item2","Beispielbeschreibung2", "Beispielfach 2 - Mitte", "A,B,C",",", null,null,null));
        list.add(new Item(2, 0, "Item3","Beispielbeschreibung3", "Beispielfach 3 - Mitte", "A,B,C",",", null,null,null));

        return list;
    }

    /**
     * Erzeugt eine Beispielliste mit Trays
     * @return
     */
    public static ArrayList<Tray> generateDummyTrayList() {

        ArrayList<Tray> list = new ArrayList<>();
        list.add(new Tray(0, 0, "Tray1","Beispielbeschreibung1","Beispielfach 1 - Mitte","A,B,C",",",null));
        list.add(new Tray(0, 0, "Tray2","Beispielbeschreibung2","Beispielfach 2 - Mitte","A,B,C",",",null));
        list.add(new Tray(0, 0, "Tray3","Beispielbeschreibung3","Beispielfach 3 - Mitte","A,B,C",",",null));

        return list;
    }

}
