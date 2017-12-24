package dresden.de.blueproject;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import dresden.de.blueproject.data.EquipmentItem;
import dresden.de.blueproject.dataStructure.ItemAdapter;

public class SearchableActivity extends AppCompatActivity {

    private static final String LOG_TAG="SearchableActivity_LOG";

    ArrayList<EquipmentItem> equipmentItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        //Search-Intent abrufen und auswerten
        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            Bundle bundle = intent.getExtras();

            try {
                equipmentItems =  bundle.getParcelableArrayList("EL");
            } catch (NullPointerException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Fehler beim Abrufen des Bundles in der SearchActivity! Cause: "+e.getCause() + " | Nachricht: " + e.getMessage());
            }

            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            ArrayList<EquipmentItem> res =  search(searchQuery);

            presentData(res);

        }

    }

    private ArrayList<EquipmentItem> search(String query) {

        //Suchanfrange aufbereiten
        String[] searchArray = (query.toLowerCase()).split(" ");

        ArrayList<EquipmentItem> resultList = new ArrayList<>();

        for (int count = 0; count <equipmentItems.size(); count++) {

            EquipmentItem item = equipmentItems.get(count);

            //Suche mittels Keywords

            for (int i = 0; i < searchArray.length; i++) {

                ArrayList<String> keywords = item.getKeywords();

                for (int x = 0; x < keywords.size(); x++) {
                    if (keywords.get(x).contains(searchArray[i]) ) {
                        resultList.add(item);
                        i = searchArray.length;
                        x = keywords.size();
                    }
                }
            }
        }

        return resultList;

    }

    private void presentData(ArrayList<EquipmentItem> searchResults) {

        ItemAdapter searchAdapter = new ItemAdapter(this, searchResults);

        ListView lv = (ListView) this.findViewById(R.id.ListViewSearch);

        lv.setAdapter(searchAdapter);

        searchAdapter.notifyDataSetChanged();

    }

}
