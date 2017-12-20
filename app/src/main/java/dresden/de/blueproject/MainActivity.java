package dresden.de.blueproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<TrayItem> trays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Test um ein anderes Fragment darzustellen
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        Fragment beladungFragment = new TrayFragment();
        ft.replace(R.id.MainFrame, beladungFragment);
        ft.commit();



    }
}
