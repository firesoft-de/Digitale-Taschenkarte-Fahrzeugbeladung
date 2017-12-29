package dresden.de.digitaleTaschenkarteBeladung;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;


/**
 * Loader für die Gegenstände
 */
public class ItemLoader extends AsyncTaskLoader<List<EquipmentItem>> {

    private static final String LOG_TAG = "ItemLoader_LOG";

    private String url;
    private int version;

    public ItemLoader(Context context, String url, int version){

        super(context);
        this.url = url;
        this.version = version;
    }

    //Hauptmethode der Klasse. Bewältigt die Hintergrundarbeit
    @Override
    public List<EquipmentItem> loadInBackground() {

        Util_Http utilities = new Util_Http();

        //TODO: URL weitergeben
        return utilities.requestItems(url, version);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


}
