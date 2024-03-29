package com.learningpartnership.realtalk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alexgomes on 2015-09-29.
 */
public class ProfileBookMarksFragment extends Fragment {

    TextView yourBookMarksGoHere, readLater;
    ImageView bookMarkView;
    String requestURL;
    ArrayList<Bookmark> listOfBookMark;
    SharedPreferences sharedPreferences;
    LinearLayout linearLayout, topView, bottomView;
    LayoutInflater layoutInflater;
    String userID, prefFile;
    int counter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_bookmark, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prefFile = getResources().getString(R.string.tlpSharedPreference);
        sharedPreferences = getActivity().getSharedPreferences(prefFile, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        linearLayout = (LinearLayout) getActivity().findViewById(R.id.listOfBookmark);

        yourBookMarksGoHere = (TextView) getActivity().findViewById(R.id.yourBookMarksHere);
        yourBookMarksGoHere.setText(getString(R.string.yourBookMarksGoHere));
        yourBookMarksGoHere.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        bookMarkView = (ImageView) getActivity().findViewById(R.id.imageView);

        readLater = (TextView) getActivity().findViewById(R.id.readLater);
        readLater.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/getBookMarks";

        if (userID == null || userID.isEmpty()) {
            bookMarkView.setVisibility(View.VISIBLE);
            yourBookMarksGoHere.setVisibility(View.VISIBLE);
        }

        MakeRequest(requestURL);
    }

    @Override
    public void onResume() {
        super.onResume();
        linearLayout.removeAllViews();
        linearLayout.invalidate();
        MakeRequest(requestURL);

    }

    public void ShowBookmark(final ArrayList<Bookmark> bookMarkList) {
        TextView bookMarkTitle, description, date;

        linearLayout.removeAllViews();

        layoutInflater = (LayoutInflater) getActivity().getApplicationContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < bookMarkList.size(); i++) {

            final View view = layoutInflater.inflate(R.layout.profile_single_bookmark, linearLayout, false);

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

            linearLayout.addView(view);

            topView = (LinearLayout) view.findViewById(R.id.top_view);
            topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String talkId = bookMarkList.get((Integer) view.getTag()).id;
                    Intent intent = new Intent(getActivity(), RealTalk.class);
                    intent.putExtra("talkID", talkId);
                    startActivity(intent);
                }
            });

            counter = linearLayout.getChildCount();

            bottomView = (LinearLayout) view.findViewById(R.id.bottom_view);
            bottomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage(R.string.deleteBookmark);

                    alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RemoveBookmark(view, bookMarkList);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialogBuilder.show();
                }
            });

        }
    }

    public void MakeRequest(String requestURL) {
        //parameter being sent with body
        final HashMap<String, String> params = new HashMap<>();
        params.put("userId", userID);

        listOfBookMark = new ArrayList<>();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listOfBookMark.clear();
                        JSONArray data = response.optJSONArray("data");
                        if (data != null) {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject bookMark = data.optJSONObject(i);
                                String id = bookMark.optString("_id");
                                String title = bookMark.optString("title");
                                String description = bookMark.optString("description");
                                String date = GetDate(bookMark.optString("createdAt"));

                                listOfBookMark.add(new Bookmark(id, title, description, date));

                            }
                            if (listOfBookMark.size() > 0) {
                                ShowBookmark(listOfBookMark);
                            } else {
                                yourBookMarksGoHere.setVisibility(View.VISIBLE);
                                bookMarkView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "Error: " + error.getMessage());
                        Log.v("error", error.toString());
                    }
                }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    public void RemoveBookmark(final View view, ArrayList<Bookmark> bookMarkList) {

        requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/removeBookmarkFromUser";
        String talkId = bookMarkList.get((Integer) view.getTag()).id;
        counter = counter - 1;

        if (counter == 0) {
            yourBookMarksGoHere.setVisibility(View.VISIBLE);
            bookMarkView.setVisibility(View.VISIBLE);
        }
        final HashMap<String, String> params = new HashMap<>();
        params.put("userId", userID);
        params.put("talkId", talkId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        view.setVisibility(View.GONE);
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

    public String GetDate(String date) {
        String[] parts = date.split("T");
        String part1 = parts[0];
        return part1;
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
