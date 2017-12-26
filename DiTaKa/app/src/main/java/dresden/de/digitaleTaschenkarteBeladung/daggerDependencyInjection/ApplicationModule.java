package dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final ApplicationForDagger application;

    public ApplicationModule(ApplicationForDagger application) {
        this.application = application;
    }

    @Provides
    ApplicationForDagger provideDaggerApplication(){
        return application;
    }

    @Provides
    Application provideApplication(){
        return application;
    }
}
