package com.example.realtalk.realtalk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by alexgomes on 2015-09-29.
 */
public class ProfileBookMarksFragment extends Fragment {

    TextView yourBookMarksGoHere,readLater;
    ImageView bookMarkView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_bookmark, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        yourBookMarksGoHere = (TextView)getActivity().findViewById(R.id.yourBookMarksHere);
        yourBookMarksGoHere.setText(getString(R.string.yourBookMarksGoHere));
        yourBookMarksGoHere.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        bookMarkView = (ImageView)getActivity().findViewById(R.id.imageView);
        yourBookMarksGoHere.setVisibility(View.VISIBLE);
        bookMarkView.setVisibility(View.VISIBLE);

        readLater = (TextView)getActivity().findViewById(R.id.readLater);
        readLater.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));
    }
}