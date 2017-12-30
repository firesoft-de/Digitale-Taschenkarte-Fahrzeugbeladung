package dresden.de.digitaleTaschenkarteBeladung.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
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

    public void addTrays(List<TrayItem> trays) {

        addTrayTask task = new addTrayTask();

        task.execute(Util_Data.castTrayToArray(trays));

    }

    private class addTrayTask extends AsyncTask<TrayItem,Void,Void> {

        @Override
        protected Void doInBackground(TrayItem... trays) {
            for (int i = 0; i< trays.length; i++) {
                repository.add(trays[i]);
            }
            return null;
        }
    }

    public LiveData<Integer> countTrays() {
        return repository.countTrays();
    }

    public LiveData<Integer> countItems() {
        return repository.countItems();
    }


}
