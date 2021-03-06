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


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseEquipmentMininmal;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Data;

public class DebugViewModel extends ViewModel {

    private DatabaseRepository repository;

    public DebugViewModel(DatabaseRepository repository) {
        this.repository = repository;
    }

    public void addItems(List<EquipmentItem> items) {
        addItemsTask task = new addItemsTask();
        task.execute(Util_Data.castItemToArray(items));
    }

    private class addItemsTask extends AsyncTask<EquipmentItem,Void,Void> {

        @Override
        protected Void doInBackground(EquipmentItem... items) {
            for (int i = 0; i< items.length; i++) {
                repository.add(items[i]);
            }
            return null;
        }
    }

    public LiveData<Integer> countItems() {
        return repository.countItems();
    }



    public void deleteItem(int id) {

        deleteItemTask task = new deleteItemTask();
        task.execute(id);

    }

    private class deleteItemTask extends AsyncTask<Integer,Void,Void> {

        @Override
        protected Void doInBackground(Integer... id) {
            repository.deleteItem(id[0]);
            return null;
        }
    }

    public void deleteAllItem() {

        deleteAllItemTask task = new deleteAllItemTask();
        task.execute();

    }

    private class deleteAllItemTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... x) {
            repository.deleteAllItems();
            return null;
        }
    }

    public LiveData<List<DatabaseEquipmentMininmal>> getItemsByCatID(int catID, String group) {return repository.getItemByCatID(catID, group); }



    //Tray Operationen

    public void addTrays(List<TrayItem> tray) {
        addTrayTask task = new addTrayTask();
        task.execute(Util_Data.castTrayToArray(tray));
    }


    private class addTrayTask extends AsyncTask<TrayItem,Void,Void> {

        @Override
        protected Void doInBackground(TrayItem... items) {
            for (int i = 0; i< items.length; i++) {
                repository.add(items[i]);
            }
            return null;
        }
    }

    public LiveData<Integer> countTrays() {
        return repository.countTrays();
    }

    public void deleteTrays() {
        deleteTrayTask task = new deleteTrayTask();
        task.execute();
    }


    private class deleteTrayTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... x) {
            repository.deleteAllTrays();
            return null;
        }
    }

}
