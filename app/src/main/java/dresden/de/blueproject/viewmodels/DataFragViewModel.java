package dresden.de.blueproject.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Database;
import android.os.AsyncTask;

import java.util.List;

import dresden.de.blueproject.data.DatabaseRepository;
import dresden.de.blueproject.data.EquipmentItem;

/**
 * ViewModel für das Datenimport Fragment
 */

public class DataFragViewModel extends ViewModel {

    private DatabaseRepository repository;

    public DataFragViewModel(DatabaseRepository repository) {
        this.repository = repository;
    }

    public void addItems(List<EquipmentItem> items) {

        addItemsTask task = new addItemsTask();
        task.execute((EquipmentItem[]) items.toArray());

    }

    private class addItemsTask extends AsyncTask<EquipmentItem,Void,Void> {

        @Override
        protected Void doInBackground(EquipmentItem... items) {
            for (int i = 0; i< items.length; i++) {
                repository.add(items[i]);
            }
            return null;
        }
    }


}
