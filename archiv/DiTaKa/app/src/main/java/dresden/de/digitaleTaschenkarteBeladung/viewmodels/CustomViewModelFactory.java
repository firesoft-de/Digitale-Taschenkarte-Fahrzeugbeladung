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

package dresden.de.digitaleTaschenkarteBeladung.viewmodels;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;


/**
 * Mit dieser Klasse werden die Viewmodels erstellt und mit Argumenten versehen
 */
public class CustomViewModelFactory implements ViewModelProvider.Factory{

    private final DatabaseRepository repository;

    public CustomViewModelFactory(DatabaseRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            return (T) new ItemViewModel(repository);
        }
        //Weitere Viewmodels müssen hier eingetragen werden
        else if (modelClass.isAssignableFrom(DataFragViewModel.class)) {
            return (T) new DataFragViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(DebugViewModel.class)) {
            return (T) new DebugViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(TrayViewModel.class)) {
            return (T) new TrayViewModel(repository);
        } /*
        else if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            return (T) new ItemViewModel(repository);
        }*/
        else {
            throw new IllegalArgumentException("ViewModel not found!");
        }

    }
}
