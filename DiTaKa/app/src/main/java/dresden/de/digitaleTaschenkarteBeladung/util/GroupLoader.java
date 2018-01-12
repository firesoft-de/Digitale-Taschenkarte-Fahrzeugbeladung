package dresden.de.digitaleTaschenkarteBeladung.util;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

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
