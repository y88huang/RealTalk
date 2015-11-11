package com.learningpartnership.realtalk;

import android.app.Application;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by alexgomes on 2015-08-24.
 */
public class VolleyApplication extends Application {

    private Tracker mTracker;
    private static VolleyApplication sInstance;
    public static int TIMEOUT = 3000 * 10;

    String PARSE_APPLICATION_ID = "QmUQqmRvFDeNTPcjvaGnLO4WyzssRS9UmQosu7aq";
    String PARSE_CLIEND_KEY = "hNrjyxE7wtdCh24o5k07NkLHtuVmMGPzyWM2nziL";

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;

        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIEND_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

    }

    public synchronized static VolleyApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }


    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker("UA-69126442-1");
        }
        return mTracker;
    }

}
