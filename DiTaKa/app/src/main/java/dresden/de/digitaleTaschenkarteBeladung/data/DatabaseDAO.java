package dresden.de.digitaleTaschenkarteBeladung.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.dataStructure.DatabaseEquipmentMininmal;

/**
 * Database Access Object für die Equipment Datenbank
 */
@Dao
public interface DatabaseDAO {

    //DAO für die Items

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(EquipmentItem... item); //Eventuell Long

/*    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemList(ArrayList<DatabaseClass> items);*/

    @Update
    void upadteItem(EquipmentItem... item);

    @Delete
    void deleteItem(EquipmentItem... item);

    @Query("SELECT * FROM equipment")
    LiveData<List<EquipmentItem>> getAllItems();

    @Query("DELETE FROM equipment")
    void deleteAllItems();

    @Query("DELETE FROM equipment WHERE id LIKE :id")
    void deleteItem(int id);

    @Query("SELECT * FROM equipment WHERE id LIKE :id")
    LiveData<EquipmentItem> findItemByID(int id);

    @Query("SELECT id, name, position FROM equipment WHERE id LIKE :id")
    DatabaseEquipmentMininmal findMinimalItemByID(int id);

    @Query("SELECT id, name, position FROM equipment WHERE categoryId LIKE :id")
    LiveData<List<DatabaseEquipmentMininmal>> findItemByCatID(int id);

    @Query("SELECT id, name, position FROM equipment")
    LiveData<List<DatabaseEquipmentMininmal>> getMinimalItems();

    @Query("SELECT COUNT(id) FROM equipment")
    LiveData<Integer> countItems();

    //Query für das Suchfeld TODO: Einbinden!
    @Query("SELECT id, name, position FROM equipment WHERE keywords LIKE :key OR name LIKE :key")
    LiveData<List<DatabaseEquipmentMininmal>> searchItemsMinimal(String key);


    //DAO für die Trays
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTray(TrayItem tray);

    @Query("SELECT * FROM tray")
    LiveData<List<TrayItem>> getAllTrays();

    @Query("DELETE FROM tray")
    void deleteTray();

    @Query("SELECT COUNT(id) FROM tray")
    LiveData<Integer> countTrays();
}
