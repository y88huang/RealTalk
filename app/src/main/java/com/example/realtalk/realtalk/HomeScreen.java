package com.example.realtalk.realtalk;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.realtalk.realtalk.Utility.KillApplicationDialog;
import static com.example.realtalk.realtalk.Utility.hidePDialog;
import static com.example.realtalk.realtalk.Utility.isNetworkStatusAvailable;

public class HomeScreen extends AppCompatActivity {

    //    private Toolbar toolbar;
    LinearLayout homeList;
    ParallaxListView listView;
    RelativeLayout sub_actionbar, searchBar;
    ImageButton dropdown, logo, btnExplore, btnProfile;
    TextView mostLiked, mostBookedMarked, categoryName;
    EditText searchBox;
    HomeListViewAdapter adapter;
    public static ImageLoader imgLoader;
    private ArrayList<Card> item;
    private ProgressDialog progressDialog;

    String url, searchUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        url = getResources().getString(R.string.serverURL) + "api/talk/getAllTalks";
        searchUrl = getResources().getString(R.string.serverURL) + "api/talk/searchTalks";

        if (!isNetworkStatusAvailable(HomeScreen.this)) {
            KillApplicationDialog(getString(R.string.connectionError), HomeScreen.this);
        }

//delete this commented section if topbar doesnt cause any issue.
//        toolbar = (Toolbar) findViewById(R.id.custom_actionbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        homeList = (LinearLayout) findViewById(R.id.home_list);

        sub_actionbar = (RelativeLayout) findViewById(R.id.sub_actionbar);
        item = new ArrayList<>();

