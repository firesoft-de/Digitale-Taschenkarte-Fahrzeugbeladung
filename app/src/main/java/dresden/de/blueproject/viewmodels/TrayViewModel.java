package dresden.de.blueproject.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.persistence.room.Database;

import java.util.List;

import dresden.de.blueproject.data.DatabaseRepository;
import dresden.de.blueproject.data.TrayItem;


public class TrayViewModel extends ViewModel {

    //TODO: TrayViewModel implementieren

    private DatabaseRepository repository;

    public TrayViewModel(DatabaseRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<TrayItem>> getTrays() {return repository.getTrays();}

    public void deleteTrays() {repository.deleteAllTrays();}


}
