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

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CustomViewmodelFactory implements ViewModelProvider.Factory {

    // private ItemModel itemModel;

    @Inject
    public CustomViewmodelFactory() { //ItemModel itemModel) {
        //this.itemModel = itemModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

//        if (modelClass.isAssignableFrom(MainViewmodel.class)) {
//                return modelClass.cast(new MainViewmodel());
//            }
//        else if (modelClass.isAssignableFrom(InputViewmodel.class)) {
//            return (T) new InputViewmodel(model);
//        }

        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
