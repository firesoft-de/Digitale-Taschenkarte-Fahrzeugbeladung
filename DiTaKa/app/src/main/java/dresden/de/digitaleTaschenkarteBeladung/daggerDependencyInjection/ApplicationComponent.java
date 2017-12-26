package dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import dresden.de.digitaleTaschenkarteBeladung.SearchableActivity;
import dresden.de.digitaleTaschenkarteBeladung.fragments.DataImportFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.DebugFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.DetailFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.ItemFragment;
import dresden.de.digitaleTaschenkarteBeladung.fragments.TrayFragment;

/**
 * Diese Klasse bildet das Grundgerüst für die Dagger2 gestützte Abhängigkeitsinjection die alles weitere erleichtert.
 * Entnommen aus https://www.youtube.com/watch?v=LCOKWgHdBvE
 */

@Singleton
@Component(modules = {ApplicationModule.class, RoomModule.class})
public interface ApplicationComponent {

    void inject(TrayFragment trayFragment);
    void inject(DataImportFragment dataImportFragment);
    void inject(ItemFragment itemFragment);
    void inject(DebugFragment debugFragment);
    void inject(DetailFragment detailFragment);
    void inject(SearchableActivity searchableActivity);

    Application application();

}
