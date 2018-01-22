package dresden.de.digitaleTaschenkarteBeladung.loader;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import dresden.de.digitaleTaschenkarteBeladung.data.Group;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;

public class GroupLoader extends AsyncTaskLoader<ArrayList<Group>> {

    private String url;

    public GroupLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Nullable
    @Override
    public ArrayList<Group> loadInBackground() {
        return Util_Http.requestGroups(url);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
