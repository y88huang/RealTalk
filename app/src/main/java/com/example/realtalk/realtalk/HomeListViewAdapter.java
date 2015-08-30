package com.example.realtalk.realtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

/**
 * Created by alexgomes on 2015-08-15.
 */
public class HomeListViewAdapter extends BaseAdapter {

    private ArrayList<Card> cardView;
    private LayoutInflater inflater;
    private TextView title,readMore;
    private Context context;
    private NetworkImageView bg;
    private ImageButton share,bookmark;

    public HomeListViewAdapter(Context c,  LayoutInflater layoutInflater, ArrayList<Card> item){
        inflater = layoutInflater;
        context = c;
        cardView = item;
    }

    @Override
    public int getCount() {
        return cardView.size();
    }

    @Override
    public Object getItem(int position) {
        return cardView.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_single_row, parent, false);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.home_list_single_row, parent, false);

        title = (TextView) view.findViewById(R.id.title);
        title.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.MontSerratRegular));

        bg = (NetworkImageView) view.findViewById(R.id.bg);

        readMore = (TextView) view.findViewById(R.id.readMore);
        readMore.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));

        bookmark = (ImageButton) view.findViewById(R.id.bookmark);
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookmark.setImageResource(R.drawable.iconbookmarked_filled);
            }
        });

        title.setText(cardView.get(position).title);
        readMore.setText(cardView.get(position).readMore);
        bg.setImageUrl(cardView.get(position).bg, HomeScreen.imgLoader);


        return view;
    }
}
