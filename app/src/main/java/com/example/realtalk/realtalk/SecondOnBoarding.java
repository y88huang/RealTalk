package com.example.realtalk.realtalk;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

/**
 * Created by Alex Gomes on Oct 6,2015. - alex.09hg@gmail.com
 */
public class SecondOnBoarding extends Fragment {

    TextView txtSkip,txtTitle;
    SwipeFlingAdapterView swipeCards;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> al;
    int i;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_on_boarding, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtSkip = (TextView)getActivity().findViewById(R.id.txtSkip);
        txtSkip.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));

        txtTitle= (TextView)getActivity().findViewById(R.id.txtTitle);
        txtTitle.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        swipeCards = (SwipeFlingAdapterView)getActivity().findViewById(R.id.frame);
        swipeCards.removeAllViewsInLayout();

        al = new ArrayList<String>();
        al.add("php");
        al.add("c");
        al.add("python");
        al.add("java");

        arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.on_boarding_swipe_item, R.id.helloText, al);
        swipeCards.setAdapter(arrayAdapter);

        swipeCards.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(getActivity(), "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(getActivity(), "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
//                 Ask for more data here
                al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = swipeCards.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

    }
}
