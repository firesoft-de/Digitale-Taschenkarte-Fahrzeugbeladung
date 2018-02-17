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

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

/**
 * Diese Klasse vereint das Datenbankobjeckt und die DAO Klasse
 */
@Database(entities = {EquipmentItem.class, TrayItem.class, ImageItem.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class DatabaseClass extends RoomDatabase{
    public abstract DatabaseDAO equipmentDAO();

    // Der Builder der Datenbank befindet sich in der Klasse RoomModule

    public static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            // Neue Tabelle erstellen
            database.execSQL("CREATE TABLE `equipment_new` " +
                    "(`id` INTEGER NOT NULL, `name` TEXT, `description` TEXT, `categoryId` INTEGER NOT NULL, " +
                    "`setName` TEXT, `count` INTEGER NOT NULL DEFAULT 0, `position` TEXT, `keywords` TEXT, " +
                    "`additionalNotes` TEXT, `group` TEXT, " +
                    "`positionIndex` INTEGER NOT NULL, PRIMARY KEY(`id`))");

            // Daten in die neue Tabelle schreiben
            database.execSQL("INSERT INTO equipment_new (id, name, description, categoryId, setName, " +
                    "position, keywords, additionalNotes, `group`, positionIndex) " +
                    "SELECT id, name, description, categoryId, " +
                    "mSetName, position, keywords, additionalNotes, `group`, positionIndex FROM equipment");

            // Sicherheitshalber in die neue Spalte count noch überall 1 schreiben
            database.execSQL("UPDATE equipment_new SET count=1 WHERE 1=1");

            // Alte Tabelle löschen
            database.execSQL("DROP TABLE equipment");

            // Neue Tabelle einsetzen
            database.execSQL("ALTER TABLE equipment_new RENAME TO equipment");

        }
    };

}
