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

import javax.inject.Singleton;

import dagger.Component;
import firesoft.de.ditaka.MainActivity;
import firesoft.de.ditaka.fragments.aboutFragment.AboutFragment;
import firesoft.de.ditaka.fragments.trayFragment.TrayFragment;
import firesoft.de.ditaka.interfaces.SwitchFragmentInterface;

@Singleton
@Component(modules = {InjectionModule.class, SwitchInjectionModule.class})
public interface InjectionComponent {

    void inject(MainActivity mainActivity);

    void inject(AboutFragment fragment);

    void inject(TrayFragment fragment);

}
