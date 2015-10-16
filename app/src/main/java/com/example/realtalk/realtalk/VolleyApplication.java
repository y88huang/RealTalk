package com.example.realtalk.realtalk;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by alexgomes on 2015-08-24.
 */
public class VolleyApplication extends Application {

    private static VolleyApplication sInstance;
    public static int TIMEOUT = 3000*10;

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        mRequestQueue = Volley.newRequestQueue(this);
        sInstance = this;
    }

    public synchronized static VolleyApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }




}
