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

package firesoft.de.ditaka.fragments;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.support.v4.util.Pair;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import firesoft.de.ditaka.datamodels.Item;

import static org.junit.Assert.*;

public class ListViewItemViewmodelShould {

    private Item testItem;
    private Item testTray;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {

        testItem = new Item(0,"Testitem","Testdescription","testtag2,testtag2",",","Beispiellocation",null, null,null, 0);
        testTray = new Item(0,"Testitem","Testdescription","testtag2,testtag2",",","Beispiellocation",null, null,null, 0);

    }

    @Test
    public void getLocationFromItem() {

        ListViewItemViewmodel model = new ListViewItemViewmodel(testItem);

        assertEquals(model.location.getValue(), "Beispiellocation");

    }

    @Test
    public void getLocationFromTray() {

        ListViewItemViewmodel model = new ListViewItemViewmodel(testItem);

        assertEquals(model.location.getValue(), "Beispiellocation");

    }


}