package com.learningpartnership.realtalk;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.learningpartnership.realtalk.Utility.KillApplicationDialog;
import static com.learningpartnership.realtalk.Utility.hidePDialog;
import static com.learningpartnership.realtalk.Utility.isNetworkStatusAvailable;

public class HomeScreen extends AppCompatActivity {

    //    private Toolbar toolbar;
    LinearLayout homeList;
    ParallaxListView listView;
    RelativeLayout sub_actionbar, search_bar;
    ImageButton dropdown, logo, btnExplore, btnProfile, btnCloseExplore;
    TextView mostLiked, mostBookedMarked, categoryName;
    EditText searchBox;
    HomeListViewAdapter adapter;
    public static ImageLoader imgLoader;
    public ArrayList<Card> item;
    public static ProgressDialog progressDialog;
    private Tracker mTracker;
    SharedPreferences sharedPreferences;

    String url, userId, prefFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        if (!isNetworkStatusAvailable(HomeScreen.this)) {
            KillApplicationDialog(getString(R.string.connectionError), HomeScreen.this);
        }

        VolleyApplication analytics = (VolleyApplication) getApplication();
        mTracker = analytics.getDefaultTracker();
        mTracker.setScreenName("Home");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        prefFile = getResources().getString(R.string.tlpSharedPreference);
        sharedPreferences = this.getApplicationContext().getSharedPreferences(prefFile, Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userID", "");

        url = getResources().getString(R.string.serverURL) + "api/talk/getAllTalks";
        adapter = new HomeListViewAdapter(HomeScreen.this, LayoutInflater.from(this));
        listView = new ParallaxListView(this);
        listView.setDivider(null);

        homeList = (LinearLayout) findViewById(R.id.home_list);

        sub_actionbar = (RelativeLayout) findViewById(R.id.sub_actionbar);
        search_bar = (RelativeLayout) findViewById(R.id.search_bar);

        item = new ArrayList<>();
        adapter.SetList(item);
        listView.setAdapter(adapter);

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
                item.clear();
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
                item.clear();
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
                    search_bar.setVisibility(View.INVISIBLE);
                    anim = ObjectAnimator.ofFloat(sub_actionbar, "translationY", 0.0f, (float) sub_actionbar.getMeasuredHeight());
                    anim.setDuration(300);
                    anim.setRepeatCount(0);
                    anim.start();
                } else {
                    dropdown.setScaleY(-1f);
                    search_bar.setClickable(true);
                    search_bar.setVisibility(View.VISIBLE);
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

        searchBox = (EditText) findViewById(R.id.searchBox);
        searchBox.setTypeface(FontManager.setFont(this, FontManager.Font.OpenSansRegular));
        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment searchFragment = new SearchFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(android.R.id.content, searchFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        //by default make the request with default url - getAllTalks
//        if (this.getIntent().getStringArrayExtra("preferredCategories") != null) {
//            String[] preferredCategories = this.getIntent().getStringArrayExtra("preferredCategories");
////            String concat = "["+'"'+
////                    preferredCategories[0]+'"'+','+'"'+
////                    preferredCategories[1]+'"'+','+'"'+
////                    preferredCategories[2]+'"'+','+'"'+
////                    preferredCategories[3]+'"'+','+'"'+
////                    preferredCategories[4]+'"'+"]";
//            JSONObject jsonObject = new JSONObject();
//            for (int i = 0; i < preferredCategories.length; i++) {
//                jsonObject.put("params_"+i,);
//            }
//
//            HashMap<String, String> params = new HashMap<>();
//            params.put("offset", "0");
//            params.put("limit","15");
//                params.put("preferredCategories", jsonObject.toString());
//            MakePreferedRequest(url, params);
//        }
        if (userId == null || userId.isEmpty()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("offset", "0");
            params.put("limit", "15");
            MakeRequest(url, params);
        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("userId", userId);
            params.put("offset", "0");
            params.put("limit", "15");
            MakeRequest(url, params);
        }

        listView.setOnDetectScrollListener(new OnDetectScrollListener() {
            Matrix imageMatrix;

            @Override
            public void onUpScrolling() {
                int first = listView.getFirstVisiblePosition();
                int last = listView.getLastVisiblePosition();
                for (int i = 0; i < (last - first); i++) {
                    ImageView imageView = ((HomeListViewAdapter.ViewHolder) listView.getChildAt(i).getTag()).bg;
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
                    ImageView imageView = ((HomeListViewAdapter.ViewHolder) listView.getChildAt(i).getTag()).bg;
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

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int bufferItemCount = 3;
            private int itemCount = 0;
            private boolean isLoading = true;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount < itemCount) {
                    this.itemCount = totalItemCount;
                    if (totalItemCount == 0) {
                        this.isLoading = true;
                    }
                }

                if (isLoading && (totalItemCount > itemCount)) {
                    isLoading = false;
                    itemCount = totalItemCount;
                }
                if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + bufferItemCount) && String.valueOf(categoryName.getVisibility()) != "0") {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("offset", String.valueOf(listView.getCount()));
                    params.put("limit", "15");
                    MakeRequest(url, params);
                    isLoading = true;
                }
            }
        });

