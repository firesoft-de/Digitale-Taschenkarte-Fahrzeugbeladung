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
