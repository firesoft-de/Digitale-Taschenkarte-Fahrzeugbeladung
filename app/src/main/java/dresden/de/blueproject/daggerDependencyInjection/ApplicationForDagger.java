package dresden.de.blueproject.daggerDependencyInjection;

import android.app.Application;

/**
 * Created by David on 24.12.2017.
 */

public class ApplicationForDagger extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .roomModule(new RoomModule(this))
                .build();

    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

}
