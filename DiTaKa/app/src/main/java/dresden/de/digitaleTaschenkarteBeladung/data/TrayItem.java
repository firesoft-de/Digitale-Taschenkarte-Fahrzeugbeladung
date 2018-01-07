package dresden.de.digitaleTaschenkarteBeladung.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Diese Klasse implementiert die Datenstruktur für ein Fach
 */
@Entity(tableName = "tray")
public class TrayItem {

    //Interne Variablen

    //ID
    @PrimaryKey
    private int id;

    //Name
    private String name;

    //Beschreibung
    private String description;

    //Zweite Beschreibung/Anmerkungen
    private String descriptionTwo;

    //Die Koordinaten für die Markierung der Itempositionen
    //Die vier Koordinaten werden als vier unabhängige Einträge angelegt. D.h. beim Abrufen muss der ausgegebene Index mit 4 multipliziert werden und dann dieser sowie
    // die nachfolgenden drei abgefragt werden um die richtigen Koordinaten zu erhalten
    @ColumnInfo(name = "positions")
    @TypeConverters(Converters.class)
    private ArrayList<Integer> positionCoordinates;

    //Ein Bild
//    private Image mImage;


    //Konstruktor
    /**
     * Initialisiert die Datenklasse
     * @param id ID des Behälters/der Kategorie
     * @param name Öffentlicher Name
     * @param description Eine Beschreibung
     */
    public TrayItem(int id, String name, String description) {

        this.id = id;
        this.name = name;
        this.description = description;

    }


    //Get-Methoden
    public int getId() {return id;}

    public String getName() {return name;}

    public String getDescription() {return  description;}

    public String getDescriptionTwo() {return descriptionTwo;}

//    public Image getImage() {return mImage;}

    public ArrayList<Integer> getPositionCoordinates() {
        return positionCoordinates;
    }

    //Set-Methoden
    public void setName(String name) {}

    public void setId(int id) {}

    public void setDescriptionTwo(String description) {descriptionTwo = description;}

//    public void setImage(Image image) {mImage = image;}

    public void setPositionCoordinates(ArrayList<Integer> positionCoordinates) {
        this.positionCoordinates = positionCoordinates;
    }

    public void positionCoordFromString(String positions) {

        //Pattern:
        // PositionsID:coordLinks-coordOben-coordRechts-coordUnten;PositionsID2:coorLinks2- etc.

        if (!positions.equals("")) {
            String[] coords = positions.split(";");

            if (positionCoordinates == null) {
                positionCoordinates = new ArrayList<>();
            }

            for (String coord : coords) {
                String[] coordQuery = coord.split(":");
                String[] singleCoord = coordQuery[1].split("-");
                for (String s : singleCoord
                        ) {
                    positionCoordinates.add(new Integer(s));
                }
            }
        }
    }
}
