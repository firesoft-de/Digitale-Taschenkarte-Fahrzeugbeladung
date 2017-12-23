package dresden.de.blueproject;


import android.media.Image;

/**
 * {@link EquipmentItem} Diese Klasse implementiert die Datenstruktur für einen einzelnen Ausstattungsgegenstand
 */
public class EquipmentItem {
    //TODO Implementieren

    //Interne Variablen

    //ID
    private int mID;

    //Name
    private String mName;

    //Beschreibung
    private String mDescription;

    //ID des zugehörigen Behälters/Kategorie
    private int mCategoryId;

    //Zusätzliche Beschreibung, bspw. für Ausrüstungssätze
    private String mSetName;

    //Enthält ein Bild des Items
    private Image mImage;

    //Verzeichnet die Position des Items
    private String mPosition;

    //Konstruktor

    /**
     * Datenklasse initialiseren
     * @param id ID des Items
     * @param name Name des Items
     * @param description Beschreibung zum Item
     * @param position Position des Items
     * @param categoryId Id des zugehörigen Behälters
     */
    public EquipmentItem(int id, String name, String description, String position, int categoryId) {

        mID = id;
        mName = name;
        mDescription = description;
        mPosition = position;
        mCategoryId = categoryId;

    }

    /**
     * Datenklasse initialiseren
     * @param id ID des Items
     * @param name Name des Items
     * @param description Beschreibung zum Item
     * @param setName Der Name des Ausstattungssatzes
     * @param position Position des Items
     * @param categoryId Id des zugehörigen Behälters
     */
    public EquipmentItem(int id, String name, String description, String setName, String position, int categoryId) {

        mID = id;
        mName = name;
        mDescription = description;
        mSetName = setName;
        mPosition = position;
        mCategoryId = categoryId; }

    //Get Methoden

    public String getName() {return mName;}

    public String getDescription() {return mDescription;}

    public  int getId() {return mID;}

    public String getPosition() {return mPosition;}

    public int getCategoryId() {return mCategoryId;}

    public String getSetName() {return mSetName;}

    //Set Methoden

    public void setSetName(String setName) {mSetName = setName;}

    public void setImage(Image image) {mImage = image;}

}
