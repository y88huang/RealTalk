package com.example.realtalk.realtalk;


import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class ExploreFragment extends android.support.v4.app.Fragment {

    TextView titleExplore;
    ImageButton btnClose;
    GridView exploreGridView;

    public static String[] exploreItemText = {
            "CREATIVE",
            "NATURE LOVER",
            "CARING",
            "INVESTOR",
            "GEEK",
            "FUTURIST",
            "ANIMAL LOVER",
            "THINKER",
            "CHANGE MAKER",
            "SPORTY",
            "EXPLORER",
            "ORGANIZED",
            "LEADER",
            "CONFUSED",
            "HANDY",
            "FOODIE",
            "SOCIALITE",
    };

    public static String[] exploreBackEndMatchingCategory = {
            "CREATIVE",
            "NATURE LOVER",
            "CARING",
            "MONEY EXPERT",
            "GEEK",
            "Jobs of the Future",
            "ANIMAL LOVER",
            "THINKER",
            "Changemaker",
            "SPORTY",
            "EXPLORER",
            "ORGANIZED",
            "LEADER",
            "CONFUSED",
            "HANDY",
            "FOODIE",
            "Social",
    };

    public static int[] exploreItemIcon = {
            R.drawable.iconcreative,
            R.drawable.iconnature,
            R.drawable.iconcaring,
            R.drawable.iconmoney,
            R.drawable.icongeek,
            R.drawable.iconfuturist,
            R.drawable.iconanimal,
            R.drawable.iconthinker,
            R.drawable.iconchangemaker,
            R.drawable.iconsporty,
            R.drawable.iconexplorer,
            R.drawable.iconorganized,
            R.drawable.iconleader,
            R.drawable.iconconfused,
            R.drawable.iconhandy,
            R.drawable.iconfoodie,
            R.drawable.iconsocial,
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleExplore = (TextView) getActivity().findViewById(R.id.titleExplore);
        titleExplore.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        btnClose = (ImageButton) getActivity().findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                getActivity().getSupportFragmentManager().beginTransaction().remove(ExploreFragment.this).commit();

            }
        });

        exploreGridView = (GridView) getActivity().findViewById(R.id.exploreGridView);
        exploreGridView.setAdapter(new GridAdapter(getActivity(), exploreItemText, exploreItemIcon ,exploreBackEndMatchingCategory));


    }
}

class GridAdapter extends BaseAdapter {

    private Context context;
    private String[] itemText;
    private String[] exploreBackEndMatchingCategory;
    private int[] image;
    private static LayoutInflater inflater = null;

    public GridAdapter(Context context, String[] text, int[] image,String[] exploreBackEndMatchingCategory) {
        this.context = context;
        this.itemText = text;
        this.image = image;
        this.exploreBackEndMatchingCategory = exploreBackEndMatchingCategory;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemText.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        convertView = inflater.inflate(R.layout.explore_single_grid_item, null);

        holder.icon = (ImageView) convertView.findViewById(R.id.exploreIcon);
        holder.icon.setImageResource(image[position]);

        holder.itemName = (TextView) convertView.findViewById(R.id.exploreItemText);
        holder.itemName.setTypeface(FontManager.setFont(context, FontManager.Font.JustAnotherHandRegular));
        holder.itemName.setText(itemText[position]);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((HomeScreen) context).SetToolBarTitle(itemText[position]);

                HashMap<String, String> params = new HashMap<>();
                params.put("category", exploreBackEndMatchingCategory[position]);

                String categoryUrl = context.getResources().getString(R.string.serverURL) + "api/talk/getAllTalksByCategory";

                ((HomeScreen)context).MakeRequest(categoryUrl,params);

                CloseFragment((HomeScreen) context);

            }
        });

        return convertView;
    }
    public void CloseFragment(HomeScreen context){
        context.getSupportFragmentManager().popBackStack();

    }

    public class Holder {
        TextView itemName;
        ImageView icon;
    }
}