package dresden.de.blueproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataImportFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    private static  final String LOG_TAG="DataImportFragment_LOG";

    public DataImportFragment() {
        // Required empty public constructor

    }
    @Override
    public Loader onCreateLoader(int id, Bundle args) {

        switch (id) {
            case 1:
                //ID 1: Ein neuer ItemLoader wird gebraucht!
                return new  ItemLoader(getContext());

            case 2:
                //ID 2: Ein neuer TrayLoader wird gebraucht!
                return new TrayLoader(getContext());

            default:
                //Irgendwas ist schief gegangen -> Falsche ID
                Log.e(LOG_TAG, "Fehler beim starten des Loaders! Konnte keine zu einem Loader passende ID finden!");
                return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Den Backbutton in der Actionbar hinzufügen
/*        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.fragment_title_data);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        //TODO: Implementieren was passiert wenn der Loader fertig geworden ist! -> Schreiben der Daten in eine Datenbank und weiterreichen an die Liste im RAM
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //TODO: Hier die Dinge zurücksetzen die zurückgesetzt werden müssen
    }
}
