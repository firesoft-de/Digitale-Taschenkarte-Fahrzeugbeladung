package dresden.de.blueproject.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import dresden.de.blueproject.dataStructure.DatabaseEquipmentMininmal;
import dresden.de.blueproject.data.DatabaseRepository;
import dresden.de.blueproject.data.EquipmentItem;


/**
 * Diese Klasse stellt die ViewModellkomponenten in der MVVM Architektur dar. Sie bietet dem zugehörigen Fragment Methoden an mit denen auf die Datenbank zugegriffen wird.
 */
public class ItemViewModel extends ViewModel{

    private DatabaseRepository repository;

    public ItemViewModel(DatabaseRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<EquipmentItem>> getItems() {
        return repository.getItems();
    }

    public LiveData<EquipmentItem> getItem(int id) {return repository.getItemByID(id);}

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

    public LiveData<List<DatabaseEquipmentMininmal>> getItemsByCatID(int catID) {return repository.getItemByCatID(catID); }

}
