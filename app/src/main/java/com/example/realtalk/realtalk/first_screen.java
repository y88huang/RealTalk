package com.example.realtalk.realtalk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class first_screen extends AppCompatActivity{

    private Toolbar toolbar;
    private RecyclerView hRecyclerView;
    private RecyclerView.Adapter hRecyclerViewAdapter;
    private RecyclerView.LayoutManager hLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);

        toolbar = (Toolbar) findViewById(R.id.custom_actionbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton logo = (ImageButton)findViewById(R.id.logo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You clicked the logo", Toast.LENGTH_LONG).show();
            }
        });

        //////
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

}

