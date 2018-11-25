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

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import firesoft.de.ditaka.util.*;

import firesoft.de.ditaka.datamodels.BaseDataClass;
import firesoft.de.ditaka.datamodels.Group;
import firesoft.de.ditaka.datamodels.Item;
import firesoft.de.ditaka.datamodels.Tray;
import firesoft.de.ditaka.datamodels.Vehicle;

import static org.junit.Assert.*;

public class ArrayListCoverterShould {

    ArrayList<Item> items;
    ArrayList<Tray> trays;
    ArrayList<BaseDataClass> baseDataClasses;
    ArrayList<Vehicle> vehicles;
    ArrayList<Group> groups;

    @Before
    public void setUp() {

        // Listen initalisieren
        items = new ArrayList<>();
        trays = new ArrayList<>();
        baseDataClasses = new ArrayList<>();
        vehicles = new ArrayList<>();
        groups = new ArrayList<>();

        // Setup Items
        items.add(new Item(0,"A","DescA","A,B,C",",","Test",null,null,null,0));
        items.add(new Item(1,"B","DescA","A,B,C",",","Test",null,null,null,0));
        items.add(new Item(2,"C","DescA","A,B,C",",","Test",null,null,null,0));


    }

    @Test
    public void convertItemListToBaseDataList() {

        ArrayList<BaseDataClass> base =  ArrayListCoverter.convertToBaseData(items);

        assertEquals(base.get(0).getName(),items.get(0).getName());
    }


    @Test
    public void testConversion() {

        // Nur eine kurze Prüfung ob das konvertieren überhaupt funktioniert

        Item item = new Item(0,"A","DescA","A,B,C",",","Test",null,null,null,0);
        BaseDataClass baseDataClass = (BaseDataClass) item;

        assertEquals(baseDataClass.getName(),"A");

    }

}