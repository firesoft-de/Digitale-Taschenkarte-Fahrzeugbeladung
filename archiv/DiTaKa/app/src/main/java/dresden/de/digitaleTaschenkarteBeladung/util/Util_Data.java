/*  Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
    Copyright (C) 2017  David Schlossarczyk

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    For the full license visit https://www.gnu.org/licenses/gpl-3.0.*/

package dresden.de.digitaleTaschenkarteBeladung.util;

import java.util.ArrayList;
import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;

public class Util_Data {


    /**
     * @findItemsByTray sucht alle zu einem Behälter passenden Gegenstände und gibt diese als ArrayListe aus
     * @param tItem der Behälter zu dem gesucht werden soll
     * @param mainItems die Liste mit allen verfügbaren, gespeicherten Gegenständen
     * @return ArrayList mit den gefundenen Gegenständen
     */
    public static ArrayList<EquipmentItem> findItemsByTray(TrayItem tItem, ArrayList<EquipmentItem> mainItems) {

        ArrayList<EquipmentItem> items = new ArrayList<>();
        
        int searchId = tItem.getId();

        for (int i=0;i<mainItems.size();i++) {

            EquipmentItem currentItem =  mainItems.get(i);

            if (searchId ==currentItem.getCategoryId()) {

                items.add(currentItem);

            }
        }

        return items;

    }

    public static EquipmentItem[] castItemToArray(List<EquipmentItem> items) {

        EquipmentItem[] result = new EquipmentItem[items.size()];

        for (int i=0;i<items.size();i++) {
            result[i] = items.get(i);
        }

        return result;
    }

    public static TrayItem[] castTrayToArray(List<TrayItem> items) {

        TrayItem[] result = new TrayItem[items.size()];

        for (int i=0;i<items.size();i++) {
            result[i] = items.get(i);
        }

        return result;
    }

    public static ImageItem[] castImageToArray(List<ImageItem> items) {

        ImageItem[] result = new ImageItem[items.size()];

        for (int i=0;i<items.size();i++) {
            result[i] = items.get(i);
        }

        return result;
    }

}
