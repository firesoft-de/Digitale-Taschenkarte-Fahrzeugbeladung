package dresden.de.blueproject.dataStructure;


import android.arch.persistence.room.TypeConverter;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dresden.de.blueproject.data.DatabaseEquipmentObject;

/**
 * {@link EquipmentItem} Diese Klasse implementiert die Datenstruktur für einen einzelnen Ausstattungsgegenstand
 */
public class EquipmentItem implements Parcelable {
    //Interne Variablen

    private static  final String LOG_TAG="EquipmentItem_LOG";

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

    //Wird für die Suche verwendet
    private String[] mKeywords;


    public static final Parcelable.Creator<EquipmentItem> CREATOR
            = new Parcelable.Creator<EquipmentItem>() {
        public EquipmentItem createFromParcel(Parcel in) {
            return new EquipmentItem(in);
        }

        public EquipmentItem[] newArray(int size) {
            return new EquipmentItem[size];
        }
    };


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
    public EquipmentItem(int id, String name, String description, String setName, String position, int categoryId, String[] keywords) {

        mID = id;
        mName = name;
        mDescription = description;
        mSetName = setName;
        mPosition = position;
        mCategoryId = categoryId;
        mKeywords = keywords;
        mSetName = "";
        mImage = null;


    }


    //FÜr Parcelable
    public EquipmentItem(Parcel input) {
        mID = input.readInt();
        mName = input.readString();
        mDescription = input.readString();
        mPosition = input.readString();
        mCategoryId = input.readInt();
        mSetName = input.readString();


        mKeywords= input.createStringArray();
        Log.d(LOG_TAG,"Parcelinformation gelesen");

    }

    //Get Methoden

    public String getName() {return mName;}

    public String getDescription() {return mDescription;}

    public  int getId() {return mID;}

    public String getPosition() {return mPosition;}

    public int getCategoryId() {return mCategoryId;}

    public String getSetName() {return mSetName;}

    public String[] getKeywords() {return mKeywords;}

    //Set Methoden

    public void setSetName(String setName) {mSetName = setName;}

    public void setImage(Image image) {mImage = image;}

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mID);
        parcel.writeString(mName);
        parcel.writeString(mDescription);
        parcel.writeString(mPosition);
        parcel.writeInt(mCategoryId);
        parcel.writeString(mSetName);
        parcel.writeStringArray(mKeywords);
    }

    @Override
    public int describeContents() {
         return 0;
    }

    public DatabaseEquipmentObject toDatabaseObject() {

       DatabaseEquipmentObject object = new DatabaseEquipmentObject();
       object.id = mID;
       object.name = mName;
       object.position = mPosition;
       List<String> tmp =  Arrays.asList(mKeywords);
       object.keywords = new ArrayList<String>();
       object.keywords.addAll(tmp);
       object.categoryId = mCategoryId;
       object.setName = mSetName;

        return object;
    }

}

class Converters {
    @TypeConverter
    public static ArrayList<String> fromJSON(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArray(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}