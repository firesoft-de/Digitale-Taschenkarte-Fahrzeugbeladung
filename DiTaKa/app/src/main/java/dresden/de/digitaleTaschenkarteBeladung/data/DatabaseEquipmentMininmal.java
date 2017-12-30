package dresden.de.digitaleTaschenkarteBeladung.data;

import android.arch.persistence.room.PrimaryKey;

public class DatabaseEquipmentMininmal {

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
