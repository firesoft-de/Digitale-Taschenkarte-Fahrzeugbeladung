package dresden.de.blueproject;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;


//Es besteht die Möglichkeit bestimmte Felder zu indizieren. Damit kann u.U. der Suchvorgang erheblich beschleunigt werden. -> Siehe Android Doku

/**
 * Datenklasse zur Definition der in der Datenbank zu speichernden Informationen
 */
@Entity(tableName = "equipment")
public class DatabaseEquipmentObject {
    @PrimaryKey
    public int id;

    //Fields für EquipmentItem
    public String name;
    public String description;
    public String position;
    public int categoryId;
    public String setName;
    public ArrayList<String> keywords;

    @Ignore
    public String imagePath;

}

class DatabaseEquipmentMininmal {

    @PrimaryKey
    public int id;

    public String name;
    public String position;

//    public DatabaseEquipmentMininmal(int id, String Name, String Position) {
//        id = id;
//        name = Name;
//        position = Position;
//    }

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