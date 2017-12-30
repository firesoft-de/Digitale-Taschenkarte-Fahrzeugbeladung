package dresden.de.digitaleTaschenkarteBeladung.util;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.TrayItem;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;

/**
 * Loaderklasse für die Behälterelemente
 */
public class TrayLoader extends AsyncTaskLoader<List<TrayItem>> {

    private static final String LOG_TAG = "TrayLoader_LOG";

    private String url;
    private int version;

    public TrayLoader(Context context, String url, int version){
        super(context);
        this.url = url;
        this.version = version;
    }

    //Hauptmethode der Klasse. Bewältigt die Hintergrundarbeit
    @Override
    public List<TrayItem> loadInBackground() {
        return Util_Http.requestTray(url, version);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


}