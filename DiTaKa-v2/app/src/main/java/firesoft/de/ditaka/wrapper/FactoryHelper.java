/*
 * Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
 * Copyright (C) 2018  David Schlossarczyk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * For the full license visit https://www.gnu.org/licenses/gpl-3.0.
 */

package firesoft.de.ditaka.wrapper;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import firesoft.de.ditaka.dagger.CustomViewmodelFactory;
import firesoft.de.ditaka.fragments.trayFragment.TrayListViewmodel;

public class FactoryHelper {

    /**
     * Provides a method to generate viewmodels
     * @param activity The activity in which scope the viewmodel should be used.
     * @param factory The factory to use
     * @param viewModel The desired viewmodel
     * @param <T>
     * @return Instance of the desired viewmodel
     */
    public static <T extends ViewModel> T generateViewmodel(FragmentActivity activity, CustomViewmodelFactory factory, Class<T> viewModel) throws Exception{

        T newViewmodel;

//        if (viewModel.isAssignableFrom(TrayListViewmodel.class)) {
//            newViewmodel = (T) ViewModelProviders.of(activity, factory).get(TrayListViewmodel.class);
//            return newViewmodel;
//        }
//        else if (viewModel.isAssignableFrom(MainViewmodel.class)) {
//            newViewmodel = (T) ViewModelProviders.of(activity, factory).get(MainViewmodel.class);
//            return newViewmodel;
//        }

        throw new Exception("Unable to parse give viewmodelclass");

    }

//    /**
//     * Provides a method to generate viewmodels
//     * @param activity The activity in which scope the viewmodel should be used.
//     * @param factory The factory to use
//     * @param viewModel The desired viewmodel
//     * @param <T>
//     * @return Instance of the desired viewmodel
//     */
//    public static <T extends ViewModel> T generateViewmodel(FragmentActivity activity, ViewModelProvider.Factory factory, Class<T> viewModel) throws Exception{
//
//        T newViewmodel;
//
//        if (viewModel.isAssignableFrom(OutputViewmodel.class)) {
//            newViewmodel = (T) ViewModelProviders.of(activity, factory).get(OutputViewmodel.class);
//            return newViewmodel;
//        }
//        else if (viewModel.isAssignableFrom(InputViewmodel.class)) {
//            newViewmodel = (T) ViewModelProviders.of(activity, factory).get(InputViewmodel.class);
//            return newViewmodel;
//        }
//
//        throw new Exception("Unable to parse give viewmodelclass");
//
//    }

}
