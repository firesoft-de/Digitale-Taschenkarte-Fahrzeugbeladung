package dresden.de.blueproject;

import java.util.ArrayList;

public class Util_Data {


    /**
     * @findItemsByTray sucht alle zu einem Behälter passenden Gegenstände und gibt diese als ArrayListe aus
     * @param tItem der Behälter zu dem gesucht werden soll
     * @param mainItems die Liste mit allen verfügbaren, gespeicherten Gegenständen
     * @return ArrayList mit den gefundenen Gegenständen
     */
    public static ArrayList<EquipmentItem> findItemsByTray(TrayItem tItem, ArrayList<EquipmentItem> mainItems) {

        ArrayList<EquipmentItem> items = new ArrayList<>();
        
        int searchId = tItem.getID();

        for (int i=0;i<mainItems.size();i++) {

            EquipmentItem currentItem =  mainItems.get(i);

            if (searchId ==currentItem.getCategoryId()) {

                items.add(currentItem);

            }
        }

        return items;

    }

}
