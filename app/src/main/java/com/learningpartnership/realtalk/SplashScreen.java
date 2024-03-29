package com.learningpartnership.realtalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import static com.learningpartnership.realtalk.Utility.isNetworkStatusAvailable;

/**
 * Created by alexgomes on 2015-09-27.
 */
public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;
    SharedPreferences sharedPreferences;
    String prefFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        prefFile = getResources().getString(R.string.tlpSharedPreference);
        sharedPreferences = this.getApplicationContext().getSharedPreferences(prefFile, MODE_PRIVATE);
        final boolean firstRun = sharedPreferences.getBoolean("firstRun", true);

        if (!isNetworkStatusAvailable(SplashScreen.this)) {
            Utility.KillApplicationDialog(getString(R.string.connectionError), SplashScreen.this);
        } else {
            if (firstRun) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirstOnBoarding firstOnBoarding = new FirstOnBoarding();
                        android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.add(android.R.id.content, firstOnBoarding);
                        transaction.commit();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("firstRun", false);
                        editor.apply();
                    }
                }, SPLASH_TIME_OUT);
            } else {
                Intent runHomeScreen = new Intent(this, HomeScreen.class);
                startActivity(runHomeScreen);
            }
        }
    }
}
