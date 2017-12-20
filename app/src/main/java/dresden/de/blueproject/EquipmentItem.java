package dresden.de.blueproject;


import android.media.Image;

/**
 * {@link EquipmentItem} Diese Klasse implementiert die Datenstruktur für einen einzelnen Ausstattungsgegenstand
 */
public class EquipmentItem {
    //TODO Implementieren

    //Interne Variablen

    String mName;

    String mDescription;

    int mID;

    //Enthält ein Bild des Items
    Image mImage;

    //Verzeichnet die Position des Items
    String mPosition;

    /**
     * Datenklasse initialiseren
     * @param id ID des Items
     * @param name Name des Items
     * @param description Beschreibung zum Item
     */
    public void EquitmentItem(int id, String name, String description, String position) {

        mID = id;
        mName = name;
        mDescription = description;
        mPosition = position;

    }




}
