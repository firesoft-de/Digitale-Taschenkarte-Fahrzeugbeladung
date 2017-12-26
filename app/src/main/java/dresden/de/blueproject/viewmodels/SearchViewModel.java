package dresden.de.blueproject.viewmodels;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import dresden.de.blueproject.data.DatabaseRepository;
import dresden.de.blueproject.dataStructure.DatabaseEquipmentMininmal;

public class SearchViewModel extends ViewModel {

    private DatabaseRepository repository;

    public SearchViewModel(DatabaseRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<DatabaseEquipmentMininmal>> searchItems(String query) {

        return repository.searchItemsMinimal(query); }

}
