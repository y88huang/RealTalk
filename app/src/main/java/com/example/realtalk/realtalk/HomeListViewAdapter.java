package com.example.realtalk.realtalk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alexgomes on 2015-08-15.
 */
public class HomeListViewAdapter extends BaseAdapter {

    private ArrayList<Card> cardView;
    private LayoutInflater inflater;
    private Context context;
    private SharedPreferences sharedPreferences;
    String userID,facebookId;
    String requestURL;

    public HomeListViewAdapter(Context c,  LayoutInflater layoutInflater,ArrayList<Card> item){
        inflater = layoutInflater;
        context = c;
        cardView = item;

        requestURL = context.getResources().getString(R.string.serverURL) + "api/user/addBookmarkToUser";

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

                viewHolder.bookMark.setTag(position);
                if (((ImageButton) v).getDrawable() == drawable1) {
                    ((ImageButton) v).setImageDrawable(drawable2);

                } else {
                    ((ImageButton) v).setImageDrawable(drawable1);
                }

                sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.tlpSharedPreference), Context.MODE_PRIVATE);
                userID = sharedPreferences.getString("userID", "");
                facebookId = sharedPreferences.getString("facebookId", "");
                Toast.makeText(context,facebookId, Toast.LENGTH_SHORT).show();
                if(userID.isEmpty() && facebookId.isEmpty()){
                    Intent intent = new Intent(context,Authentication.class);
                    context.startActivity(intent);
                }else{
                    final HashMap<String, String> params = new HashMap<>();
                    params.put("userId", userID);
                    params.put("talkId", cardView.get(position)._id);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    Log.v("response", response.toString());
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d("Error", "Error: " + error.getMessage());
                                }
                            }
                    );
                    VolleyApplication.getInstance().getRequestQueue().add(request);
                }
            }
        });
        viewHolder.title.setText(cardView.get(position).title);
        viewHolder.description.setText(cardView.get(position).description);
        viewHolder.bg.setImageUrl(cardView.get(position).bg, HomeScreen.imgLoader);

        Matrix matrix = viewHolder.bg.getImageMatrix();// imageView.getImageMatrix();
        matrix.preTranslate(0, -100);
        viewHolder.bg.setImageMatrix(matrix);

        for (int i = 0; i < cardView.get(position).categories.length; i++) {
            String category1 = cardView.get(position).categories[0].optString("title");
            String category2 = cardView.get(position).categories[1].optString("title");
            String category3 = cardView.get(position).categories[2].optString("title");
            viewHolder.category1.setText(category1);
            viewHolder.category2.setText(category2);
            viewHolder.category3.setText(category3);
        }

        return view;
    }
    static class ViewHolder{
        ImageButton bookMark;
        TextView title,description,category1,category2,category3;
        NetworkImageView bg;
    }
}

