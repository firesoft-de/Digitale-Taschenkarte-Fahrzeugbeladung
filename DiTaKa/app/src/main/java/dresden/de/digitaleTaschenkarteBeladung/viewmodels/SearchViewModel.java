package dresden.de.digitaleTaschenkarteBeladung.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;
import dresden.de.digitaleTaschenkarteBeladung.dataStructure.DatabaseEquipmentMininmal;

public class SearchViewModel extends ViewModel {

    private DatabaseRepository repository;

    public SearchViewModel(DatabaseRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<DatabaseEquipmentMininmal>> searchItems(String query) {

        return repository.searchItemsMinimal(query); }

}
