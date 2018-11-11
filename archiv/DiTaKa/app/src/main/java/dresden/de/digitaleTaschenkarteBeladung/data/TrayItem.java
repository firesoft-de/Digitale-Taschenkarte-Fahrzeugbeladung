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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Diese Klasse implementiert die Datenstruktur für ein Fach
 */
@Entity(tableName = "tray")
public class TrayItem {

    //Interne Variablen

    //ID
    @PrimaryKey
    private int id;

    //Name
    private String name;

    //Beschreibung
    private String description;

    //Zweite Beschreibung/Anmerkungen
    private String descriptionTwo;

    private String group;

    //Die Koordinaten für die Markierung der Itempositionen
    //Die vier Koordinaten werden als vier unabhängige Einträge angelegt. D.h. beim Abrufen muss der ausgegebene Index mit 4 multipliziert werden und dann dieser sowie
    // die nachfolgenden drei abgefragt werden um die richtigen Koordinaten zu erhalten
    @ColumnInfo(name = "positions")
    // @TypeConverters(TrayConverter.class)
    private String positionCoordinates;

    //Ein Bild
//    private Image mImage;


    //Konstruktor
    /**
     * Initialisiert die Datenklasse
     * @param id ID des Behälters/der Kategorie
     * @param name Öffentlicher Name
     * @param description Eine Beschreibung
     */
    public TrayItem(int id, String name, String description) {

        this.id = id;
        this.name = name;
        this.description = description;

    }


    //Get-Methoden
    public int getId() {return id;}

    public String getName() {return name;}

    public String getDescription() {return  description;}

    public String getDescriptionTwo() {return descriptionTwo;}

//    public Image getImage() {return mImage;}

    public String getPositionCoordinates() {
        return positionCoordinates;
    }

    public String getGroup() {return group;}

    //Set-Methoden
    public void setName(String name) {}

    public void setId(int id) {}

    public void setDescriptionTwo(String description) {descriptionTwo = description;}

//    public void setImage(Image image) {mImage = image;}

    public void setPositionCoordinates(String positionCoordinates) {
        this.positionCoordinates = positionCoordinates;
    }

    public void setGroup(String group) {this.group = group;}

//    public void positionCoordFromString(String positions) {
//
//        if (!positions.equals("-1")) {
//
//            //Pattern:
//            // PositionsID:coordLinks-coordOben-coordRechts-coordUnten;PositionsID2:coorLinks2- etc.
//
//            if (!positions.equals("")) {
//                String[] coords = positions.split(";");
//
//                if (positionCoordinates == null) {
//                    positionCoordinates = new ArrayList<>();
//                }
//
//                for (String coord : coords) {
//                    String[] coordQuery = coord.split(":");
//                    String[] singleCoord = coordQuery[1].split("-");
//                    for (String s : singleCoord
//                            ) {
//                        positionCoordinates.add(Integer.valueOf(s));
//                    }
//                }
//            }
//        }
//    }

    /**
     * Gibt die Positionskoordinaten für den Markierungsrahmen für einen bestimmten Index aus
     * @param positionIndex Der Index für den die Koordinaten ermittelt werden sollen.
     * @return Array mit den Koordinaten. Die Reihenfolge lautet links, oben, rechts, unten. Falls keine Daten hinterlegt sind, wird auf Arrayposition 0 eine -1 übergeben.
     */
    public int[] getCoordinates(int positionIndex) throws NumberFormatException{

        int[] positions = new int[4];

        // Prüfen ob keine Positionen hinterlegt sind (-> wird mit -1 gekennzeichnet) oder der gespeicherte String leer ist
        if (!positionCoordinates.equals("") && !positionCoordinates.equals("-1")) {

            //Pattern:
            // PositionsID:coordLinks-coordOben-coordRechts-coordUnten;PositionsID2:coorLinks2- etc.
            String[] coordSections = positionCoordinates.split(";");

            for (String coordSection : coordSections) {
                String[] coordQuery = coordSection.split(":");

                // Den gesuchten Abschnitt identifizieren
                if (Integer.valueOf(coordQuery[0]) == positionIndex) {
                    String[] singleCoord = coordQuery[1].split("-");

                    for (int i = 0; i < singleCoord.length; i++) {
                        positions[i] = Integer.valueOf(singleCoord[i]);
                    }
                }
            }
        }
        else {
            positions[0] = -1;
        }

        return positions;
    }
}
