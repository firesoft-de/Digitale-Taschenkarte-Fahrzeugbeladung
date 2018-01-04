package dresden.de.digitaleTaschenkarteBeladung.util;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util.LogDebug;

public class VersionLoader extends AsyncTaskLoader<Integer> {

    private static final String LOG_TAG = "VersionLoader_LOG";

    private String url;
    Context context;

    public VersionLoader(Context context, String url) {
        super(context);
        this.context = context;
        this.url = url;
    }

    @Override
    public Integer loadInBackground() {

        LogDebug(LOG_TAG,"Start");

        //TODO: Pausieren verbessern
//        for (int i = 0; i < 20000000; i++) {
//            int x = 1+1;
//        }

        LogDebug(LOG_TAG,"End");

        return Util_Http.checkVersion(url);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }



}
