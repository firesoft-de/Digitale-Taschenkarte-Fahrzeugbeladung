package dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dresden.de.digitaleTaschenkarteBeladung.data.Group;
import dresden.de.digitaleTaschenkarteBeladung.util.GroupManager;
import dresden.de.digitaleTaschenkarteBeladung.util.PreferencesManager;
import dresden.de.digitaleTaschenkarteBeladung.util.VariableManager;

/**
 * Dagger Module in dem die DI's definiert werden, die nicht mit ROOM oder der Application zusammenhängen
 */
@Module
public class WorkModule {

    private final Application application;

    public WorkModule(Application application) {
        this.application = application;
    }

    @Provides
    Context provideContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    PreferencesManager providePrefManager(Context context, GroupManager groupManager, VariableManager variableManager) {
        return new PreferencesManager(context, groupManager, variableManager);
    }

    @Provides
    @Singleton
    VariableManager provideVariableManager() {
        return new VariableManager();
    }

    @Provides
    @Singleton
    GroupManager provideGroupManager() {
        return  new GroupManager();
    }

}
