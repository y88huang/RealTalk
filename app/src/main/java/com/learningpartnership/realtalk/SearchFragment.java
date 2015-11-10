package com.learningpartnership.realtalk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
 * Created by alexgomes on 2015-10-28. - alex.09hg@gmail.com
 */
public class SearchFragment extends Fragment {

    String searchUrl;
    ImageButton backButton;
    EditText searchBox;
    SearchAdapter searchAdapter;
    ArrayList<SearchObject> searchItem;
    ListView listView;
    LinearLayout noSearchLayout;

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
        listView = (ListView) getActivity().findViewById(R.id.searchedItem);

        noSearchLayout = (LinearLayout) getActivity().findViewById(R.id.noSearchLayout);

        searchItem = new ArrayList<>();
        searchAdapter = new SearchAdapter(getActivity(), LayoutInflater.from(getActivity().getApplicationContext()));
        searchAdapter.SetList(searchItem);
        listView.setAdapter(searchAdapter);

        backButton = (ImageButton) getActivity().findViewById(R.id.seachBackButton);
        searchBox = (EditText) getActivity().findViewById(R.id.autoCompleteSerch);
        searchBox.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchBox, InputMethodManager.SHOW_FORCED);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.clearFocus();
                searchBox.setFocusable(false);
                searchBox.setFocusableInTouchMode(false);
                getActivity().getSupportFragmentManager().popBackStack();
                closeKeyboard(getActivity(), searchBox.getWindowToken());
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

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
                HashMap<String, String> params = new HashMap<>();
                params.put("searchText", s.toString());
                if (s.length() > 3) {
                    MakeSearchRequest(searchUrl, params);
                }
            }
        });

        searchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    searchItem.clear();
                    searchAdapter.notifyDataSetChanged();
                    searchAdapter.clear();
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeKeyboard(getActivity(), searchBox.getWindowToken());

                String talkId = searchItem.get(position)._id;
                Intent intent = new Intent(getActivity(), RealTalk.class);
                intent.putExtra("talkID", talkId);
                startActivity(intent);
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
                        if (searchItem.size() == 0) {
                            noSearchLayout.setVisibility(View.VISIBLE);
                        } else {
                            noSearchLayout.setVisibility(View.INVISIBLE);
                        }
                        searchAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.v("Error", "Error: " + error.getMessage());
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

    class SearchAdapter extends ArrayAdapter<SearchObject> {

        LayoutInflater inflater;
        Context context;
        ArrayList<SearchObject> searchObjectsList;

        public SearchAdapter(Context context, LayoutInflater layoutInflater) {
            super(context, 0);
            this.inflater = layoutInflater;
        }

        public void SetList(ArrayList<SearchObject> list) {
            searchObjectsList = list;
        }

        @Override
        public int getCount() {
            return searchObjectsList.size();
        }

        @Override
        public SearchObject getItem(int position) {
            return searchObjectsList.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.single_search_item, parent, false);
            }

            TextView title = (TextView) convertView.findViewById(R.id.searchTitle);
            title.setText(searchObjectsList.get(position).title);

            return convertView;
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





