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

import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseEquipmentMininmal;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;


/**
 * Diese Klasse stellt die ViewModellkomponenten in der MVVM Architektur dar. Sie bietet dem zugehörigen Fragment Methoden an mit denen auf die Datenbank zugegriffen wird.
 */
public class ItemViewModel extends ViewModel{

    private DatabaseRepository repository;

    public ItemViewModel(DatabaseRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<EquipmentItem>> getItems() {
        return repository.getItems();
    }

    public LiveData<List<DatabaseEquipmentMininmal>> getItemsMinimal() {
        return repository.getItemsMinimal();
    }

    public LiveData<EquipmentItem> getItem(int id) {return repository.getItemByID(id);}

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

    public LiveData<List<DatabaseEquipmentMininmal>> getItemsByCatID(int catID, String group) {return repository.getItemByCatID(catID, group); }

    public LiveData<ImageItem> getImageByCatID(int catID) {return repository.getImageByCatID(catID);}

    public LiveData<TrayItem> getTrayItem(int id) {return repository.getTrayById(id);}

    //Abfrage um die Positionskoordinaten aus dem Tray zu erhalten
//    public LiveData<List<Integer>> getPositionCoordinates(int catID) {return repository.getPositionCoordinates(catID);}

}


