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

package firesoft.de.ditaka.manager;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import static firesoft.de.ditaka.util.Definitions.*;

/**
 * Manager Klasse mit der die gespeicherten Einstellungen zentral verwaltet und bereitgestellt werden
 */
@Singleton
public class PrefrencesManager {

    // region Variablen

    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    // endregion

    // region Einstellungsvariablen

    /**
     * Enthält die URL des Servers
     */
    private String url;

    /**
     * Enthält den Nutzernamen
     */
    private String user;

    /**
     * Enthält das Nuterpasswort
     */
    private String password;

    // endregion


    @Inject
    public PrefrencesManager(Context context) {

        this.context = context;

    }

    // region Speichermethoden und sonstige Methoden

    /**
     * Speichert die Einstellungen in den PREFS
     */
    public void save()  {
        editor = preferences.edit();

        editor.putString(URL,url);
        editor.putString(USER,user);
        editor.putString(PASSWORD,password);

        editor.apply();
    }

    /**
     * Löscht alle Einstellungen
     */
    public void delete() {
        editor = preferences.edit();
        editor.clear();
        editor.apply();
        reset();
    }

    /**
     * Setzt die Einstellungen zurück
     */
    public void reset() {
        url = "";
        user = "";
        password = "";
    }


    // endregion

    // region Lademethoden

    /**
     * Lädt die Einstellungen aus der Preference Datei
     * @throws PackageManager.NameNotFoundException Falls die eingegebene Version nicht gefunden wird, wird ein Fehler geworfen.
     */
    public void load() throws PackageManager.NameNotFoundException {

        // Preference Objekt erstellen
        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        // Aktuelle Versionsnummer abrufen
        PackageInfo packageInfo;

        packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);

        // Durch das positionieren von default: am oberen Ende der Switch-Anweisung und das absteigende Aufführen der Load-Anweisungen wird bei unklaren Versionsnummern immer die zuletzt gültige aufgerufen.
        // Dadurch muss nicht für jede Versionsnummer ein eigener Case definiert werden.
        switch (packageInfo.versionCode) {

            default:

//            case xyz:
//                // Version xyz
//                loadvxyz();
//                break;

            case 1:
                // Version 0.1
                loadv1();
                break;
        }

    }

    /**
     * Lädt die Einstellungen der Appversion 0.1 und aller kompatiblen Versionen
     */
    private void loadv1() {
        url = preferences.getString(URL, "");
        user = preferences.getString(USER, "");
        password = preferences.getString(PASSWORD, "");
    }

    // endregion

    // region Getter und Setter für Einstellungsvariablen

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // endregion

}
