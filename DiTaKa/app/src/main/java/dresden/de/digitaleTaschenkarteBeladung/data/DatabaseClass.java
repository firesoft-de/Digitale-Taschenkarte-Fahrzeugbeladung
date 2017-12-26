package dresden.de.digitaleTaschenkarteBeladung.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

/**
 * Diese Klasse vereint das Datenbankobjeckt und die DAO Klasse
 */
@Database(entities = {EquipmentItem.class, TrayItem.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class DatabaseClass extends RoomDatabase{
    public abstract DatabaseDAO equipmentDAO();
}
