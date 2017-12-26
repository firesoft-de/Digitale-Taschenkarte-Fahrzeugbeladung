package dresden.de.blueproject.viewmodels;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import dresden.de.blueproject.data.DatabaseRepository;


/**
 * Mit dieser Klasse werden die Viewmodels erstellt und mit Argumenten versehen
 */
public class CustomViewModelFactory implements ViewModelProvider.Factory{

    private final DatabaseRepository repository;

    public CustomViewModelFactory(DatabaseRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            return (T) new ItemViewModel(repository);
        }
        //TODO ANPASSEN AN DIE ANDEREN
        else if (modelClass.isAssignableFrom(DataFragViewModel.class)) {
            return (T) new DataFragViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(DebugViewModel.class)) {
            return (T) new DebugViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(repository);
        }
        else if (modelClass.isAssignableFrom(TrayViewModel.class)) {
            return (T) new TrayViewModel(repository);
        } /*
        else if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            return (T) new ItemViewModel(repository);
        }*/
        else {
            throw new IllegalArgumentException("ViewModel not found!");
        }

    }
}
