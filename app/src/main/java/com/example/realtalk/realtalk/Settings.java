package com.example.realtalk.realtalk;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by alexgomes on 2015-09-29. - alex.09hg@gmail.com
 */
public class Settings extends Fragment {

    TextView txtSettings;
    ImageButton btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_screen, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtSettings = (TextView) getActivity().findViewById(R.id.txtSettings);
        txtSettings.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        btnBack = (ImageButton) getActivity().findViewById(R.id.btnBackButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
