package dresden.de.digitaleTaschenkarteBeladung.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import dresden.de.digitaleTaschenkarteBeladung.MainActivity;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;

public class Util {

    //Konstanten für die Fragmenterkennung
//    public static final String FRAGMENT_MAIN = "100";
    public static final String FRAGMENT_DATA  = "101";
    public static final String FRAGMENT_LIST_TRAY = "102";
    public static final String FRAGMENT_LIST_ITEM = "103";
    public static final String FRAGMENT_DETAIL = "104";
    public static final String FRAGMENT_DEBUG = "105";
    public static final String FRAGMENT_SETTINGS = "106";
    public static final String FRAGMENT_ABOUT = "107";

    public static final String ARGS_URL = "ARGS_URL";
    public static final String ARGS_VERSION = "ARGS_VERSION";
    public static final String ARGS_DBSTATE = "ARGS_DBSTATE";
    public static final String ARGS_CALLFORUSER = "ARGS_CALLFORUSER";

    public static final String PREFS_NAME="dresden.de.digitaleTaschenkarteBeladung";
    public static final String PREFS_URL="dresden.de.digitaleTaschenkarteBeladung.url";
    public static final String PREFS_DBVERSION="dresden.de.digitaleTaschenkarteBeladung.dbversion";

    public static final String FILE_DESTINATION_IMAGE = "image";

    public static void LogDebug(String tag, String message) {
        if (MainActivity.DEBUG_ENABLED) {
            Log.d(tag, message);
        }
    }

    public static void LogError(String tag, String message) {
        if (MainActivity.DEBUG_ENABLED) {
            Log.e(tag,message);
        }
    }

    public static String saveImage(int id, Bitmap image, Context context) {

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
        return file.getPath();
    }

    public static Bitmap openImage(ImageItem imageItem, Context context) {
        Bitmap image;
        File file;
        FileInputStream stream;
        int id = imageItem.getId();
        try {
            File directory = context.getDir(FILE_DESTINATION_IMAGE, Context.MODE_PRIVATE);
            file = new File(directory, id +".jpg");
            stream = new FileInputStream(file);
            image = BitmapFactory.decodeStream(stream);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
