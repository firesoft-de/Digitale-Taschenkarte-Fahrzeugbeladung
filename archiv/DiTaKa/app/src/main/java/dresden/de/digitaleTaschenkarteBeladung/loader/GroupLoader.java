package dresden.de.digitaleTaschenkarteBeladung.loader;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import dresden.de.digitaleTaschenkarteBeladung.data.Group;
import dresden.de.digitaleTaschenkarteBeladung.util.Util_Http;

import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_QUERY_GET_TABLE;
import static dresden.de.digitaleTaschenkarteBeladung.util.Util_Http.SERVER_TABLE_GROUP;

public class GroupLoader extends AsyncTaskLoader<ArrayList<Group>> {

    private String url;

    public GroupLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Nullable
    @Override
    public ArrayList<Group> loadInBackground() {
        return requestGroups(url);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    private ArrayList<Group> requestGroups(String url) {

        String response = Util_Http.request((url + SERVER_QUERY_GET + SERVER_QUERY_GET_TABLE + SERVER_TABLE_GROUP));
        ArrayList<Group> list = jsonGroupParsing(response);

        return list;
    }

    /**
     * Führt die notwendige Dekodierung des JSON Strings in Group Objekte durch
     * @param response Serverantwort als String
     * @return Liste mit den dekodierten Objekten
     */
    private static ArrayList<Group> jsonGroupParsing(String response) {

        ArrayList<Group> list  = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(response);
            JSONArray responseArray = baseJsonResponse.getJSONArray("OUTPUT");

            for (int i = 0; i < responseArray.length(); i ++) {
                JSONObject object =  responseArray.getJSONObject(i);
                Group group = new Group(object);
                list.add(group);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            //TODO: Ordentliche Fehlerbehandlung
        }
        return list;
    }

}
