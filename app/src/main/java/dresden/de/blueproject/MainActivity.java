package dresden.de.blueproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        Fragment beladungFragment = new TrayFragment();
        ft.replace(R.id.MainFrame, beladungFragment);
        ft.commit();
    }
}
