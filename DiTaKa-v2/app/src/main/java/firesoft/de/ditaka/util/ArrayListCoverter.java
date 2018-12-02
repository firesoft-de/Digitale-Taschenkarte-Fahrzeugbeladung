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

import firesoft.de.ditaka.datamodels.BasicData;

import static android.media.CamcorderProfile.get;

/**
 * Stellt eine Methode bereit, mit der eine ArrayList mit Elementen vom Typ Item, Tray, Group oder Vehicle in eine ArrayList vom Typ BaseData konvertiert werden kann.
 */
public class ArrayListCoverter {

    /**
     * Konvertiert die Liste der Klasse in eine ArrayList vom Typ BasicData.
     */
    public static <T> ArrayList<BasicData> convertToBasicData(ArrayList<T> list) {

        ArrayList<BasicData> convertedList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) != null) {
                convertedList.add((BasicData) list.get(i));
            }
        }

        return convertedList;
    }

    /**
     * Konvertiert die Liste der Klasse in eine ArrayList vom Typ BasicData.
     *//*
    public static ArrayList<BasicData> convertToBasicData(ArrayList<Tray> list) {

        ArrayList<BasicData> convertedList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) != null) {
                convertedList.add(list.get(i));
            }
        }

        return convertedList;
    }

    *//**
     * Konvertiert die Liste der Klasse in eine ArrayList vom Typ BasicData.
     *//*
    public static ArrayList<BasicData> convertToBasicData(ArrayList<Vehicle> list) {

        ArrayList<BasicData> convertedList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) != null) {
                convertedList.add(list.get(i));
            }
        }

        return convertedList;
    }

    *//**
     * Konvertiert die Liste der Klasse in eine ArrayList vom Typ BasicData.
     *//*
    public static ArrayList<BasicData> convertToBasicData(ArrayList<Group> list) {

        ArrayList<BasicData> convertedList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i) != null) {
                convertedList.add(list.get(i));
            }
        }

        return convertedList;
    }
*/
}
