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

package dresden.de.digitaleTaschenkarteBeladung.data;


import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

public class DatabaseRepository {

    private  final DatabaseDAO daoObject;

    @Inject
    public DatabaseRepository(DatabaseDAO daoObject) {
        this.daoObject = daoObject;
    }

    public void add(EquipmentItem object) {
        daoObject.insertItem(object);
    }

    public LiveData<List<EquipmentItem>> getItems() {
        return daoObject.getAllItems();
    }

    public LiveData<List<DatabaseEquipmentMininmal>> getItemsMinimal() {
        return daoObject.getMinimalItems();
    }

    public LiveData<EquipmentItem> getItemByID(int id) {
        return daoObject.findItemByID(id);
    }

    public LiveData<List<DatabaseEquipmentMininmal>> getItemByCatID(int id, String group) {return daoObject.findItemByCatID(id, group);}

    public void deleteItem(int id) {
        daoObject.deleteItem(id);
    }

    public void deleteAllItems() {daoObject.deleteAllItems();}

    public LiveData<Integer> countItems() {return daoObject.countItems();}

    public LiveData<List<DatabaseEquipmentMininmal>> searchItemsMinimal(String key) {return daoObject.searchItemsMinimal(key);}

    public void add(TrayItem object) {daoObject.insertTray(object);}

    public void deleteAllTrays() {daoObject.deleteTray();}

    public LiveData<List<TrayItem>> getTrays(String group) {return daoObject.getAllTrays(group);}

    public LiveData<Integer> countTrays() {return daoObject.countTrays();}

    public LiveData<TrayItem> getTrayById(int id) {return daoObject.getTrayById(id);}



    public LiveData<Integer> countImage(){return daoObject.countImage();}

    public LiveData<ImageItem> getImageByID(int id) {return daoObject.findImageByID(id);}

    public void deleteAllImages() {daoObject.deleteImage();}

    public void add(ImageItem imageItem) {daoObject.insertImages(imageItem);}

    public LiveData<ImageItem> getImageByCatID(int catID) {return daoObject.getImageByCatID(catID);}

//    public LiveData<List<Integer>> getPositionCoordinates(int catID) {return daoObject.getPositionCoordinates(catID);}

}
