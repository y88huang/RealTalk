package com.example.realtalk.realtalk;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

/**
 * Created by Alex Gomes on Oct 6,2015. - alex.09hg@gmail.com
 */
public class SecondOnBoarding extends Fragment {

    TextView txtSkip,txtTitle;
    ImageView btnReject, btnAccept;
    SwipeFlingAdapterView swipeCards;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> cardList;
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

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeScreen = new Intent(getActivity(),HomeScreen.class);
                getActivity().startActivity(homeScreen);
            }
        });
        btnAccept = (ImageView)getActivity().findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Accepted!", Toast.LENGTH_SHORT).show();
            }
        });

        btnReject= (ImageView)getActivity().findViewById(R.id.btnReject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Rejected!", Toast.LENGTH_SHORT).show();
            }
        });

        swipeCards = (SwipeFlingAdapterView)getActivity().findViewById(R.id.frame);
        swipeCards.removeAllViewsInLayout();

        cardList = new ArrayList<String>();
        cardList.add("I wear every single chain, even when I’m in the house...");
        cardList.add("Sometimes it's the journey that teaches you a lot about your destination.");
        cardList.add("When it comes to knowing what to say, to charm, I always had it.");

        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.on_boarding_swipe_item, R.id.cardText, cardList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position,convertView,parent);
                TextView cardText = (TextView)v.findViewById(R.id.cardText);
                cardText.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));
                return v;
            }
        };
        swipeCards.setAdapter(arrayAdapter);


        swipeCards.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                cardList.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Toast.makeText(getActivity(), "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(getActivity(), "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
//                 Ask for more data here
//                cardList.add("XML".concat(String.valueOf(i)));
                cardList.add("Sometimes it's the journey that teaches you a lot about your destination.");
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
