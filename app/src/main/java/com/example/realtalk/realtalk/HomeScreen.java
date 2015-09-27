package com.example.realtalk.realtalk;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.List;

import static com.example.realtalk.realtalk.Utility.KillApplicationDialog;
import static com.example.realtalk.realtalk.Utility.hidePDialog;
import static com.example.realtalk.realtalk.Utility.isNetworkStatusAvailable;

public class HomeScreen extends AppCompatActivity {

    //    private Toolbar toolbar;
    LinearLayout homeList;
    ParallaxListView listView;
    HomeListViewAdapter adapter;
    RelativeLayout sub_actionbar;
    ImageButton dropdown, logo;
    TextView mostLiked, mostBookedMarked;
    public static ImageLoader imgLoader;
    private ArrayList<Card> item;
    private ProgressDialog progressDialog;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getResources().getString(R.string.serverURL) + "api/talk/getAllTalks";

        if (!isNetworkStatusAvailable(HomeScreen.this)) {
            KillApplicationDialog(getString(R.string.connectionError), HomeScreen.this);
        }
        setContentView(R.layout.home_screen);

        //delete this commented section if topbar doesnt cause any issue.
//        toolbar = (Toolbar) findViewById(R.id.custom_actionbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        homeList = (LinearLayout)findViewById(R.id.home_list);

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
                MakeRequest(url);
            }
        });

        mostBookedMarked = (TextView) findViewById(R.id.mostBookmarkText);
        mostBookedMarked.setTypeface(FontManager.setFont(getApplicationContext(), FontManager.Font.OpenSansRegular));
        mostBookedMarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                url = getResources().getString(R.string.serverURL) + "api/talk/getTalksByMostBookMarked";
                MakeRequest(url);
            }
        });

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

        //by default make the request with default url - getAllTalks
        MakeRequest(url);

        listView.setOnDetectScrollListener(new OnDetectScrollListener() {
            Matrix imageMatrix;

            @Override
            public void onUpScrolling() {
                int first = listView.getFirstVisiblePosition();
                int last = listView.getLastVisiblePosition();
                for (int i = 0; i < (last - first); i++) {
                    NetworkImageView imageView = ((HomeListViewAdapter.ViewHolder) listView.getChildAt(i).getTag()).bg;
                    imageMatrix = imageView.getImageMatrix();
                    imageMatrix.postTranslate(0, -1f);
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
                    imageMatrix.postTranslate(0, 1f);
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

    }

    public void MakeRequest(String url) {
        //clear the item from adapter before making the request
        item.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = response.optJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.optJSONObject(i);
                            String _id = jsonObject.optString("_id");
                            String title = jsonObject.optString("title");
                            String description = jsonObject.optString("description");
                            String imgUrl = jsonObject.optString("imageUrl");

                            int lengthOfCategories = jsonObject.optJSONArray("categories").length();
                            JSONObject[] jsonObjectArray = new JSONObject[lengthOfCategories];

                            for (int j = 0; j < jsonObject.optJSONArray("categories").length(); j++) {
                                jsonObjectArray[j] = jsonObject.optJSONArray("categories").optJSONObject(j);
                            }

                            Card card = new Card(_id, title, description, jsonObjectArray, imgUrl);
                            item.add(card);

                            Log.v("likes",jsonObject.optString("likesCount"));
                            Log.v("bookmark",jsonObject.optString("bookmarkCount"));
                        }
                        adapter.notifyDataSetChanged();
                        hidePDialog(progressDialog);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.v("Error", "Error: " + error.getMessage());
                        hidePDialog(progressDialog);
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog(progressDialog);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!isNetworkStatusAvailable(HomeScreen.this)) {
            KillApplicationDialog(getString(R.string.connectionError), HomeScreen.this);
        }
    }
}

class Card {
    public String _id, title, description, bg;
    public JSONObject[] categories;

    public Card(String id, String title, String descript, JSONObject[] cats, String bg) {
        this._id = id;
        this.title = title;
        this.description = descript;
        this.categories = cats;
        this.bg = bg;
    }
}

