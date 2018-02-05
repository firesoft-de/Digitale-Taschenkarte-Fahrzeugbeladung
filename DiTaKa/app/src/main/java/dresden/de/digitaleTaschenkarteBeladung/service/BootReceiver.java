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

package dresden.de.digitaleTaschenkarteBeladung.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


/**
 * Diese Klasse definiert einen BroadcastReceivier, mit dem beim Abschluss des Bootvorgangs der Hintergrundservice gestartet wird
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            startBackgroundService(context);
        }

    }

    public static void startBackgroundService(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        Intent serviceIntent = new Intent(context, BackgroundService.class);
        PendingIntent startServiceIntent = PendingIntent.getService(context,0,serviceIntent,0);

        //Server wird alle 24 Stunden um 16:00 Uhr überprüft
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //AlarmManager aktivieren
        if (alarmManager != null) { //AlarmManager.INTERVAL_DAY
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,startServiceIntent);
        }
    }

    public static void stopBackgroundService(Context context) {
        context.stopService(new Intent(context, BackgroundService.class));
    }
}
