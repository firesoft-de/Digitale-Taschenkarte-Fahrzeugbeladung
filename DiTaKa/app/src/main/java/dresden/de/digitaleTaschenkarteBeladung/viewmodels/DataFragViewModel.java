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

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Data;


/**
 * ViewModel für das Datenimport Fragment
 */

public class DataFragViewModel extends ViewModel {

    private DatabaseRepository repository;

    public DataFragViewModel(DatabaseRepository repository) {
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

    public void addTrays(List<TrayItem> trays) {

        addTrayTask task = new addTrayTask();

        task.execute(Util_Data.castTrayToArray(trays));

    }

    private class addTrayTask extends AsyncTask<TrayItem,Void,Void> {

        @Override
        protected Void doInBackground(TrayItem... trays) {
            for (int i = 0; i< trays.length; i++) {
                repository.add(trays[i]);
            }
            return null;
        }
    }

    public void addImage(List<ImageItem> images) {
        addImageTask task = new addImageTask();
        task.execute(Util_Data.castImageToArray(images));
    }

    private class addImageTask extends AsyncTask<ImageItem,Void,Void> {
        @Override
        protected Void doInBackground(ImageItem... images) {
            for (int i = 0; i< images.length; i++) {
                repository.add(images[i]);
            }
            return null;
        }
    }

    public void deleteByGroup(String groupKey) {
        deleteByGroupTask task = new deleteByGroupTask();
        task.execute(groupKey);
    }

    private class deleteByGroupTask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... groupKey) {
            repository.deleteImageByGroup(groupKey[0]);
            repository.deleteItemByGroup(groupKey[0]);
            repository.deleteTrayByGroup(groupKey[0]);
            return null;
        }
    }

    public void deleteAll() {
        deleteAllTask task = new deleteAllTask();
        task.execute();
    }

    private class deleteAllTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            repository.deleteAllImages();
            repository.deleteAllItems();
            repository.deleteAllTrays();
            return null;
        }
    }

    public LiveData<Integer> countTrays() {
        return repository.countTrays();
    }

    public LiveData<Integer> countItems() {
        return repository.countItems();
    }

    public LiveData<Integer> countImages() {
        return repository.countImage();
    }


}
