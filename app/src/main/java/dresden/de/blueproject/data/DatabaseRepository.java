package dresden.de.blueproject.data;


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

    public LiveData<List<DatabaseEquipmentMininmal>> getItemByCatID(int id) {return daoObject.findItemByCatID(id);}

    public void deleteItem(int id) {
        daoObject.deleteItem(id);
    }

}
