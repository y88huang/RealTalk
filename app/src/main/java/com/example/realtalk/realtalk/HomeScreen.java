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
        ArrayList<Card> item = new ArrayList<Card>();

        item.add(new Card("I’m a yoga instructor that teaches around the globe.","Read More",R.drawable.rt_yoga_copy));
        item.add(new Card("I’m a yoga instructor that teaches around the globe.","Read More",R.drawable.rt_architecture_copy));
        item.add(new Card("I’m a yoga instructor that teaches around the globe.","Read More",R.drawable.rt_nature_copy));

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

