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
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;
import android.media.Image;

import java.util.List;

/**
 * Database Access Object für die Equipment Datenbank
 */
@Dao
public interface DatabaseDAO {

    //=======================================================
    //======================DAO ITEMS========================
    //=======================================================


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(EquipmentItem... item); //Eventuell Long

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

    @Query("DELETE FROM equipment WHERE `group` LIKE :groupKey")
    void deleteItemByGroup(String groupKey);

    @Query("SELECT * FROM equipment WHERE id LIKE :id")
    LiveData<EquipmentItem> findItemByID(int id);

    @Query("SELECT id, name, position FROM equipment WHERE id LIKE :id AND NOT name LIKE '%#X#x#X#%'")
    DatabaseEquipmentMininmal findMinimalItemByID(int id);

    @Query("SELECT id, name, position FROM equipment WHERE categoryId LIKE :id AND `group` LIKE :groupKey AND NOT name LIKE '%#X#x#X#%'")
    LiveData<List<DatabaseEquipmentMininmal>> findItemByCatID(int id, String groupKey);

    @Query("SELECT id, name, position FROM equipment WHERE NOT name LIKE '%#X#x#X#%'")
    LiveData<List<DatabaseEquipmentMininmal>> getMinimalItems();

    @Query("SELECT COUNT(id) FROM equipment")
    LiveData<Integer> countItems();

    //Query für das Suchfeld
    @Query("SELECT id, name, position FROM equipment WHERE NOT name LIKE '%#X#x#X#%' AND (keywords OR name OR mSetName LIKE :key)")
    LiveData<List<DatabaseEquipmentMininmal>> searchItemsMinimal(String key);


    //=======================================================
    //======================DAO TRAYS========================
    //=======================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTray(TrayItem tray);

    @Query("SELECT * FROM tray WHERE NOT name LIKE '%#X#x#X#%' AND `group` LIKE :groupKey")
    LiveData<List<TrayItem>> getAllTrays(String groupKey);

    @Query("DELETE FROM tray")
    void deleteTray();

    @Query("DELETE FROM tray WHERE `group` LIKE :groupKey")
    void deleteTrayByGroup(String groupKey);

    @Query("SELECT COUNT(id) FROM tray")
    LiveData<Integer> countTrays();

    @Query("SELECT * FROM tray WHERE id LIKE :id AND NOT name LIKE '%#X#x#X#%'")
    LiveData<TrayItem> getTrayById(int id);

    //    @Query("SELECT positions FROM tray WHERE id LIKE :id")
//    @TypeConverters(Converters.class)
//    LiveData<List<Integer>> getPositionCoordinates(int id);


    //=======================================================
    //======================DAO IMAGE========================
    //=======================================================

    @Query("SELECT COUNT(id) FROM image")
    LiveData<Integer> countImage();

    @Query("SELECT * FROM image")
    LiveData<List<ImageItem>> getAllImages();

    @Query("SELECT * FROM image WHERE id LIKE :id")
    LiveData<ImageItem> findImageByID(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImages(ImageItem image);

    @Query("DELETE FROM image")
    void deleteImage();

    @Query("DELETE FROM image WHERE `group` LIKE :groupKey")
    void deleteImageByGroup(String groupKey);

    @Query("SELECT * FROM image WHERE categoryId LIKE :id")
    LiveData<ImageItem> getImageByCatID(int id);

}
