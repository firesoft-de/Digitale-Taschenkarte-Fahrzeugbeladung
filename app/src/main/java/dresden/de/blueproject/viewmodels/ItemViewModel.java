package dresden.de.blueproject.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import dresden.de.blueproject.data.DatabaseEquipment;
import dresden.de.blueproject.data.DatabaseEquipmentObject;
import dresden.de.blueproject.data.DatabaseRepository;


/**
 * Diese Klasse stellt die ViewModellkomponenten in der MVVM Architektur dar. Sie bietet dem zugehörigen Fragment Methoden an mit denen auf die Datenbank zugegriffen wird.
 */
public class ItemViewModel extends ViewModel{

    private DatabaseRepository repository;

    public ItemViewModel(DatabaseRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<DatabaseEquipmentObject>> getItems() {
        return repository.getItems();
    }

    public void deleteItem(int id) {

        deleteItemTask task = new deleteItemTask();
        task.execute(id);

    }

    private class deleteItemTask extends AsyncTask<Integer,Void,Void> {

        @Override
        protected Void doInBackground(Integer... id) {
            repository.deleteItem(id[0]);
            return null;
        }
    }

}
