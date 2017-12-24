package dresden.de.blueproject.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

//TODO: App dahingehend umbauen, dass kein zentrales Datenobjekt als ArrayListe mehr vorgehalten wird, sondern das meiste über die Datenbank gehandelt wird!

/**
 * Database Access Object für die Equipment Datenbank
 */
@Dao
public interface DatabaseEquipmentDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(DatabaseEquipmentObject... item); //Eventuell Long

/*    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemList(ArrayList<DatabaseEquipment> items);*/

    @Update
    void upadteItem(DatabaseEquipmentObject... item);

    @Delete
    void deleteItem(DatabaseEquipmentObject... item);

    @Query("SELECT * FROM equipment")
    LiveData<List<DatabaseEquipmentObject>> getAllItems();

    @Query("DELETE FROM equipment")
    void deleteAllItems();

    @Query("DELETE FROM equipment WHERE id LIKE :id")
    void deleteItem(int id);

    @Query("SELECT * FROM equipment WHERE id LIKE :id")
    LiveData<DatabaseEquipmentObject> findItemByID(int id);

    @Query("SELECT id, name, position FROM equipment WHERE id LIKE :id")
    DatabaseEquipmentMininmal findMinimalItemByID(int id);

    @Query("SELECT id, name, position FROM equipment")
    LiveData<List<DatabaseEquipmentMininmal>> getMinimalItems();


/*    @Delete
    void deleteAllItems();*/

}
