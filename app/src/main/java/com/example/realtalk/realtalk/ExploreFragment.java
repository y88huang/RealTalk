package com.example.realtalk.realtalk;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class ExploreFragment extends android.support.v4.app.Fragment {

    TextView titleExplore;
    ImageButton btnClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleExplore = (TextView)getActivity().findViewById(R.id.titleExplore);
        titleExplore.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        btnClose = (ImageButton)getActivity().findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(ExploreFragment.this).commit();
            }
        });
    }
}
