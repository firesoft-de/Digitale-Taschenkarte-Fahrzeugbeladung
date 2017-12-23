package dresden.de.blueproject;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

/**
 * Diese Klasse vereint das Datenbankobjeckt und die DAO Klasse
 */
@Database(entities = {DatabaseEquipmentObject.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class DatabaseEquipment extends RoomDatabase{
    public abstract DatabaseEquipmentDAO equipmentDAO();
}