        btnProfile = (ImageButton) findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = sharedPreferences.getString("userID", "");
                if (userId.isEmpty()) {
                    Intent authentiation = new Intent(HomeScreen.this, Authentication.class);
                    HomeScreen.this.startActivity(authentiation);
                } else {
                    Intent profile = new Intent(getBaseContext(), Profile.class);
                    startActivity(profile);
                }
            }
        });

        btnCloseExplore = (ImageButton) findViewById(R.id.btnCloseExplore);
        btnCloseExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCloseExplore.setVisibility(View.GONE);
                categoryName.setVisibility(View.GONE);
                logo.setVisibility(View.VISIBLE);
                btnExplore.setVisibility(View.VISIBLE);
                item.clear();
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("offset", "0");
                params.put("limit", "15");
                MakeRequest(url, params);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void MakeRequest(final String url, HashMap<String, String> args) {
        //clear the item from adapter before making the request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(args),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = response.optJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.optJSONObject(i);
                            final String _id = jsonObject.optString("_id");
                            final String title = jsonObject.optString("title");
                            final String tagline = jsonObject.optString("tagline");
                            final String imgUrl = jsonObject.optString("imageUrl");
                            String shortUrl = jsonObject.optString("shortUrl");
                            boolean bookMarkedByUser = jsonObject.optBoolean("bookmarkedByUser");
                            boolean newTalk = jsonObject.optBoolean("currentTalk");

                            int lengthOfCategories = jsonObject.optJSONArray("categories").length();
                            final JSONObject[] jsonObjectArray = new JSONObject[lengthOfCategories];

                            for (int j = 0; j < jsonObject.optJSONArray("categories").length(); j++) {
                                jsonObjectArray[j] = jsonObject.optJSONArray("categories").optJSONObject(j);
                            }

                            Card card = new Card(_id, title, tagline, jsonObjectArray, imgUrl, bookMarkedByUser, newTalk, shortUrl);
                            item.add(card);
                            adapter.notifyDataSetChanged();
                        }
                        hidePDialog(progressDialog, 400);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.v("Error", "Error: " + error.getMessage());
                        hidePDialog(progressDialog, 400);
                    }
                }
        );
        adapter.notifyDataSetChanged();
        VolleyApplication.getInstance().getRequestQueue().add(request);
        imgLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLru(6400));
    }

    public void MakePreferedRequest(final String url, HashMap<String, String> args) {
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
                            String shortUrl = jsonObject.optString("shortUrl");
                            boolean bookMarkedByUser = jsonObject.optBoolean("bookmarkedByUser");
                            boolean newTalk = jsonObject.optBoolean("currentTalk");

                            int lengthOfCategories = jsonObject.optJSONArray("categories").length();
                            final JSONObject[] jsonObjectArray = new JSONObject[lengthOfCategories];

                            for (int j = 0; j < jsonObject.optJSONArray("categories").length(); j++) {
                                jsonObjectArray[j] = jsonObject.optJSONArray("categories").optJSONObject(j);
                            }

                            Card card = new Card(_id, title, tagline, jsonObjectArray, imgUrl, bookMarkedByUser, newTalk, shortUrl);
                            item.add(card);
                            adapter.notifyDataSetChanged();
                        }
                        hidePDialog(progressDialog, 400);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.v("Error", "Error: " + error.getMessage());
                        hidePDialog(progressDialog, 400);
                    }
                }
        );
        adapter.notifyDataSetChanged();
        VolleyApplication.getInstance().getRequestQueue().add(request);
        imgLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLru(6400));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog(progressDialog, 800);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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
    public String _id, title, tagline, bg, shortUrl;
    public JSONObject[] categories;
    Boolean newTalk, bookmarkedByUser;

    public Card(String id, String title, String tagline, JSONObject[] cats, String bg, Boolean bookMarked, Boolean newTalk, String shortUrl) {
        this._id = id;
        this.title = title;
        this.tagline = tagline;
        this.categories = cats;
        this.bg = bg;
        this.bookmarkedByUser = bookMarked;
        this.newTalk = newTalk;
        this.shortUrl = shortUrl;
    }

    public void setBookmarkedByUser(Boolean bookmarkedByUser) {
        this.bookmarkedByUser = bookmarkedByUser;
    }

    public Boolean getBookmarkedByUser() {
        return bookmarkedByUser;
    }
}
