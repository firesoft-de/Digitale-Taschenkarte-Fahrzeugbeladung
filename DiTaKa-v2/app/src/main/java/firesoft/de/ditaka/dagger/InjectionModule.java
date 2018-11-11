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

package firesoft.de.ditaka.dagger;

import android.app.Application;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import firesoft.de.ditaka.datamodels.Group;
import firesoft.de.ditaka.datamodels.Item;
import firesoft.de.ditaka.datamodels.Tray;
import firesoft.de.ditaka.datamodels.Vehicle;

/**
 * Diese Klasse definiert die Instanzierung der Variablen
 * Hier werden die absolut minimalen Abhängigkeiten hinterlegt. Dies sind im aktuellen Fall, die MainActivity und der Context
 * Zwischen den einzelnen @Provide Methoden muss keine Verbindung bzgl. der eingegebenen Variablen hergestellt werden. Dies übernimmt Dagger
 */
@Module
public class InjectionModule {

    private final Application application;
    private ArrayList<Item> itemlist;
    private ArrayList<Tray> traylist;
    private ArrayList<Group> grouplist;
    private ArrayList<Vehicle> vehiclelist;

    InjectionModule(Application application) {this.application = application;}

    /**
     * Stellt eine Instanz der Application bereit
     */
    @Provides
    InjectableApplication provideApplication() {return (InjectableApplication) application;}

    /**
     * Stellt eine Liste der Gegenstände bereit. Es wird nur eine Instanz erstellt, da die Liste in allen Klassen gleich sein muss.
     */
    // TODO: Hier muss u.U. eine andere Lademethodik eingebaut werden.
    @Provides
    @Singleton
    ArrayList<Item> provideItemList()
    {
        if (itemlist == null) {
            itemlist = new ArrayList<>();
        }

        return itemlist;
    }

    /**
     * Stellt eine Liste der Gerätefächer. Es wird nur eine Instanz erstellt, da die Liste in allen Klassen gleich sein muss.
     */
    // TODO: Hier muss u.U. eine andere Lademethodik eingebaut werden.
    @Provides
    @Singleton
    ArrayList<Tray> provideTrayList()
    {
        if (traylist == null) {
            traylist = new ArrayList<>();
        }

        return traylist;
    }

    /**
     * Stellt eine Liste der Gruppen. Es wird nur eine Instanz erstellt, da die Liste in allen Klassen gleich sein muss.
     */
    // TODO: Hier muss u.U. eine andere Lademethodik eingebaut werden.
    @Provides
    @Singleton
    ArrayList<Group> provideGroupList()
    {
        if (grouplist == null) {
            grouplist = new ArrayList<>();
        }

        return grouplist;
    }


    /**
     * Stellt eine Liste der Fahrzeuge. Es wird nur eine Instanz erstellt, da die Liste in allen Klassen gleich sein muss.
     */
    // TODO: Hier muss u.U. eine andere Lademethodik eingebaut werden.
    @Provides
    @Singleton
    ArrayList<Vehicle> provideVehicleList()
    {
        if (vehiclelist == null) {
            vehiclelist = new ArrayList<>();
        }

        return vehiclelist;
    }


}
