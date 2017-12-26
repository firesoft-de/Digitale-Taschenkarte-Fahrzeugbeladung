package dresden.de.digitaleTaschenkarteBeladung.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Data;


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

        task.execute(Util_Data.castItemToArray(items));

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
