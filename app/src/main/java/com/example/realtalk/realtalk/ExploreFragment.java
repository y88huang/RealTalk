package com.example.realtalk.realtalk;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import static com.example.realtalk.realtalk.Utility.isNetworkStatusAvailable;


public class ExploreFragment extends android.support.v4.app.Fragment {

    TextView titleExplore;
    ImageButton btnClose;
    GridView exploreGridView;

    public static String[] exploreItemText = {
            "CARING",
            "SOCIAL",
            "GEEK",
            "HANDY",
            "THINKER",
            "CREATIVE",
            "CHANGE MAKER",
            "NATURE LOVER",
            "SPORTY",
            "BIZ WIZ",
            "LEADER",
            "ANIMAL LOVER",
            "ORGANIZED",
            "FOODIE",
            "EXPLORER",
    };

    public static String[] exploreBackEndMatchingCategory = {
            "Caring",
            "Social",
            "Geek",
            "Handy",
            "Thinker",
            "Creative",
            "Changemaker",
            "Nature Lover",
            "Sporty",
            "Biz Wiz",
            "Leader",
            "Animal",
            "Organized",
            "Foodie",
            "Explorer",
    };

    public static int[] exploreItemIcon = {
            R.drawable.iconcreative,
            R.drawable.iconsocial,
            R.drawable.icongeek,
            R.drawable.iconhandy,
            R.drawable.iconthinker,
            R.drawable.iconcreative,
            R.drawable.iconchangemaker,
            R.drawable.iconnature,
            R.drawable.iconsporty,
            R.drawable.iconmoney,
            R.drawable.iconleader,
            R.drawable.iconanimal,
            R.drawable.iconorganized,
            R.drawable.iconfoodie,
            R.drawable.iconexplorer,
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

        if (!isNetworkStatusAvailable(getActivity())) {
            Utility.KillApplicationDialog(getString(R.string.connectionError), getActivity());
        }

        titleExplore = (TextView) getActivity().findViewById(R.id.titleExplore);
        titleExplore.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        btnClose = (ImageButton) getActivity().findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        exploreGridView = (GridView) getActivity().findViewById(R.id.exploreGridView);
        exploreGridView.setAdapter(new GridAdapter(getActivity(), exploreItemText, exploreItemIcon, exploreBackEndMatchingCategory));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isNetworkStatusAvailable(getActivity())) {
            Utility.KillApplicationDialog(getString(R.string.connectionError), getActivity());
        }
    }
}

class GridAdapter extends BaseAdapter {

    private Context context;
    private String[] itemText;
    private String[] exploreBackEndMatchingCategory;
    private int[] image;
    private static LayoutInflater inflater = null;

    public GridAdapter(Context context, String[] text, int[] image, String[] exploreBackEndMatchingCategory) {
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
                params.put("offset", "0");
                params.put("limit", "15");

                String categoryUrl = context.getResources().getString(R.string.serverURL) + "api/talk/getAllTalksByCategory";

                ((HomeScreen) context).MakeRequest(categoryUrl, params);

                HomeScreen.progressDialog.show();

                CloseFragment((HomeScreen) context);

            }
        });

        return convertView;
    }

    public void CloseFragment(HomeScreen context) {
        context.getSupportFragmentManager().popBackStack();
    }

    public class Holder {
        TextView itemName;
        ImageView icon;
    }
}
