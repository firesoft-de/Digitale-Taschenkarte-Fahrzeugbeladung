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

package firesoft.de.ditaka.datahandling;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;

import firesoft.de.ditaka.datamodels.Group;
import firesoft.de.ditaka.datamodels.Item;
import firesoft.de.ditaka.datamodels.Tray;
import firesoft.de.ditaka.datamodels.Vehicle;
import firesoft.de.ditaka.wrapper.ExtendedResultWrapper;

/**
 * Diese Klasse erweitert einen AsyncTaskLoader. Sie stellt Methoden bereit um die vom Server heruntergeladenen Daten zu verarbeiten und in die lokale Datenstruktur zu überführen.
 */
public class DataProcessor extends AsyncTaskLoader<ExtendedResultWrapper> {

    // region Variablen

    /**
     * Enthält die Rohdaten die vom Server heruntergeladen wurden
     */
    String rawData;

    // endregion

    // region Konstruktor

    /**
     * Erstellt eine neue Instanz des DataProcessors
     * @param rawData Rohdaten die vom Server geliefert worden als String
     */
    public DataProcessor(Context context, String rawData) {
        super(context);
    }

    // endregion

    // region Arbeitsmethoden

    /**
     * Hauptarbeitsroutine der Klasse
     * @return ExtendedResultWrapper welcher die extrahierten Daten als ArrayLists enthält (Zugriff mit .getItem(), .getTray(), .getVehicle(), .getGroup())
     */
    @Override
    public ExtendedResultWrapper loadInBackground() {

        // Daten aufsplitten
        ArrayList<Item> items = extractItems();
        ArrayList<Tray> trays = extractTrays();
        ArrayList<Vehicle> vehicles = extractVehicle();
        ArrayList<Group> groups = extractGroups();

        // Ergebnisse in einem ResultWrapper zusammenfassen
        ExtendedResultWrapper resultWrapper;
        resultWrapper = new ExtendedResultWrapper(items,trays,vehicles,groups);

        return resultWrapper;
    }

    // endregion


    // region Hilfsmethoden

    private ArrayList<Item> extractItems() {

        ArrayList<Item> items = new ArrayList<>();

        return items;
    }

    private ArrayList<Tray> extractTrays() {

        ArrayList<Tray> trays = new ArrayList<>();

        return trays;
    }

    private ArrayList<Vehicle> extractVehicle() {

        ArrayList<Vehicle> vehicles = new ArrayList<>();

        return vehicles;
    }

    private ArrayList<Group> extractGroups() {

        ArrayList<Group> groups = new ArrayList<>();

        return groups;
    }


    // endregion

}
