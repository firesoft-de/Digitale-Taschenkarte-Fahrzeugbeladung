package dresden.de.digitaleTaschenkarteBeladung.loader;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;

public class GroupLoader extends AsyncTaskLoader<ArrayList<String>> {

    private String url;
    private int version;

    public GroupLoader(Context context, String url, int version) {
        super(context);
        this.url = url;
        this.version = version;
    }

    @Nullable
    @Override
    public ArrayList<String> loadInBackground() {
        return Util_Http.requestGroups(url,version);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
