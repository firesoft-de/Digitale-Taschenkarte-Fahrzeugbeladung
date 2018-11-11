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
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
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
@TypeConverters({ItemConverter.class})
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
    private String setName;

    private int count;

    //Enthält ein Bild des Items
//    private Image image;

    //Verzeichnet die Position des Items
    private String position;

    //Wird für die Suche verwendet
    private ArrayList<String> keywords;

    //Zusätzliche Hinweise zum Gerät
    private String additionalNotes;

    private String group;

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
        this.setName = setName;
    }


    //Für Parcelable
    public EquipmentItem(Parcel input) {
        id = input.readInt();
        name = input.readString();
        description = input.readString();
        position = input.readString();
        categoryId = input.readInt();
        setName = input.readString();
        count = input.readInt();

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

    public String getSetName() {return setName;}

    public ArrayList<String> getKeywords() {return keywords;}

    public String getAdditionalNotes() {return additionalNotes;}

    public int getPositionIndex() {return positionIndex;}

    public String getGroup() {return group;}

    public int getCount() {
        return count;
    }

    //Set Methoden

    public void setName(String name) {this.name = name;}

    public void setDescription(String description) {this.description = description;}

    public void setPosition(String position) {this.position = position;}

    public void setCategoryId(int categoryId) {this.categoryId = categoryId;}

    public void setSetName(String setName) {this.setName = setName;}

    public void setKeywords(ArrayList<String> keywords) {this.keywords = keywords;}

    public  void setAdditionalNotes(String additionalNotes) {this.additionalNotes = additionalNotes;}

    public void setKeywordsFromArray(String[] keywords) {
        ArrayList<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(keywords));
        this.keywords = list;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setGroup(String group) {this.group = group;}

    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(position);
        parcel.writeInt(categoryId);
        parcel.writeString(setName);
        parcel.writeInt(count);
        parcel.writeStringArray((String[]) keywords.toArray());
    }

    @Override
    public int describeContents() {
         return 0;
    }

//  public void fromMinimal(DatabaseEquipmentMininmal minimal) {
//      this.id = minimal.id;
//      this.name = minimal.name;
//      this.position = minimal.position;
//  }

}

