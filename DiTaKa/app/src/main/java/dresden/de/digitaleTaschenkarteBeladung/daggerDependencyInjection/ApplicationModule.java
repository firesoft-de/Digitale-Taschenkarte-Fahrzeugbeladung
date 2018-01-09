/*  Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
    Copyright (C) 2017  David Schlossarczyk

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    For the full license visit https://www.gnu.org/licenses/gpl-3.0.*/

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
