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

import firesoft.de.ditaka.datamodels.BaseDataClass;
import firesoft.de.ditaka.datamodels.Group;
import firesoft.de.ditaka.datamodels.Item;
import firesoft.de.ditaka.datamodels.Tray;
import firesoft.de.ditaka.datamodels.Vehicle;

import static android.media.CamcorderProfile.get;

/**
 * Stellt eine Methode bereit, mit der eine ArrayList mit Elementen vom Typ Item, Tray, Group oder Vehicle in eine ArrayList vom Typ BaseData konvertiert werden kann.
 */
public class ArrayListCoverter {

    /**
     * Konvertiert die Liste der Klasse in eine ArrayList vom Typ BaseDataClass.
     */
    public static <T> ArrayList<BaseDataClass> convertToBaseData(ArrayList<T> list) {

        ArrayList<BaseDataClass> convertedList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) != null) {
                convertedList.add((BaseDataClass) list.get(i));
            }
        }

        return convertedList;
    }

    /**
     * Konvertiert die Liste der Klasse in eine ArrayList vom Typ BaseDataClass.
     *//*
    public static ArrayList<BaseDataClass> convertToBaseData(ArrayList<Tray> list) {

        ArrayList<BaseDataClass> convertedList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) != null) {
                convertedList.add(list.get(i));
            }
        }

        return convertedList;
    }

    *//**
     * Konvertiert die Liste der Klasse in eine ArrayList vom Typ BaseDataClass.
     *//*
    public static ArrayList<BaseDataClass> convertToBaseData(ArrayList<Vehicle> list) {

        ArrayList<BaseDataClass> convertedList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) != null) {
                convertedList.add(list.get(i));
            }
        }

        return convertedList;
    }

    *//**
     * Konvertiert die Liste der Klasse in eine ArrayList vom Typ BaseDataClass.
     *//*
    public static ArrayList<BaseDataClass> convertToBaseData(ArrayList<Group> list) {

        ArrayList<BaseDataClass> convertedList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) != null) {
                convertedList.add(list.get(i));
            }
        }

        return convertedList;
    }
*/
}
