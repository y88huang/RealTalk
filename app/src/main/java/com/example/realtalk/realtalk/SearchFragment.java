package com.example.realtalk.realtalk;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by alexgomes on 2015-10-28. - alex.09hg@gmail.com
 */
public class SearchFragment extends Fragment {

    String searchUrl;
    ImageButton backButton;
    ArrayAdapter<JSONObject> searchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchUrl = getResources().getString(R.string.serverURL) + "api/talk/searchTalks";

        backButton = (ImageButton) getActivity().findViewById(R.id.seachBackButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });



//        searchAdapter = new ArrayAdapter<JSONObject>(this, android.R.id.text1) {
//            private Filter filter;
//
//            @Override
//            public View getView(final int position, View convertView, ViewGroup parent) {
//                if (convertView == null) {
//                    convertView = this.getLayoutInflater().inflate(R.layout.search_item, parent, false);
//                }
//
//                TextView venueName = (TextView) convertView
//                        .findViewById(R.id.search_item_venue_name);
//                TextView venueAddress = (TextView) convertView
//                        .findViewById(R.id.search_item_venue_address);
//
//                final JSONObject venue = this.getItem(position);
//                convertView.setTag(venue);
//                try {
//
//                    CharSequence name = highlightText(venue.getString("name"));
//                    CharSequence address = highlightText(venue.getString("address"));
//
//                    venueName.setText(name);
//                    venueAddress.setText(address);
//                } catch (JSONException e) {
//                    Log.i(Consts.TAG, e.getMessage());
//                }
//
//                return convertView;
//
//            }
//
//            @Override
//            public Filter getFilter() {
//                if (filter == null) {
//                    filter = new VenueFilter();
//                }
//                return filter;
//            }
//        };
//
//        class VenueFilter extends Filter {
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                List<JSONObject> list = new ArrayList<JSONObject>();
//                FilterResults result = new FilterResults();
//                String substr = constraint.toString().toLowerCase();
//                // if no constraint is given, return the whole list
//                if (substr == null || substr.length() == 0) {
//                    result.values = list;
//                    result.count = list.size();
//                } else {
//                    // iterate over the list of venues and find if the venue matches the constraint. if it does, add to the result list
//                    final ArrayList<JSONObject> retList = new ArrayList<JSONObject>();
//                    for (JSONObject venue : list) {
//                        try {
//                            if (
//                            venue.getString("name").toLowerCase().contains(constraint) || venue.getString("address").toLowerCase().contains(constraint) ||
//                                    {
//                                            retList.add(venue);
//                            }
//                        } catch (JSONException e) {
//                            Log.i(Consts.TAG, e.getMessage());
//                        }
//                    }
//                    result.values = retList;
//                    result.count = retList.size();
//                }
//                return result;
//            }
//
//            @SuppressWarnings("unchecked")
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                // we clear the adapter and then pupulate it with the new results
//                searchAdapter.clear();
//                if (results.count > 0) {
//                    for (JSONObject o : (ArrayList<JSONObject>) results.values) {
//                        searchAdapter.add(o);
//                    }
//                }
//            }
//        }

    }


    public void MakeSearchRequest(String url, HashMap<String, String> args) {
        //clear the item from adapter before making the request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(args),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("response", response.toString());
                        JSONArray array = response.optJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject jsonObject = array.optJSONObject(i);
                            final String _id = jsonObject.optString("_id");
                            final String title = jsonObject.optString("title");
                            final String tagline = jsonObject.optString("tagline");
                            final String imgUrl = jsonObject.optString("imageUrl");
                            final String bookmark = jsonObject.optString("bookmarkCount");

                            int lengthOfCategories
                                    = jsonObject.optJSONArray("categories").length();
                            final JSONObject[] jsonObjectArray = new JSONObject[lengthOfCategories];

                            for (int j = 0; j < jsonObject.optJSONArray("categories").length(); j++) {
                                jsonObjectArray[j] = jsonObject.optJSONArray("categories").optJSONObject(j);
                            }

//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Card card = new Card(_id, title, tagline, jsonObjectArray, imgUrl, bookmark);
//                                    item.add(card);
//                                    adapter.notifyDataSetChanged();
//                                    if(searchAdapter !=null){
//                                        searchAdapter.notifyDataSetChanged();
//                                    }
//                                }
//                            });
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.v("Error", "Error: " + error.getMessage());
//                        hidePDialog(progressDialog, 800);
                    }
                }
        );
        System.setProperty("http.keepAlive", "true");
        request.setRetryPolicy(new DefaultRetryPolicy(
                VolleyApplication.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        VolleyApplication.getInstance().getRequestQueue().add(request);
//        imgLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLru(6400));
//
//        listView = new ParallaxListView(this);
//        HomeListViewAdapter searchAdapter = new HomeListViewAdapter(this, LayoutInflater.from(this), item);
//        listView.setAdapter(searchAdapter);
//        listView.requestLayout();
//        adapter.notifyDataSetChanged();
//        searchAdapter.notifyDataSetChanged();
    }


}





