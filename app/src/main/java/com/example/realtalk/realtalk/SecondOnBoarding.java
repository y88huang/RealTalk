package com.example.realtalk.realtalk;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

/**
 * Created by Alex Gomes on Oct 6,2015. - alex.09hg@gmail.com
 */
public class SecondOnBoarding extends Fragment {

    TextView txtSkip, txtTitle;
    ImageView btnReject, btnAccept;
    SwipeFlingAdapterView swipeCards;
    ArrayAdapter<SwipeCard> arrayAdapter;
    ArrayList<SwipeCard> cardList;
    String[] preferedCategoryString;
    int i, removedCounter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_on_boarding, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtSkip = (TextView) getActivity().findViewById(R.id.txtSkip);
        txtSkip.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));

        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        txtTitle.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeScreen = new Intent(getActivity(), HomeScreen.class);
                getActivity().startActivity(homeScreen);
            }
        });
        btnAccept = (ImageView) getActivity().findViewById(R.id.btnAccept);

        preferedCategoryString = null;
        preferedCategoryString = new String[5];
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeCards.getTopCardListener().selectRight();
                for (int i = 0; i < 5 ; i++) {
                    preferedCategoryString[i] = cardList.get(i).preferredCategories;
                }
            }
        });

        btnReject = (ImageView) getActivity().findViewById(R.id.btnReject);
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeCards.getTopCardListener().selectLeft();
            }
        });

        swipeCards = (SwipeFlingAdapterView) getActivity().findViewById(R.id.frame);
        swipeCards.removeAllViewsInLayout();

        cardList = new ArrayList<SwipeCard>();
        cardList.add(new SwipeCard(getString(R.string.thinker),"Thinker", R.drawable.thinker));
        cardList.add(new SwipeCard(getString(R.string.sporty),"Sporty", R.drawable.sporty));
        cardList.add(new SwipeCard(getString(R.string.socialite),"Social", R.drawable.socialite));
        cardList.add(new SwipeCard(getString(R.string.organized),"Organized", R.drawable.organized));
        cardList.add(new SwipeCard(getString(R.string.natureLover),"Nature Lover", R.drawable.nature_lover));
        cardList.add(new SwipeCard(getString(R.string.leader),"Leader", R.drawable.leader));
        cardList.add(new SwipeCard(getString(R.string.geek),"Geek", R.drawable.geek));
        cardList.add(new SwipeCard(getString(R.string.foodie),"Foodie", R.drawable.foodie));
        cardList.add(new SwipeCard(getString(R.string.explorer),"Explorer", R.drawable.explorer));
        cardList.add(new SwipeCard(getString(R.string.handy),"Handy",R.drawable.handy));
        cardList.add(new SwipeCard(getString(R.string.creative),"Creative", R.drawable.creativity));
        cardList.add(new SwipeCard(getString(R.string.caring),"Caring", R.drawable.caring));
        cardList.add(new SwipeCard(getString(R.string.bizwiz),"Biz Wiz", R.drawable.bizwiz));
        cardList.add(new SwipeCard(getString(R.string.animalLover),"Animal", R.drawable.animal_lover));

        arrayAdapter = new ArrayAdapter<SwipeCard>(getActivity(), R.layout.on_boarding_swipe_item, R.id.cardText, cardList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView cardText = (TextView) v.findViewById(R.id.cardText);
                cardText.setText(cardList.get(position).title);
                cardText.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

                ImageView cardImage = (ImageView) v.findViewById(R.id.cardImage);
                cardImage.setImageResource(cardList.get(position).img);
                return v;
            }

            @Override
            public int getCount() {
                return cardList.size() - 5;
            }
        };
        swipeCards.setAdapter(arrayAdapter);

        swipeCards.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                cardList.remove(0);
                cardList.size();
                removedCounter++;
                if (removedCounter >= 5) {
                    Intent homeScreen = new Intent(getActivity(), HomeScreen.class);
                    Log.v("preference",String.valueOf(preferedCategoryString.length));
                    homeScreen.putExtra("preferredCategories",preferedCategoryString);
                    getActivity().startActivity(homeScreen);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
            }

            @Override
            public void onRightCardExit(Object dataObject) {
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
//                 Ask for more data here
                //cardList.add("Sometimes it's the journey that teaches you a lot about your destination.");
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

class SwipeCard {
    String title,preferredCategories;
    int img;

    SwipeCard(String title, String preferredCategories, int img) {
        this.title = title;
        this.preferredCategories = preferredCategories;
        this.img = img;
    }

}
