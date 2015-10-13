package com.example.realtalk.realtalk;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by alexgomes on 2015-09-27.
 */
public class SplashScreen extends Activity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                FirstOnBoarding firstOnBoarding = new FirstOnBoarding();
                android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.setCustomAnimations(R.anim.abc_slide_in_bottom,R.anim.abc_slide_out_bottom);
                transaction.add(android.R.id.content, firstOnBoarding);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        }, SPLASH_TIME_OUT);

    }

}
