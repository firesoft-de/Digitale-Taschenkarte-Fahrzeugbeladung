/*
 *     Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
 *     Copyright (C) 2018  David Schlossarczyk
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     For the full license visit https://www.gnu.org/licenses/gpl-3.0.
 */

package firesoft.de.ditaka.datamodels;

import android.graphics.Bitmap;

/**
 * Stellt eine Datenstruktur für ein Gertätefach bereit
 */
public class Tray extends BaseDataClass{

    private int vehicleId;
    private String location;
    private Bitmap image;


    // #region Konstruktoren

    /**
     * Erstellt eine neue Instanz vom Typ Tray
     * @param id Id des Trays
     * @param vehicleId Id des Fahrzeuges zu dem diese Tray gehört
    */
    public Tray(int id,int vehicleId) {
        this.id = id;
        this.vehicleId = vehicleId;
    }

    /**
     * Erstellt eine neue Instanz vom Typ Tray
     * @param id Id des Trays
     * @param vehicleId Id des Fahrzeuges zu dem diese Tray gehört
     * @param name Name des Trays
     * @param description Beschreibung zum Tray
     * @param location Wörtliche Beschreibung des Installationsortes des Trays
     * @param tags Tags für die Suchfunktion
     * @param tagDelimiter Trennzeichen der Tags(";")
     * @param image Bild vom Tray
     */
    public Tray(int id, int vehicleId, String name, String description, String location, String tags, String tagDelimiter, Bitmap image) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.name = name;
        this.description = description;

        this.location = location;
        setTags(tags,tagDelimiter);
        this.image = image;
    }

    // #endregion

    // #region Getter / Setter für Variablen


    public int getVehicleId() {
        return vehicleId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    // #endregion

}
