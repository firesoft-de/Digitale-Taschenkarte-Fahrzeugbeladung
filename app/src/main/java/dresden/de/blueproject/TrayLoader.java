package dresden.de.blueproject;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import dresden.de.blueproject.dataStructure.TrayItem;
import util.Util_Http;

/**
 * Loaderklasse für die Behälterelemente
 */
public class TrayLoader extends AsyncTaskLoader<List<TrayItem>> {

    private static final String LOG_TAG = "TrayLoader";

    public TrayLoader(Context context){
            super(context);
            }

    //Hauptmethode der Klasse. Bewältigt die Hintergrundarbeit
    @Override
    public List<TrayItem> loadInBackground() {

        Util_Http utilities = new Util_Http();

        return utilities.requestTray();
    }

}