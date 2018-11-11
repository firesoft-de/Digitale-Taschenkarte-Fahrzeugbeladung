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

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;


/*
Die Klasse stellt Konvertierungsmethoden für das Abspeichern von ArrayLists zur Verfügung
 */
class ItemConverter {
    @TypeConverter
    public static ArrayList<String> fromJSON(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArray(ArrayList<String> list) {

        Gson gson = new Gson();
        return gson.toJson(list);
    }

}

class TrayConverter {

    @TypeConverter
    public static ArrayList<Integer> fromJSONToInt(String value) {

        if (value != null) {
            ArrayList<Integer> list = new ArrayList<>();
            JSONArray array = null;

            try {
                array = new JSONArray(value);
            } catch (JSONException e) {
                e.printStackTrace();

                for (int i = 0; i < array.length(); i++) {
                    try {
                        list.add(array.getInt(i));
                    } catch (JSONException x) {
                        x.printStackTrace();
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

        Log.e("Converter", "TypeConverter called!");
        Log.e("Converter", "list: " + list.toString());

        if (list != null) {
            for (int value : list) {
                array.put(value);
            }
            return array.toString();
        }
        else {
            Log.e("Converter", "Null returned!");
            return null;
        }
    }
}