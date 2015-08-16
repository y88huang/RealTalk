package com.example.realtalk.realtalk;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class first_screen extends AppCompatActivity{

    private Toolbar toolbar;
    private RecyclerView hRecyclerView;
    private RecyclerView.Adapter hRecyclerViewAdapter;
    private RecyclerView.LayoutManager hLayoutManager;
    RelativeLayout sub_actionbar;
    ImageButton dropdown,logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);

        toolbar = (Toolbar) findViewById(R.id.custom_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sub_actionbar = (RelativeLayout) findViewById(R.id.sub_actionbar);

        logo = (ImageButton)findViewById(R.id.logo);
        dropdown =  (ImageButton) findViewById(R.id.dropdown);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Logo was clicked", Toast.LENGTH_LONG).show();
            }
        });

        //topbar dropdown animation - hide/show sub toolbar
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslateAnimation slide;
                if(dropdown.getScaleY() == 1f){
                    dropdown.setScaleY(-1f);
                    sub_actionbar.setVisibility(View.VISIBLE);
                    slide = new TranslateAnimation(0,0,0,sub_actionbar.getMeasuredHeight());
                    slide.setDuration(500);
                    slide.setFillAfter(true);
                    sub_actionbar.startAnimation(slide);
                }else{
                    dropdown.setScaleY(1f);
                    sub_actionbar.setVisibility(View.VISIBLE);
                    slide = new TranslateAnimation(0,0,sub_actionbar.getMeasuredHeight(),0);
                    slide.setDuration(500);
                    slide.setFillAfter(true);
                    sub_actionbar.startAnimation(slide);
                }
            }
        });

        //custom list view for home screen
        String[] item = new String[3];
        item[0] = "Hello";
        item[1] = "Rere";
        item[2] = "Test";

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
}

