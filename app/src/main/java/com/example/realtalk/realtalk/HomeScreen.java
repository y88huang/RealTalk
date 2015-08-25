package com.example.realtalk.realtalk;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeScreen extends AppCompatActivity{

//    private Toolbar toolbar;
    private RecyclerView hRecyclerView;
    private RecyclerView.Adapter hRecyclerViewAdapter;
    private RecyclerView.LayoutManager hLayoutManager;
    RelativeLayout sub_actionbar;
    ImageButton dropdown,logo;
    TextView mostLiked,mostBookedMarked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        //delete this commented section if topbar doesnt cause any issue.
//        toolbar = (Toolbar) findViewById(R.id.custom_actionbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sub_actionbar = (RelativeLayout) findViewById(R.id.sub_actionbar);

        logo = (ImageButton)findViewById(R.id.logo);
        dropdown =  (ImageButton) findViewById(R.id.dropdown);

        mostLiked = (TextView) findViewById(R.id.mostLikedText);
        mostLiked.setTypeface(FontManager.setFont(getApplicationContext(), FontManager.Font.OpenSansRegular));
        mostLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Most Liked", Toast.LENGTH_SHORT).show();
            }
        });

        mostBookedMarked = (TextView)findViewById(R.id.mostBookmarkText);
        mostBookedMarked.setTypeface(FontManager.setFont(getApplicationContext(), FontManager.Font.OpenSansRegular));
        mostBookedMarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Most Booked Marked",Toast.LENGTH_SHORT).show();
            }
        });

        //Logo clicked listener
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Logo was clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),RealTalk.class);
                startActivity(intent);
            }
        });

        //topbar dropdown animation - hide/show sub toolbar
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator anim;
                if(dropdown.getScaleY() == 1f){
                    dropdown.setScaleY(-1f);
                    anim = ObjectAnimator.ofFloat(sub_actionbar, "translationY", 0.0f, (float)sub_actionbar.getMeasuredHeight());
                    anim.setDuration(300);
                    anim.setRepeatCount(0);
                    anim.start();
                }else{
                    dropdown.setScaleY(1f);
                    anim = ObjectAnimator.ofFloat(sub_actionbar, "translationY", (float)sub_actionbar.getMeasuredHeight(),0.0f);
                    anim.setDuration(300);
                    anim.setRepeatCount(0);
                    anim.start();
                }
            }
        });

        //custom list view for HomeScreen screen
        final ArrayList<Card> item = new ArrayList<>();

        //http://tlpserver.herokuapp.com/api/talk/getAllTalks
        //http://jsonplaceholder.typicode.com/posts

        String url = "http://tlpserver.herokuapp.com/api/talk/getAllTalks";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,url,(String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                  JSONArray title = response.getJSONArray("data");
                        JSONArray array = response.optJSONArray("data");
                        for(int i =0; i<array.length();i++){
                            JSONObject jsonObject = array.optJSONObject(i);
                            String title = jsonObject.optString("title");
                            item.add(new Card(title,"Read More",R.drawable.rt_yoga_copy));
                        }
//                        for(int i = 0; i < array.length(); i++){
//                            try {
//                                String title = response.getJSONArray("data").getJSONObject(i).getString("title");
//                                item.add(new Card(title,"Read More",R.drawable.rt_yoga_copy));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }
        );
        System.setProperty("http.keepAlive", "false");
        VolleyApplication.getInstance().getRequestQueue().add(request);


        hRecyclerView = (RecyclerView) findViewById(R.id.home_list);
        hRecyclerView.setHasFixedSize(true);

        hLayoutManager = new LinearLayoutManager(getApplicationContext());
        hRecyclerView.setLayoutManager(hLayoutManager);

        hRecyclerViewAdapter = new HomeRecycleViewAdapter(item);
        hRecyclerView.setAdapter(hRecyclerViewAdapter);
    }
}
class Card{
    public String title,readMore;
    public int bg;

    public Card(String title, String readMore, int bg) {
        this.title = title;
        this.readMore = readMore;
        this.bg = bg;
    }
}

