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

package dresden.de.digitaleTaschenkarteBeladung.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;

public class Util {

    //Konstanten für die Fragmenterkennung
//    public static final String FRAGMENT_MAIN = "100";
    public static final String FRAGMENT_DATA  = "101";
    public static final String FRAGMENT_LIST_TRAY = "102";
    public static final String FRAGMENT_LIST_ITEM = "103";
    public static final String FRAGMENT_DETAIL = "104";
    public static final String FRAGMENT_DEBUG = "105";
    public static final String FRAGMENT_SETTINGS = "106";
    public static final String FRAGMENT_ABOUT = "107";
    public static final String FRAGMENT_LICENSE = "108";

    public static final String ARGS_URL = "ARGS_URL";
    public static final String ARGS_VERSION = "ARGS_VERSION";
    public static final String ARGS_DBSTATE = "ARGS_DBSTATE";
    public static final String ARGS_CALLFORUSER = "ARGS_CALLFORUSER";

    public static final String PREFS_NAME="dresden.de.digitaleTaschenkarteBeladung";
    public static final String PREFS_URL="dresden.de.digitaleTaschenkarteBeladung.url";
    public static final String PREFS_DBVERSION="dresden.de.digitaleTaschenkarteBeladung.dbversion";
    public static final String PREFS_SORT="dresden.de.digitaleTaschenkarteBeladung.sort";
    public static final String PREFS_GROUPS="dresden.de.digitaleTaschenkarteBeladung.groups";

    private static final String FILE_DESTINATION_IMAGE = "image";

    public static final String LICENSE_URL="https://www.gnu.org/licenses/gpl-3.0.de.html";

    public static final String NO_SUBSCRIBED_GROUPS="no-subscribed-groups";

    public enum DbState {
        VALID,
        EXPIRED,
        CLEAN,
        UNKNOWN
    }

    public enum Sort {
        AZ,
        ZA,
        PRESET
    }

    public static void LogDebug(String tag, String message) {
        if (MainActivity.DEBUG_ENABLED) {
            Log.d(tag, message);
        }
    }

    public static void LogError(String tag, String message) {
        if (MainActivity.DEBUG_ENABLED) {
            Log.e(tag,message);
        }
    }

    public static String saveImage(int id, Bitmap image, Context context) {

        //https://stackoverflow.com/questions/649154/save-bitmap-to-location
        FileOutputStream stream = null;
        File file = null;
        try {
            String path = Environment.getDataDirectory().toString();
            Integer counter = 0;
            File directory = context.getDir(FILE_DESTINATION_IMAGE, Context.MODE_PRIVATE);

            file = new File(directory, id +".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.

            if (!file.canWrite()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG,85, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getPath();
    }

    public static Bitmap openImage(ImageItem imageItem, Context context) {
        Bitmap image;
        File file;
        FileInputStream stream;
        int id = imageItem.getId();
        try {
            File directory = context.getDir(FILE_DESTINATION_IMAGE, Context.MODE_PRIVATE);
            file = new File(directory, id +".jpg");
            stream = new FileInputStream(file);
            image = BitmapFactory.decodeStream(stream);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveSortPref(Sort sort, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(Util.PREFS_NAME, Context.MODE_PRIVATE).edit();

        switch (sort) {
            case PRESET:
                editor.putInt(PREFS_SORT,0);
                break;
            case AZ:
                editor.putInt(PREFS_SORT,1);
                break;
            case ZA:
                editor.putInt(PREFS_SORT,2);
                break;
        }

        editor.apply();
    }

    public static Sort loadSortPref(Activity activity) {
        int pref = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getInt(PREFS_SORT, 0);
        switch (pref) {
            default:
                return Sort.PRESET;
            case 1:
                return Sort.AZ;
            case 2:
                return Sort.ZA;
        }
    }

    public static void saveGroupPref(ArrayList<String> groups, Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(Util.PREFS_NAME,Context.MODE_PRIVATE).edit();
        StringBuilder saveString = new StringBuilder();

        for (String group: groups
             ) {
            saveString.append(group);
            saveString.append(";");
        }

        //Abschließendes ; entfernen
        saveString.deleteCharAt(saveString.length() - 1);
        //Speichern
        editor.putString(PREFS_GROUPS,saveString.toString());
        editor.apply();
    }

    public static ArrayList<String> loadGroupPref(Activity activity) {
        String saveString = activity.getSharedPreferences(Util.PREFS_NAME,Context.MODE_PRIVATE).getString(PREFS_GROUPS,"");
        ArrayList<String> groups = new ArrayList<>();

        if (!saveString.equals("")) {
            String[] array = saveString.split(";");
            groups.addAll(Arrays.asList(array));
        }
        return groups;
    }


    /**
     * Die Methode erzeugt den Query für die Gruppengestützte HTTP-Abfrage
     * @param activity
     * @return
     */
    public static String getGroupQuery(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;

        if (mainActivity.groups_subscribed.size() > 0) {

            StringBuilder stringBuilder = new StringBuilder();

            for (String group : mainActivity.groups_subscribed
                    ) {
                stringBuilder.append(group);
                stringBuilder.append("_");
            }

            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            return stringBuilder.toString();
        }
        else {
            return NO_SUBSCRIBED_GROUPS;
        }
    }

}
