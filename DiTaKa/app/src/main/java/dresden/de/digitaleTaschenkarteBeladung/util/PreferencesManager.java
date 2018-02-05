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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import dresden.de.digitaleTaschenkarteBeladung.BuildConfig;
import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.data.Group;

import static dresden.de.digitaleTaschenkarteBeladung.service.BootReceiver.startBackgroundService;
import static dresden.de.digitaleTaschenkarteBeladung.service.BootReceiver.stopBackgroundService;

/**
 * Der PreferencesManager ist für das Laden der Einstellungen aus den PREFS zuständig. Er gewährleistet dabei die Kompatibilität zu alten App-Versionen
 */
public class PreferencesManager {
    private MainActivity parent;
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //Einstellungsvariablen
    private int dbVersion;
    private String url;

    private int positionMarkColor;
    private int positionTextColor;

    private boolean checkForUpdateAllowed;

    //Flag Variablen
    //Diese Variable gibt an ob ein veralteter PREF Satz gefunden wurde. Beim Speichern müssen dann entsprechende Maßnahmen ergriffen werden
    private boolean outdatedPref;
    private boolean contextOnlyMode;

    //Konstanten

    private static final String PREFS_NAME="dresden.de.digitaleTaschenkarteBeladung";
    private static final String PREFS_URL="url";
    private static final String PREFS_DBVERSION="dbversion";
    private static final String PREFS_SORT="sort";
    private static final String PREFS_VERSION="version";
    private static final String PREFS_GROUPS="groups";
    private static final String PREFS_ACTIVE_GROUP="activegroup";
    private static final String PREFS_COLOR_POSITION_MARK="color_position_mark";
    private static final String PREFS_COLOR_POSITION_TEXT="color_position_text";
    private static final String PREFS_NETWORK_AUTOCHECK_ALLOWED="network_autocheck";


    public PreferencesManager(Activity parent) {
        this.parent = (MainActivity) parent;
        outdatedPref = false;
        contextOnlyMode = false;
    }

    public PreferencesManager(Context context) {
        this.parent = null;
        this.context = context;
        outdatedPref = false;

        //Da über den Context kein Zugriff auf gManager etc. stattfinden kann, wird die entsprechende Flag gesetzt
        contextOnlyMode = true;
    }

    //=================================================================
    //========================HAUPTMETHODEN==========================
    //=================================================================

