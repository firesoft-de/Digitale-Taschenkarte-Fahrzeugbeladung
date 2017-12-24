package dresden.de.blueproject.daggerDependencyInjection;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dresden.de.blueproject.data.DatabaseEquipment;
import dresden.de.blueproject.data.DatabaseEquipmentDAO;
import dresden.de.blueproject.data.DatabaseRepository;
import dresden.de.blueproject.viewmodels.CustomViewModelFactory;

/**
 * In dieser Klasse werden verschiedene Elemente für Dagger definiert. Letztendlich wird Dagger hiermit gesagt:
 * "Wenn du auf einen dieser Datentypen stößt, dann kriegst du mit den hier genannten Methoden die passende Instanz/Daten."
 * https://www.youtube.com/watch?v=LCOKWgHdBvE
 */

@Module
public class RoomModule {


    private final DatabaseEquipment database;

    public RoomModule(Application application) {
        this.database = Room.databaseBuilder(
                application,
                DatabaseEquipment.class,
                "equipment"
        ).build();
    }

    @Provides
    @Singleton
    DatabaseRepository provideRepository(DatabaseEquipmentDAO itemDAO){
        return new DatabaseRepository(itemDAO);
    }

    @Provides
    @Singleton
    DatabaseEquipmentDAO provideDao(DatabaseEquipment database){
        return database.equipmentDAO();
    }

    @Provides
    @Singleton
    DatabaseEquipment provideDatabase(Application application){
        return database;
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideViewModelFactory(DatabaseRepository repository){
        return new CustomViewModelFactory(repository);
    }

}
