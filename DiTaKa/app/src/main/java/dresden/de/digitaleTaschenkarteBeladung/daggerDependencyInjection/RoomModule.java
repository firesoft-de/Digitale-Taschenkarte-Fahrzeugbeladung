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

package dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseClass;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseDAO;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.CustomViewModelFactory;

/**
 * In dieser Klasse werden verschiedene Elemente für Dagger definiert. Letztendlich wird Dagger hiermit gesagt:
 * "Wenn du auf einen dieser Datentypen stößt, dann kriegst du mit den hier genannten Methoden die passende Instanz/Daten."
 * https://www.youtube.com/watch?v=LCOKWgHdBvE
 */

@Module
public class RoomModule {


    private final DatabaseClass database;

    public RoomModule(Application application) {
        this.database = Room.databaseBuilder(
                application,
                DatabaseClass.class,
                "equipment"
        ).build();
    }

    @Provides
    @Singleton
    DatabaseRepository provideRepository(DatabaseDAO itemDAO){
        return new DatabaseRepository(itemDAO);
    }

    @Provides
    @Singleton
    DatabaseDAO provideDao(DatabaseClass database){
        return database.equipmentDAO();
    }

    @Provides
    @Singleton
    DatabaseClass provideDatabase(Application application){
        return database;
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideViewModelFactory(DatabaseRepository repository){
        return new CustomViewModelFactory(repository);
    }

}
