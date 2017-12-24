package dresden.de.blueproject.data;


import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

public class DatabaseRepository {

    private  final DatabaseEquipmentDAO daoObject;

    @Inject
    public DatabaseRepository(DatabaseEquipmentDAO daoObject) {
        this.daoObject = daoObject;
    }

    public void add(DatabaseEquipmentObject object) {
        daoObject.insertItem(object);
    }

    public LiveData<List<DatabaseEquipmentObject>> getItems() {
        return daoObject.getAllItems();
    }

    public LiveData<List<DatabaseEquipmentMininmal>> getItemsMinimal() {
        return daoObject.getMinimalItems();
    }

    public LiveData<DatabaseEquipmentObject> getItemByID(int id) {
        return daoObject.findItemByID(id);
    }

    public void deleteItem(int id) {
        daoObject.deleteItem(id);
    }

}
