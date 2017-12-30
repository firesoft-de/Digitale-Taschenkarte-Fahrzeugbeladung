package dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseClass;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseDAO;
import dresden.de.digitaleTaschenkarteBeladung.data.DatabaseRepository;
import dresden.de.digitaleTaschenkarteBeladung.viewmodels.CustomViewModelFactory;

/**
 * In dieser Klasse werden verschiedene Elemente für Dagger definiert. Letztendlich wird Dagger hiermit gesagt:
 * "Wenn du auf einen dieser Datentypen stößt, dann kriegst du mit den hier genannten Methoden die passende Instanz/Daten."
 * https://www.youtube.com/watch?v=LCOKWgHdBvE
 */

@Module
public class RoomModule {


    private final DatabaseClass database;

    public RoomModule(Application application) {
        this.database = Room.databaseBuilder(
                application,
                DatabaseClass.class,
                "equipment"
        ).build();
    }

    @Provides
    @Singleton
    DatabaseRepository provideRepository(DatabaseDAO itemDAO){
        return new DatabaseRepository(itemDAO);
    }

    @Provides
    @Singleton
    DatabaseDAO provideDao(DatabaseClass database){
        return database.equipmentDAO();
    }

    @Provides
    @Singleton
    DatabaseClass provideDatabase(Application application){
        return database;
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideViewModelFactory(DatabaseRepository repository){
        return new CustomViewModelFactory(repository);
    }

}
