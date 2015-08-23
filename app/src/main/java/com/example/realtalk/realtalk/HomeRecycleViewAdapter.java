package com.example.realtalk.realtalk;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        private ImageButton share,bookmark;

        public ViewHolder(final View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            title.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.MontSerratRegular));

            bg = (ImageView) view.findViewById(R.id.bg);

            readMore =(TextView) view.findViewById(R.id.readMore);
            readMore.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));
            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                   alertView("Custom alert view - Test");
                }
            });

            bookmark = (ImageButton)view.findViewById(R.id.bookmark);
            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookmark.setImageResource(R.drawable.iconbookmarked_filled);
                }
            });
        }
        public void alertView(String message) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(itemView.getContext());

            dialog.setTitle("READ MORE")
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(message)
                      .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialoginterface, int i) {
                              dialoginterface.cancel();
                              }})
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    }).show();
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
