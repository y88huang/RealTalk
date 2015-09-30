package com.example.realtalk.realtalk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by alexgomes on 2015-09-29.
 */
public class ProfileNextStepsFragment extends Fragment {


    TextView textView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_next_steps, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        textView = (TextView)getActivity().findViewById(R.id.hey);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "HELLO", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
