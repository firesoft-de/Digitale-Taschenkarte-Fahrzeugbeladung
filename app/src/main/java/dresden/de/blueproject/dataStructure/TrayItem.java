package dresden.de.blueproject.dataStructure;

import android.media.Image;

/**
 * Diese Klasse implementiert die Datenstruktur für ein Fach
 */
public class TrayItem {

    //Interne Variablen

    //ID
    private int mID;

    //Name
    private String mName;

    //Beschreibung
    private String mDescription;

    //Zweite Beschreibung/Anmerkungen
    private String mDescriptionTwo;

    private String mPosition;

    //Ein Bild
    private Image mImage;


    //Konstruktor
    /**
     * Initialisiert die Datenklasse
     * @param id ID des Behälters/der Kategorie
     * @param name Öffentlicher Name
     * @param description Eine Beschreibung
     */
    public TrayItem(int id, String name, String description) {

        mID = id;
        mName = name;
        mDescription = description;

    }


    //Get-Methoden
    public int getID() {return mID;}

    public String getName() {return mName;}

    public String getDescription() {return  mDescription;}

    public String getDescriptionTwo() {return mDescriptionTwo;}

    public Image getImage() {return mImage;}

    public String getPosition() {return mPosition;}

    //Set-Methoden

    public void setDescriptionTwo(String description) {mDescriptionTwo = description;}

    public void setImage(Image image) {mImage = image;}

    public void setPosition(String position) {mPosition = position;}

}
