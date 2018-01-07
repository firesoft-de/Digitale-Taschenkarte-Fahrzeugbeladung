package dresden.de.digitaleTaschenkarteBeladung.data;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util.LogDebug;

/**
 * {@link EquipmentItem} Diese Klasse implementiert die Datenstruktur für einen einzelnen Ausstattungsgegenstand
 */
@Entity(tableName = "equipment")
public class EquipmentItem implements Parcelable {
    //Interne Variablen

    private static  final String LOG_TAG="EquipmentItem_LOG";

    //ID
    @PrimaryKey
    private int id;

    //Name
    private String name;

    //Beschreibung
    private String description;

    //ID des zugehörigen Behälters/Kategorie
    private int categoryId;

    //Zusätzliche Beschreibung, bspw. für Ausrüstungssätze
    private String mSetName;

    //Enthält ein Bild des Items
//    private Image image;

    //Verzeichnet die Position des Items
    private String position;

    //Wird für die Suche verwendet
    private ArrayList<String> keywords;

    //Zusätzliche Hinweise zum Gerät
    private String additionalNotes;

    //Index der Koordinaten für die Positionsmarkierung. Die Liste der Koordinaten wird im TrayItem gespeichert
    private int positionIndex;

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

    @Ignore
    public  EquipmentItem() {

    }

    /**
     * Datenklasse initialiseren
     * @param id ID des Items
     * @param name Name des Items
     * @param description Beschreibung zum Item
     * @param position Position des Items
     * @param categoryId Id des zugehörigen Behälters
     */
    @Ignore
    public EquipmentItem(int id, String name, String description, String position, int categoryId) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.position = position;
        this.categoryId = categoryId;

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
    public EquipmentItem(int id, String name, String description, String setName, String position, int categoryId, ArrayList<String> keywords) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.position = position;
        this.categoryId = categoryId;
        this.keywords = keywords;
        this.mSetName = setName;
//        mImage = null;


    }


    //FÜr Parcelable
    public EquipmentItem(Parcel input) {
        id = input.readInt();
        name = input.readString();
        description = input.readString();
        position = input.readString();
        categoryId = input.readInt();
        mSetName = input.readString();


        String[] keys = input.createStringArray();
        keywords = new ArrayList<>();

        keywords.addAll(Arrays.asList(keys));

        LogDebug(LOG_TAG,"Parcelinformation gelesen");

    }

    //Get Methoden


    public int getId() {return id;}

    public String getName() {return name;}

    public String getDescription() {return description;}

    public String getPosition() {return position;}

    public int getCategoryId() {return categoryId;}

    public String getMSetName() {return mSetName;}

    public ArrayList<String> getKeywords() {return keywords;}

    public String getAdditionalNotes() {return additionalNotes;}

    public int getPositionIndex() {
        return positionIndex;
    }

    //Set Methoden

    public void setId(int id) {}

    public void setName(String name) {this.name = name;}

    public void setDescription(String description) {this.description = description;}

    public void setPosition(String position) {this.position = position;}

    public void setCategoryId(int categoryId) {this.categoryId = categoryId;}

    public void setmSetName(String setName) {this.mSetName = setName;}

    public void setKeywords(ArrayList<String> keywords) {this.keywords = keywords;}

    public  void setAdditionalNotes(String additionalNotes) {this.additionalNotes = additionalNotes;}

    public void setKeywordsFromArray(String[] keywords) {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < keywords.length; i++) {
            list.add(keywords[i]);
        }

        this.keywords = list;
    }

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }



//    public void setImage(Image image) {mImage = image;}

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(position);
        parcel.writeInt(categoryId);
        parcel.writeString(mSetName);
        parcel.writeStringArray((String[]) keywords.toArray());
    }

    @Override
    public int describeContents() {
         return 0;
    }

  /*  public DatabaseEquipmentObject toDatabaseObject() {

       DatabaseEquipmentObject object = new DatabaseEquipmentObject();
       object.id = id;
       object.name = name;
       object.position = position;
       List<String> tmp =  Arrays.asList(keywords);
       object.keywords = new ArrayList<String>();
       object.keywords.addAll(tmp);
       object.categoryId = categoryId;
       object.setName = setName;

        return object;
    }*/

  public void fromMinimal(DatabaseEquipmentMininmal minimal) {
      this.id = minimal.id;
      this.name = minimal.name;
      this.position = minimal.position;
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

    @TypeConverter
    public static ArrayList<Integer> fromJSONToInt(String value) {

        if (value != null) {
            ArrayList<Integer> list = new ArrayList<>();
            JSONArray array = null;

            try {
                array = new JSONArray(value);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                for (int i = 0; i < array.length(); i++) {
                    try {
                        list.add(array.getInt(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return list;
            }
        }
        return null;
    }

    @TypeConverter
    public static String fromArrayToJSON(ArrayList<Integer> list) {
        JSONArray array = new JSONArray();

        if (list != null) {
            for (int value : list) {
                array.put(value);
            }
            return array.toString();
        }
        else {
            return null;
        }
    }
}