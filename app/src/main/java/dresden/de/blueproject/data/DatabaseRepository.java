package dresden.de.blueproject.data;


import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import dresden.de.blueproject.dataStructure.DatabaseEquipmentMininmal;

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

    public LiveData<List<DatabaseEquipmentMininmal>> getItemByCatID(int id) {return daoObject.findItemByCatID(id);}

    public void deleteItem(int id) {
        daoObject.deleteItem(id);
    }

    public void deleteAllItems() {daoObject.deleteAllItems();}

    public LiveData<Integer> countItems() {return daoObject.countItems();}

    public LiveData<List<DatabaseEquipmentMininmal>> searchItemsMinimal(String key) {return daoObject.searchItemsMinimal(key);}

    public void add(TrayItem object) {daoObject.insertTray(object);}

    public void deleteAllTrays() {daoObject.deleteTray();}

    public LiveData<List<TrayItem>> getTrays() {return daoObject.getAllTrays();}

    public LiveData<Integer> countTrays() {return daoObject.countTrays();}

}
