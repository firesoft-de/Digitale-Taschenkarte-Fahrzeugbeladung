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
import android.support.v4.util.Pair;

/**
 * Stellt eine Datenstruktur für einen Gegenstand bereit
 */
public class Item extends BaseDataClass{

    private String location;
    private Pair<Short,Short> markerCoordinateLowerLeft;
    private Pair<Short,Short> markerCoordinateUpperRight;
    private Bitmap image;
    private int trayId;

    // #region Konstruktoren

    /**
     * Erstellt eine neue Instanz vom Typ Item
     * @param id ID des Items
     * @param trayId ID des Trays zu welchem das Item zugeordnet ist
     */
    public Item(int id, int trayId) {
        this.id = id;
        this.trayId = trayId;
    }

    /**
     * Erstellt eine neue Instanz vom Typ Item
     * @param id ID des Items
     * @param name Name des Items
     * @param description Beschreibung des Items
     * @param tags Tags die bei der Suche überprüft werden
     * @param tagDelimiter Trennzeichen der Tags(";")
     * @param location Beschreibung welche den Lagerort des Gegenstandes anzeigt
     * @param markerCoordinateLowerLeft Zur Darstellung des Lagerortes des Gegenstandes wird ein Rechteck auf eine Bitmap gezeichnet. Diese Variable stellt die Koordinate der unteren linken Ecke des Rechtecks dar.
     * @param markerCoordinateUpperRight Zur Darstellung des Lagerortes des Gegenstandes wird ein Rechteck auf eine Bitmap gezeichnet. Diese Variable stellt die Koordinate der oberen rechten Ecke des Rechtecks dar.
     * @param image Bild das das Item zeigt
     * @param trayId ID des Trays zu welchem das Item zugeordnet ist
     */
    public Item(int id, String name, String description, String tags, String tagDelimiter, String location, Pair<Short, Short> markerCoordinateLowerLeft, Pair<Short, Short> markerCoordinateUpperRight, Bitmap image, int trayId) {
        this.id = id;
        this.name = name;
        this.description = description;
        setTags(tags, tagDelimiter);
        this.location = location;
        this.markerCoordinateLowerLeft = markerCoordinateLowerLeft;
        this.markerCoordinateUpperRight = markerCoordinateUpperRight;
        this.image = image;
        this.trayId = trayId;
    }

    // #endregion

    // #region Getter / Setter für Variablen

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTrayId() {
        return trayId;
    }

    public Pair<Short, Short> getMarkerCoordinateLowerLeft() {
        return markerCoordinateLowerLeft;
    }

    public void setMarkerCoordinateLowerLeft(Pair<Short, Short> markerCoordinateLowerLeft) {
        this.markerCoordinateLowerLeft = markerCoordinateLowerLeft;
    }

    public Pair<Short, Short> getMarkerCoordinateUpperRight() {
        return markerCoordinateUpperRight;
    }

    public void setMarkerCoordinateUpperRight(Pair<Short, Short> markerCoordinateUpperRight) {
        this.markerCoordinateUpperRight = markerCoordinateUpperRight;
    }

    // #endregion
}