    public void load() {
        int appversion = 11;

        if (parent == null) {
           preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            try {
                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                appversion = pInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            preferences = parent.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            try {
                PackageInfo pInfo = parent.getPackageManager().getPackageInfo(parent.getPackageName(), 0);
                appversion = pInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        switch (appversion) {
            case 11:
                //Version 0.4.3
                loadv11();
                outdatedPref = true; //Die Speicherstruktur ist nicht aufwärtskompatibel. Daher wird die Flag auf true gesetzt.
                break;
            case 12:
                //Version 0.5
                loadv12();
                break;
            case 13:
                //Version 0.5.1
                loadv13();
                break;
            case 14:
                //Version 0.6.1
                loadv13();
                break;
            case 15:
                //Version 0.6.2
                loadv15();
                break;
        }


    }

    public void save() {

        if (contextOnlyMode) {
            return;
        }

        editor = preferences.edit();

        if (outdatedPref) {
            //Alte Prefs komplett löschen
            delete();
        }

        //Allgemeine Einstellungen
        editor.putString(PREFS_URL, url);
        editor.putInt(PREFS_DBVERSION, dbVersion);
        editor.putInt(PREFS_VERSION, BuildConfig.VERSION_CODE);

        //Sortierung
        if (parent.liveSort.getValue() != null) {
            switch (parent.liveSort.getValue()) {
                case PRESET:
                    editor.putInt(PREFS_SORT, 0);
                    break;
                case AZ:
                    editor.putInt(PREFS_SORT, 1);
                    break;
                case ZA:
                    editor.putInt(PREFS_SORT, 2);
                    break;
            }
        }

        //Gruppe
        GroupManager gManager = parent.gManager;

        if (gManager.getSubscribedGroups().size() > 0) {

            JSONArray jsonArray = new JSONArray();

            for (Group group : gManager.getSubscribedGroups())
            {
                jsonArray.put(group.toJSON());
            }

            try {
                JSONObject object = new JSONObject();
                object.put("GROUPS",jsonArray);
                //Speichern
                editor.putString(PREFS_GROUPS, object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //Aktive Gruppe laden
        editor.putString(PREFS_ACTIVE_GROUP,gManager.getActiveGroup().getName());

        editor.apply();
    }

    public void delete() {
        editor.clear();
        editor.apply();
    }

    public void reset() {
        delete();
        dbVersion = -1;
        url = "NO_URL_FOUND";

        if (!contextOnlyMode) {
            parent.liveNetDBVersion.setValue(0);
            parent.dbState = Util.DbState.CLEAN;
            positionTextColor = ResourcesCompat.getColor(parent.getResources(), R.color.text,null);
            positionMarkColor = ResourcesCompat.getColor(parent.getResources(), R.color.position_image_highlight,null);
        }
    }

    //=================================================================
    //========================GETTER & SETTER==========================
    //=================================================================

    public int getDbVersion() {
        return dbVersion;
    }

    public void setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    //=================================================================
    //======================Kompatibilitätsmethoden====================
    //=================================================================

    private void loadv11() {

        //Allgemeine Einstellungen
        dbVersion = preferences.getInt(PREFS_DBVERSION, -1);
        url = preferences.getString(PREFS_URL, "NO_URL_FOUND");

        //Nachfolgende Befehl benötigen eine Parent-Activity vom Typ MainActivity
        //Sie können daher nur mit dem Context nicht ausgeführt werden
        if (!contextOnlyMode) {

        //Sortierung
            int pref = preferences.getInt(PREFS_SORT, 0);
            switch (pref) {
                default:
                    parent.liveSort.setValue(Util.Sort.PRESET);
                    break;
                case 1:
                    parent.liveSort.setValue(Util.Sort.AZ);
                    break;
                case 2:
                    parent.liveSort.setValue(Util.Sort.ZA);
                    break;
            }

        //Gruppen laden
        GroupManager gManager = parent.gManager;
        String saveString = preferences.getString(PREFS_GROUPS,"");
        ArrayList<String> subscribedGroups = new ArrayList<>();

        if (!saveString.equals("")) {
            String[] array = saveString.split(";");
            subscribedGroups.addAll(Arrays.asList(array));
        }

        int index = 0;
        for (String name: subscribedGroups) {

            Group group = new Group(index,name,true);
            gManager.add(group);
            index += 1;
        }

        //Aktive Gruppe laden
        gManager.setActiveGroup(preferences.getString(PREFS_ACTIVE_GROUP,""));

        }
    }

    private void loadv12() {

        //Allgemeine Einstellungen
        dbVersion = preferences.getInt(PREFS_DBVERSION,-1);
        url = preferences.getString(PREFS_URL,"NO_URL_FOUND");

        //Nachfolgende Befehl benötigen eine Parent-Activity vom Typ MainActivity
        //Sie können daher nur mit dem Context nicht ausgeführt werden
        if (!contextOnlyMode) {

            //Sortierung
            int pref = preferences.getInt(PREFS_SORT, 0);
            switch (pref) {
                default:
                    parent.liveSort.setValue(Util.Sort.PRESET);
                    break;
                case 1:
                    parent.liveSort.setValue(Util.Sort.AZ);
                    break;
                case 2:
                    parent.liveSort.setValue(Util.Sort.ZA);
                    break;
            }

            //Gruppen laden
            GroupManager gManager = parent.gManager;
            String json = preferences.getString(PREFS_GROUPS, "");

            JSONArray array = new JSONArray();
            try {
                JSONObject object = new JSONObject(json);
                array = object.getJSONArray("GROUPS");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < array.length(); i++) {
                try {
                    Group group = new Group((JSONObject) array.get(i));
                    gManager.add(group);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //Aktive Gruppe laden
            gManager.setActiveGroup(preferences.getString(PREFS_ACTIVE_GROUP, ""));
        }
    }

    private void loadv13() {
        loadv12();

        //Nachfolgende Befehl benötigen eine Parent-Activity vom Typ MainActivity
        //Sie können daher nur mit dem Context nicht ausgeführt werden
        if (!contextOnlyMode) {

            //Farbeinstellungen
            int color = ResourcesCompat.getColor(parent.getResources(), R.color.position_image_highlight, null);

            positionMarkColor = preferences.getInt(PREFS_COLOR_POSITION_MARK, color);

            color = ResourcesCompat.getColor(parent.getResources(), R.color.text, null);

            positionTextColor = preferences.getInt(PREFS_COLOR_POSITION_TEXT, color);
        }
    }

    private void loadv15() {

        loadv13();

        //Netzwerkeinstellungen
        checkForUpdateAllowed = preferences.getBoolean(PREFS_NETWORK_AUTOCHECK_ALLOWED, true);

    }

    public int getPositionTextColor() {
        return positionTextColor;
    }

    public void setPositionTextColor(int positionText) {
        this.positionTextColor = positionText;
    }

    public int getPositionMarkColor() {
        return positionMarkColor;
    }

    public void setPositionMarkColor(int positionMark) {
        this.positionMarkColor = positionMark;
    }

    public boolean isCheckForUpdateAllowed() {
        return checkForUpdateAllowed;
    }

    public void setCheckForUpdateAllowed(boolean checkForUpdateAllowed) {
        this.checkForUpdateAllowed = checkForUpdateAllowed;

        if (checkForUpdateAllowed) {
            startBackgroundService(context);
        }
        else {
            stopBackgroundService(context);
        }
    }
}
