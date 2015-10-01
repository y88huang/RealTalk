package com.example.realtalk.realtalk;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by alexgomes on 2015-09-30.
 */
public class Authentication extends Activity {

    TextView txtAuthTitle;
    ImageButton backButton,btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.authentication);

        Fragment yourAccount = new YourAccount();
        FragmentTransaction yourAccountTransaction = getFragmentManager().beginTransaction();
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

        btnClose = (ImageButton)findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void SetToolBarTitle(String tit){
        txtAuthTitle.setText(tit);
    }
}