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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Die Group Klasse stellt ein Datenobjekt für die Gruppen zu Verfügung
 */
public class Group {

    private String name;
    private int id;
    private boolean subscribed;
    private boolean active;
    private String trayname;

    /**
     * Initalisiert die neue Gruppe
     * @param id Identifier der Gruppe
     * @param name Name der Gruppe
     * @param subscribed Subscriptionstatus der Gruppe
     */
    public Group(int id, String name, boolean subscribed) {
        this.id = id;
        this.name = name;
        this.subscribed = subscribed;
        this.trayname = "default";
    }

    public Group(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.name = object.getString("name");
            this.trayname = object.getString("trayname");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Diese beiden Werte sind abgesetzt, da sie beim Datenabruf vom Server einen Fehler werfen werden.
        try {
            this.active = object.getBoolean("active");
            this.subscribed = object.getBoolean("subscribed");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJSON() {

        JSONObject object = new JSONObject();
        try {
            object.put("id",id);
            object.put("name",name);
            object.put("active",active);
            object.put("subscribed",subscribed);
            object.put("trayname",trayname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTrayname() {
        return trayname;
    }

    public void setTrayname(String trayname) {
        this.trayname = trayname;
    }
}
