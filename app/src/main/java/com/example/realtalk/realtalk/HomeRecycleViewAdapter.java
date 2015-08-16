package com.example.realtalk.realtalk;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alexgomes on 2015-08-15.
 */
public class HomeRecycleViewAdapter extends RecyclerView.Adapter<HomeRecycleViewAdapter.ViewHolder> {

    private ArrayList<Card> cardView;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title,readMore;
        private ImageView bg;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);

            Typeface myTypeface = Typeface.createFromAsset(view.getResources().getAssets(), "Montserrat-Regular.ttf");
            title.setTypeface(myTypeface);


            readMore =(TextView) view.findViewById(R.id.readMore);
            bg = (ImageView) view.findViewById(R.id.bg);
        }
    }

    public HomeRecycleViewAdapter(ArrayList<Card> item){
        cardView = item;
    }

    @Override
    public HomeRecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_list_single_row,viewGroup,false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HomeRecycleViewAdapter.ViewHolder viewHolder, int position) {
        viewHolder.title.setText(cardView.get(position).title);
        viewHolder.readMore.setText(cardView.get(position).readMore);
        viewHolder.bg.setImageResource(cardView.get(position).bg);
    }

    @Override
    public int getItemCount() {
        return cardView.size();
    }
}
