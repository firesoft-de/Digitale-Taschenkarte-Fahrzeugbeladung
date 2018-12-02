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
 * Stellt eine Datenstruktur für ein Fahrzeug bereit
 */
public class Vehicle extends BasicData {

    private String callsign;
    private Bitmap image;
    private String registration;

    // region Konstruktoren

    /**
     * Erstellt eine neue Instanz vom Typ Vehicle
     * @param id ID des Fahrzeuges
     */
    public Vehicle(int id) {
        this.id = id;
    }

    /**
     * Erstellt eine neue Instanz vom Typ Vehicle
     * @param id ID des Fahrzeuges
     * @param name Name des Fahrzeuges
     * @param callsign Funkkenner des Fahrzeuges
     * @param description Beschreibung des Fahrzeuges
     * @param image Bild vom Fahrzeug
     * @param registration Amtliches Kennzeichen des Fahrzeuges
     * @param tags Tags für die Suchfunktion
     * @param tagDelimiter Trennzeichen der Tags(";")
     */
    public Vehicle(int id, String name, String callsign, String description, Bitmap image, String registration, String tags, String tagDelimiter) {
        this.id = id;
        this.name = name;
        this.callsign = callsign;
        this.description = description;
        this.image = image;
        this.registration = registration;
        setTags(tags,tagDelimiter);
    }

    // endregion

    // region Getter / Setter für Variablen

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    // endregion
}
