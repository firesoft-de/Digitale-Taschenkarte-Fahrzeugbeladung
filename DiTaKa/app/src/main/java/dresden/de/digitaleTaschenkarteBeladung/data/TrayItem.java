package dresden.de.digitaleTaschenkarteBeladung.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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

    private String position;

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

    public String getPosition() {return position;}

    //Set-Methoden
    public void setName(String name) {}

    public void setId(int id) {}

    public void setDescriptionTwo(String description) {descriptionTwo = description;}

//    public void setImage(Image image) {mImage = image;}

    public void setPosition(String position) {this.position = position;}

}
