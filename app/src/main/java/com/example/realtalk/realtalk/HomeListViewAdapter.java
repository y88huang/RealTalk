package com.example.realtalk.realtalk;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.util.ArrayList;
/**
 * Created by alexgomes on 2015-08-15.
 */
public class HomeListViewAdapter extends BaseAdapter {

    private ArrayList<Card> cardView;
    private LayoutInflater inflater;
    private Context context;

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
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolder viewHolder;

        if(view == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.home_list_single_row, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.description= (TextView) view.findViewById(R.id.description);
            viewHolder.bookMark = (ImageButton) view.findViewById(R.id.bookmark);
            viewHolder.bg = (NetworkImageView) view.findViewById(R.id.bg);
            viewHolder.category1 = (TextView) view.findViewById(R.id.category1);
            viewHolder.category2 = (TextView) view.findViewById(R.id.category2);
            viewHolder.category3 = (TextView) view.findViewById(R.id.category3);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
            view.setTag(viewHolder);
        }

        viewHolder.title.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.MontSerratRegular));
        viewHolder.description.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));

        viewHolder.category1.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));
        viewHolder.category2.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));
        viewHolder.category3.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));

        final Drawable drawable1 = ContextCompat.getDrawable(context, R.drawable.iconbookmarked);
        final Drawable drawable2 = ContextCompat.getDrawable(context, R.drawable.iconbookmarked_filled);

        viewHolder.bookMark.setImageDrawable(drawable1);
        viewHolder.bookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Text " + position, Toast.LENGTH_SHORT).show();
                viewHolder.bookMark.setTag(position);
                if (((ImageButton) v).getDrawable() == drawable1) {
                    ((ImageButton) v).setImageDrawable(drawable2);
                } else {
                    ((ImageButton) v).setImageDrawable(drawable1);
                }
            }
        });
        viewHolder.title.setText(cardView.get(position).title);
        viewHolder.description.setText(cardView.get(position).description);
        viewHolder.bg.setImageUrl(cardView.get(position).bg, HomeScreen.imgLoader);

        Log.v("categoryList Count", String.valueOf(cardView.get(position).categories));

        for (JSONObject[] test: cardView.get(position).categories) {
            for (JSONObject single: test) {
//                Log.v("jsonObject", String.valueOf(single));
            }
        }

        return view;
    }
}
class ViewHolder{
    ImageButton bookMark;
    TextView title,description,category1,category2,category3;
    NetworkImageView bg;
}
