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


    public int getId() {return id;}
    public String getName() {return name;}
    public String getPosition() {return position;}

    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setPosition(String position) {this.position = position;}

}