        progressDialog = new ProgressDialog(HomeScreen.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        logo = (ImageButton) findViewById(R.id.logo);
        dropdown = (ImageButton) findViewById(R.id.dropdown);
        dropdown.setScaleY(-1f);

        mostLiked = (TextView) findViewById(R.id.mostLikedText);
        mostLiked.setTypeface(FontManager.setFont(HomeScreen.this, FontManager.Font.OpenSansRegular));
        mostLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                url = getResources().getString(R.string.serverURL) + "api/talk/getTalksByMostLiked";
                MakeRequest(url, new HashMap<String, String>());
            }
        });

        mostBookedMarked = (TextView) findViewById(R.id.mostBookmarkText);
        mostBookedMarked.setTypeface(FontManager.setFont(getApplicationContext(), FontManager.Font.OpenSansRegular));
        mostBookedMarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                url = getResources().getString(R.string.serverURL) + "api/talk/getTalksByMostBookMarked";
                MakeRequest(url, new HashMap<String, String>());
            }
        });

        categoryName = (TextView) findViewById(R.id.categoryName);
        categoryName.setTypeface(FontManager.setFont(this, FontManager.Font.JustAnotherHandRegular));

        //topbar dropdown animation - hide/show sub toolbar
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator anim;
                if (dropdown.getScaleY() == -1f) {
                    dropdown.setScaleY(1f);
                    anim = ObjectAnimator.ofFloat(sub_actionbar, "translationY", 0.0f, (float) sub_actionbar.getMeasuredHeight());
                    anim.setDuration(300);
                    anim.setRepeatCount(0);
                    anim.start();
                } else {
                    dropdown.setScaleY(-1f);
                    anim = ObjectAnimator.ofFloat(sub_actionbar, "translationY", (float) sub_actionbar.getMeasuredHeight(), 0.0f);
                    anim.setDuration(300);
                    anim.setRepeatCount(0);
                    anim.start();
                }
            }
        });

        btnExplore = (ImageButton) findViewById(R.id.btnExplore);
        btnExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExploreFragment exploreFragment = new ExploreFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom, R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                fragmentTransaction.add(android.R.id.content, exploreFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        searchBar = (RelativeLayout) findViewById(R.id.search_bar);
        searchBox = (EditText) findViewById(R.id.searchBox);
        searchBox.setTypeface(FontManager.setFont(this, FontManager.Font.OpenSansRegular));
        searchBox.addTextChangedListener(new TextWatcher() {
            final long idleTime = 10; // 4 seconds after user stops typing
            long startTime;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                startTime = 0;
            }

            @Override
            public void afterTextChanged(final Editable s) {
                startTime = System.currentTimeMillis();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (startTime > idleTime) {
                            if(s.length() >3) {
                                final HashMap<String, String> params = new HashMap<String, String>();
                                params.put("searchText", s.toString());
                                Log.v("text", s.toString());
                                MakeSearchRequest(searchUrl, params);
                                startTime = 0;
                            }
                        }
                    }
                }, 0);
            }
        });

        //by default make the request with default url - getAllTalks
        MakeRequest(url, new HashMap<String, String>());

        listView.setOnDetectScrollListener(new OnDetectScrollListener() {
            Matrix imageMatrix;

            @Override
            public void onUpScrolling() {
                int first = listView.getFirstVisiblePosition();
                int last = listView.getLastVisiblePosition();
                for (int i = 0; i < (last - first); i++) {
                    NetworkImageView imageView = ((HomeListViewAdapter.ViewHolder) listView.getChildAt(i).getTag()).bg;
                    imageMatrix = imageView.getImageMatrix();
                    imageMatrix.postTranslate(0, -0.5f);
                    imageView.setImageMatrix(imageMatrix);
                    imageView.invalidate();
                }
            }

            @Override
            public void onDownScrolling() {
                int first = listView.getFirstVisiblePosition();
                int last = listView.getLastVisiblePosition();
                for (int i = 0; i < (last - first); i++) {
                    NetworkImageView imageView = ((HomeListViewAdapter.ViewHolder) listView.getChildAt(i).getTag()).bg;
                    imageMatrix = imageView.getImageMatrix();
                    imageMatrix.postTranslate(0, 0.5f);
                    imageView.setImageMatrix(imageMatrix);
                    imageView.invalidate();
                }
            }
        });
        homeList.addView(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Card card = (Card) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getBaseContext(), RealTalk.class);
                intent.putExtra("talkID", card._id);
                startActivity(intent);
            }
        });

        btnProfile = (ImageButton) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Profile.class);
                startActivity(intent);
            }
        });
    }

    public void MakeRequest(String url, HashMap<String, String> args) {
        //clear the item from adapter before making the request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(args),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        item.clear();

                        JSONArray array = response.optJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.optJSONObject(i);
                            String _id = jsonObject.optString("_id");
                            String title = jsonObject.optString("title");
                            String tagline = jsonObject.optString("tagline");
                            String imgUrl = jsonObject.optString("imageUrl");
                            String bookmark = jsonObject.optString("bookmarkCount");

                            int lengthOfCategories = jsonObject.optJSONArray("categories").length();
                            JSONObject[] jsonObjectArray = new JSONObject[lengthOfCategories];

                            for (int j = 0; j < jsonObject.optJSONArray("categories").length(); j++) {
                                jsonObjectArray[j] = jsonObject.optJSONArray("categories").optJSONObject(j);
                            }

                            Card card = new Card(_id, title, tagline, jsonObjectArray, imgUrl,bookmark);
                            item.add(card);

//                            Log.v("likes", jsonObject.optString("likesCount"));
//                            Log.v("bookmark", jsonObject.optString("bookmarkCount"));
                        }
                        adapter.notifyDataSetChanged();
                        hidePDialog(progressDialog, 800);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.v("Error", "Error: " + error.getMessage());
                        hidePDialog(progressDialog, 800);
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
        imgLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLru(6400));

        listView = new ParallaxListView(this);
        adapter = new HomeListViewAdapter(HomeScreen.this, LayoutInflater.from(this), item);
        listView.setAdapter(adapter);
        listView.requestLayout();
        adapter.notifyDataSetChanged();
    }

    //////////

    public void MakeSearchRequest(String url, HashMap<String, String> args) {
        //clear the item from adapter before making the request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(args),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        item.clear();

                        JSONArray array = response.optJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            final JSONObject jsonObject = array.optJSONObject(i);
                            final String _id = jsonObject.optString("_id");
                            final String title = jsonObject.optString("title");
                            final String tagline = jsonObject.optString("tagline");
                            final String imgUrl = jsonObject.optString("imageUrl");
                            final String bookmark = jsonObject.optString("bookmarkCount");

                            int lengthOfCategories = jsonObject.optJSONArray("categories").length();
                            final JSONObject[] jsonObjectArray = new JSONObject[lengthOfCategories];

                            for (int j = 0; j < jsonObject.optJSONArray("categories").length(); j++) {
                                jsonObjectArray[j] = jsonObject.optJSONArray("categories").optJSONObject(j);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Card card = new Card(_id, title, tagline, jsonObjectArray, imgUrl,bookmark);
                                    item.add(card);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.v("Error", "Error: " + error.getMessage());
                        hidePDialog(progressDialog, 800);
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
        imgLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLru(6400));

        listView = new ParallaxListView(this);
        HomeListViewAdapter searchAdapter = new HomeListViewAdapter(this, LayoutInflater.from(this), item);
        listView.setAdapter(searchAdapter);
        listView.requestLayout();
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog(progressDialog, 800);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!isNetworkStatusAvailable(HomeScreen.this)) {
            KillApplicationDialog(getString(R.string.connectionError), HomeScreen.this);
        }
    }

    public void SetToolBarTitle(String title) {
        if (title.isEmpty() || title == null || title == "") {
            logo.setVisibility(View.VISIBLE);
        }
        if (!title.isEmpty() || title != null || title != "") {
            categoryName.setText(title);
            categoryName.setVisibility(View.VISIBLE);
            logo.setVisibility(View.GONE);
        }
    }

}

class Card {
    public String _id, title, tagline, bg,bookmark;
    public JSONObject[] categories;

    public Card(String id, String title, String tagline, JSONObject[] cats, String bg,String bookmark) {
        this._id = id;
        this.title = title;
        this.tagline = tagline;
        this.categories = cats;
        this.bg = bg;
        this.bookmark = bookmark;
    }
}

