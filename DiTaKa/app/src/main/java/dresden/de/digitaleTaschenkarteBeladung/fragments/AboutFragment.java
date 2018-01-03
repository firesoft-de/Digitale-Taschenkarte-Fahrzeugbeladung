package dresden.de.digitaleTaschenkarteBeladung.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dresden.de.digitaleTaschenkarteBeladung.R;


public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        //Link setzen
        TextView tv = view.findViewById(R.id.about_tv2);
//        tv.append(" ");
//        tv.append(getString(R.string.about_git_url));

        return view;
    }

    //TODO: Versionsanzeige

}
