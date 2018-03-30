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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.Toast;

import junit.runner.Version;

import javax.inject.Inject;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.R;
import dresden.de.digitaleTaschenkarteBeladung.daggerDependencyInjection.CustomApplication;
import dresden.de.digitaleTaschenkarteBeladung.loader.VersionLoader;
import dresden.de.digitaleTaschenkarteBeladung.util.PreferencesManager;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;


/**
 * Diese Klasse definiert einen Service mit dem periodisch der Server nach einer neuen Datenbankversion abgefragt wird
 */
public class BackgroundService extends Service {

    private static final String LOG_TRACE = "VersionService";

    @Inject
    PreferencesManager pManager;

    @Override
    public void onCreate() {
        super.onCreate();

        ((CustomApplication) getApplication())
                .getApplicationComponent()
                .inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        pManager.load();

        VersionLoader vLoader = new VersionLoader(getApplicationContext(),pManager.getUrl());

        vLoader.registerListener(0, new Loader.OnLoadCompleteListener<Integer>() {
            @Override
            public void onLoadComplete(@NonNull Loader<Integer> loader, @Nullable Integer data) {
                VersionLoader versionLoader = (VersionLoader) loader;

                if (versionLoader.getVersion() > pManager.getDbVersion()) {
                    buildNotification();

                    //Hintergrundprozess wieder beenden
                    stopSelf();
                }
            }
        });

        vLoader.startLoading();

        return super.onStartCommand(intent, flags, startId);
    }

    private void buildNotification() {
        //Notification erstellen
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_symbol);
        Notification notification;

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction("DB_UPDATE");
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //API Version prüfen und entsprechend eine Notification mit oder ohne NotificationChannel erstellen
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // The id of the channel.
            String id = "channel_01";
            // The user-visible name of the channel.
            CharSequence name = "Datenbankbenachrichtigung"; //getString(R.string.channel_name);
            // The user-visible description of the channel.
            String description = "Benachrichtigungen rund um die Datenbank";
            int importance = 0;
            importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);

            notification = new Notification.Builder(getApplicationContext(), id)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.ic_notif_db_update)
                    .setContentTitle("Datenbankupdate")
                    .setContentText("Es ist eine neue Datenbankversion verfügbar.")
                    .setChannelId(mChannel.getId())
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .build();

        } else {
            notification = new Notification.Builder(getApplicationContext())
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.ic_notif_db_update)
                    .setContentTitle("Datenbankupdate")
                    .setContentText("Es ist eine neue Datenbankversion verfügbar.")
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .build();
        }

        notificationManager.notify(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
