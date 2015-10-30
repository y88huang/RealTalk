package com.example.realtalk.realtalk;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by alexgomes on 2015-09-30.
 */
public class Authentication extends FragmentActivity {

    TextView txtAuthTitle;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.authentication);

        Fragment yourAccount = new YourAccount();
        FragmentTransaction yourAccountTransaction =  getSupportFragmentManager().beginTransaction();
        yourAccountTransaction.replace(R.id.fragmentReplacer,yourAccount).commit();

        txtAuthTitle = (TextView)findViewById(R.id.txtAuthTitle);
        txtAuthTitle.setTypeface(FontManager.setFont(this, FontManager.Font.JustAnotherHandRegular));

        backButton = (ImageButton)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void SetToolBarTitle(String tit){
        txtAuthTitle.setText(tit);
    }
}
