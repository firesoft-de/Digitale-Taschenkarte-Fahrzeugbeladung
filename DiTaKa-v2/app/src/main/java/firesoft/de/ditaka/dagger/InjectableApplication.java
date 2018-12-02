/*
 *     Diese App stellt die Beladung von BOS Fahrzeugen in digitaler Form dar.
 *     Copyright (C) 2018  David Schlossarczyk
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     For the full license visit https://www.gnu.org/licenses/gpl-3.0.
 */
package firesoft.de.ditaka.dagger;

import android.app.Application;

public class InjectableApplication extends Application {

    private InjectionComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        createComponent();
    }

    public InjectionComponent getComponent() {

        createComponent();

        return  mComponent;
    }

    private void createComponent() {

        // Falls noch keine Instanz vorhanden ist (was eigentlich der Fall sein sollte), muss diese erstellt werden
        if (mComponent == null) {

            // Component instanzieren
            mComponent = DaggerInjectionComponent.builder()
                    // Das Modul implementieren und mit den Basisabhängigkeiten versorgen (MainActivity)
                    .injectionModule(new InjectionModule(this))
                    .switchInjectionModule(new SwitchInjectionModule())
                    .build();
        }

    }

}
