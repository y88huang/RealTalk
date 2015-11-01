package com.example.realtalk.realtalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageButton;
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
import java.util.List;

/**
 * Created by alexgomes on 2015-10-28. - alex.09hg@gmail.com
 */
public class SearchFragment extends Fragment {

    String searchUrl;
    ImageButton backButton;
    AutoCompleteTextView searchBox;
    SearchAdapter searchAdapter;
    ArrayList<SearchObject> searchItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);

    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchUrl = getResources().getString(R.string.serverURL) + "api/talk/searchTalks";
        searchItem = new ArrayList<>();
        searchAdapter = new SearchAdapter(getActivity(), LayoutInflater.from(getActivity().getApplicationContext()));
        searchAdapter.SetList(searchItem);

        backButton = (ImageButton) getActivity().findViewById(R.id.seachBackButton);
        searchBox = (AutoCompleteTextView) getActivity().findViewById(R.id.autoCompleteSerch);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(getActivity().getApplicationContext(), searchBox.getWindowToken());
                getActivity().onBackPressed();
            }
        });

        searchBox.setAdapter(searchAdapter);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("searchText", s.toString());
                if (s.length() > 3) {
                    MakeSearchRequest(searchUrl, params);
                }
            }
        });
    }

    public void MakeSearchRequest(String url, HashMap<String, String> args) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(args),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("response", response.toString());
                        searchItem.clear();
                        JSONArray array = response.optJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject jsonObject = array.optJSONObject(i);
                            final String _id = jsonObject.optString("_id");
                            final String title = jsonObject.optString("title");
                            searchItem.add(new SearchObject(_id, title));
                        }
                        searchAdapter.notifyDataSetChanged();
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
    }

    private List<String> mObjects;

    class SearchAdapter extends ArrayAdapter<String> {

        LayoutInflater inflater;
        Context context;
        ArrayList<SearchObject> searchObjectsList;
        private Filter filter;


        public SearchAdapter(Context context, LayoutInflater layoutInflater) {
            super(context, 0);
            this.inflater = layoutInflater;
            mObjects = new ArrayList<>();
        }

        public void SetList(ArrayList<SearchObject> list) {
            searchObjectsList = list;
        }

        @Override
        public int getCount() {
            return mObjects.size();
        }

        @Override
        public String getItem(int position) {
            return mObjects.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.single_search_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.searchTitle);

            final String item = this.getItem(position);
            convertView.setTag(position);
            title.setText(item);

            final View finalConvertView = convertView;
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String talkId = searchItem.get((Integer) finalConvertView.getTag())._id;
                    Intent intent = new Intent(getActivity(), RealTalk.class);
                    intent.putExtra("talkID", talkId);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        @Override
        public Filter getFilter() {
            filter = new SearchFilter();
            return filter;
        }

    }

    class SearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults result = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                ArrayList<SearchObject> list = new ArrayList<>();
                result.values = list;
                result.count = list.size();
            } else {
                // iterate over the list of venues and find if the venue matches the constraint. if it does, add to the result list
                final ArrayList<String> retList = new ArrayList<>();

                for (int i = 0; i < searchItem.size(); i++) {
                    retList.add(searchItem.get(i).title);
                }
                result.values = retList;
                result.count = retList.size();
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // we clear the adapter and then pupulate it with the new results
            searchAdapter.clear();
            searchItem.clear();
            if (results.count > 0) {
                mObjects = (List<String>) results.values;

                for (String s : mObjects) {
                    searchAdapter.add(s);
                    Log.d("CustomArrayAdapter", String.valueOf(results.values));
                    Log.d("CustomArrayAdapter", String.valueOf(results.count));
                }
            }
            searchAdapter.notifyDataSetChanged();
        }
    }
}


class SearchObject {
    String _id, title;

    SearchObject(String id, String title) {
        this._id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}





