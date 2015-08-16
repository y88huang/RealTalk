package com.example.realtalk.realtalk;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by alexgomes on 2015-08-15.
 */
public class HomeRecycleViewAdapter extends RecyclerView.Adapter<HomeRecycleViewAdapter.ViewHolder> {

    private String[] cardView;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
        }
    }

    public HomeRecycleViewAdapter(String[] item){
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
        viewHolder.title.setText(cardView[position]);
    }

    @Override
    public int getItemCount() {
        return cardView.length;
    }
}
