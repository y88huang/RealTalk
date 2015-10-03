package com.example.realtalk.realtalk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by alexgomes on 2015-09-29.
 */
public class ProfileBookMarksFragment extends Fragment {

    TextView yourBookMarksGoHere, readLater;
    ImageView bookMarkView;
    String requestURL;
    ArrayList<Bookmark> listOfBookMark;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_bookmark, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        yourBookMarksGoHere = (TextView) getActivity().findViewById(R.id.yourBookMarksHere);
        yourBookMarksGoHere.setText(getString(R.string.yourBookMarksGoHere));
        yourBookMarksGoHere.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        bookMarkView = (ImageView) getActivity().findViewById(R.id.imageView);

        readLater = (TextView) getActivity().findViewById(R.id.readLater);
        readLater.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/getBookMarks";

        //parameter being sent with body
        final HashMap<String, String> params = new HashMap<>();
        params.put("userId", "55fc61ef0c5c7903008d92b7");

        listOfBookMark = new ArrayList<>();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray data = response.optJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject bookMark = data.optJSONObject(i);
                            String id = bookMark.optString("_id");
                            String title = bookMark.optString("title");
                            String description = bookMark.optString("description");
                            String date = String.valueOf(new Date());

                            listOfBookMark.add(new Bookmark(id, title, description, date));

                        }
                        if (listOfBookMark.size() > 0) {
                            ShowBookmark(listOfBookMark);
                        }else{
                            yourBookMarksGoHere.setVisibility(View.VISIBLE);
                            bookMarkView.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "Error: " + error.getMessage());
                    }
                }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(
                VolleyApplication.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    public void ShowBookmark(final ArrayList<Bookmark> bookMarkList) {
        final LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.listOfBookmark);
        TextView bookMarkTitle, description, date;
        LayoutInflater layoutInflater =
                (LayoutInflater) getActivity().getApplicationContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < bookMarkList.size(); i++) {
            View view = layoutInflater.inflate(R.layout.profile_single_bookmark, linearLayout, false);

            bookMarkTitle = (TextView) view.findViewById(R.id.bookmarkTitle);
            bookMarkTitle.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));
            bookMarkTitle.setText(bookMarkList.get(i).title);

            description = (TextView) view.findViewById(R.id.bookmarkDescription);
            description.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));
            description.setText(bookMarkList.get(i).description);

            date = (TextView) view.findViewById(R.id.bookMarkSaved);
            date.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));
            date.setText(bookMarkList.get(i).date);

            view.setTag(i);

            linearLayout.removeView(view);
            linearLayout.addView(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String description = bookMarkList.get((Integer) v.getTag()).description;
                    Log.v("description", description);
                }
            });
        }


    }
}

class Bookmark {

    String id, title, description, date;

    public Bookmark(String id, String title, String description, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }
}
