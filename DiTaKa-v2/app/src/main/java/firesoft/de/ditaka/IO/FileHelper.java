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

package firesoft.de.ditaka.IO;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static firesoft.de.ditaka.util.Definitions.FILE_DESTINATION_IMAGE;

/**
 * Stellt Methoden zum Arbeiten im Android-Dateisystem bereit
 */
public class FileHelper {

    public static void saveImage(Context context, int id, Bitmap image) {

        //https://stackoverflow.com/questions/649154/save-bitmap-to-location
        FileOutputStream stream = null;
        File file = null;
        try {
            String path = Environment.getDataDirectory().toString();
            Integer counter = 0;
            File directory = context.getDir(FILE_DESTINATION_IMAGE, Context.MODE_PRIVATE);

            file = new File(directory, id +".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.

            if (!file.canWrite()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG,85, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Eventuell muss noch der Dateipfad mit file.getPath(); abgelegt werden.

    }

}
