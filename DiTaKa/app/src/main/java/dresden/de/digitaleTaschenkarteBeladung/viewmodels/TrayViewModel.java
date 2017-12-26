package dresden.de.digitaleTaschenkarteBeladung.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;
import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;


public class TrayViewModel extends ViewModel {

    //TODO: TrayViewModel implementieren

    private DatabaseRepository repository;

    public TrayViewModel(DatabaseRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<TrayItem>> getTrays() {return repository.getTrays();}

    public void deleteTrays() {repository.deleteAllTrays();}


}
