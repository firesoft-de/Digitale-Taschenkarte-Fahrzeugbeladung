/*  Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
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


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

@Entity(tableName = "image")
public class ImageItem {

    @PrimaryKey
    private int id;

    private String path;
    private int categoryID;

//    @Ignore
//    private Bitmap image;

    public ImageItem(int id, String path, int categoryID) {
        this.id = id;
        this.path = path;
        this.categoryID = categoryID;
    }

    public void setId(int id) {}
    public void setPath(String path) {this.path = path;}
    public void setCategoryID(int categoryID) {this.categoryID = categoryID;}
//    public void setImage(Bitmap bitmap) {this.image = bitmap;}

    public int getId() {return id;}
    public String getPath() {return path;}
    public int getCategoryID() {return categoryID;}


//    public Bitmap getImage() {return image;}
}
