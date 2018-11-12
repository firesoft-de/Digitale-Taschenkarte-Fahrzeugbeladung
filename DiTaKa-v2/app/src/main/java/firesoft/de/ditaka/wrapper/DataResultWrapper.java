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

package firesoft.de.ditaka.wrapper;

import java.util.ArrayList;

import firesoft.de.ditaka.datamodels.Group;
import firesoft.de.ditaka.datamodels.Item;
import firesoft.de.ditaka.datamodels.Tray;
import firesoft.de.ditaka.datamodels.Vehicle;
import firesoft.de.libfirenet.util.ResultWrapper;

/**
 * ResultWrapper werden verwendet, um Ergebnisse aus AsyncTaskLoadern an den übergeordneten AsyncTaskManager zurückzugeben.
 * Da dabei nur eine Variable übergeben werden kann, wird dieser Wrapper verwendet.
 * Er bietet die Möglichkeit sowohl Ausnahmen (Exceptions) als auch Ergebnisse (Results) zu übergeben.
 */
public class DataResultWrapper extends ResultWrapper {

    // region Variablen

    private ArrayList<Vehicle> vehicles;
    private ArrayList<Item> items;
    private ArrayList<Group> groups;
    private ArrayList<Tray> trays;

    // endregion

    // region Konstruktor

    /**
     * Erstellt einen neuen Resultwrapper. Da die Daten nicht als Object übergeben werden, wird das in der Elternklasse verfügbare result-Object null gesetzt.
     * @param vehicles Nimmt Daten als ArrayList vom Typ Vehicle entgegen
     * @param items Nimmt Daten als ArrayList vom Typ Item entgegen
     * @param groups Nimmt Daten als ArrayList vom Typ Group entgegen
     * @param trays Nimmt Daten als ArrayList vom Typ Tray entgegen
     */
    public DataResultWrapper(ArrayList<Item> items, ArrayList<Tray> trays, ArrayList<Vehicle> vehicles, ArrayList<Group> groups) {
        super(null);
        this.vehicles = vehicles;
        this.items = items;
        this.groups = groups;
        this.trays = trays;
    }

    /**
     * Erstellt einen neuen Resultwrapper
     * @param result Ergebnis des Loaders als Object
     */
    public DataResultWrapper(Object result) {
        super(result);
    }

    /**
     * Erstellt einen neuen Resultwrapper
     * @param exception Ausnahme die während der Ausführung des Loaders aufgetreten ist
     */
    public DataResultWrapper(Exception exception) {
        super(exception);
    }

    // endregion

    // region Getter / Setter

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public ArrayList<Tray> getTrays() {
        return trays;
    }

    // endregion

}
