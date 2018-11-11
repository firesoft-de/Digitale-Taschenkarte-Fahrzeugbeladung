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

package firesoft.de.ditaka.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import firesoft.de.ditaka.datamodels.Item;

@Singleton
public class ItemModel {

    @Inject
    ArrayList<Item> itemList;

    @Inject
    public ItemModel() {

    }

}
