/*/*  Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
    Copyright (C) 2017  David Schlossarczyk

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    For the full license visit https://www.gnu.org/licenses/gpl-3.0.*/

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
