package dresden.de.digitaleTaschenkarteBeladung.util;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import dresden.de.digitaleTaschenkarteBeladung.data.EquipmentItem;
import dresden.de.digitaleTaschenkarteBeladung.data.ImageItem;

public class ImageLoader extends AsyncTaskLoader<List<ImageItem>> {

    private static final String LOG_TAG = "ImageLoader_LOG";

    private String url;
    private int version;

    public ImageLoader(Context context, String url, int version){
            super(context);
            this.url = url;
            this.version = version;
    }

    //Hauptmethode der Klasse. Bewältigt die Hintergrundarbeit
    @Override
    public List<ImageItem> loadInBackground() {
        return Util_Http.requestImages(url, version, getContext());
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


}
