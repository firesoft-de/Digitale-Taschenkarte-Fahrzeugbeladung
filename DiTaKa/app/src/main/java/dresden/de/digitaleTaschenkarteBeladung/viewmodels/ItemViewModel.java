package dresden.de.digitaleTaschenkarteBeladung.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseEquipmentMininmal;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;
import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;


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

    public LiveData<ImageItem> getImageByCatID(int catID) {return repository.getImageByCatID(catID);}

    public LiveData<TrayItem> getTrayItem(int id) {return repository.getTrayById(id);}

    //Abfrage um die Positionskoordinaten aus dem Tray zu erhalten
//    public LiveData<List<Integer>> getPositionCoordinates(int catID) {return repository.getPositionCoordinates(catID);}

}


