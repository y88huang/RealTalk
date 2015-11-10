package com.learningpartnership.realtalk;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Created by alexgomes on 2015-08-25.
 */
public class BitmapLru extends LruCache<String,Bitmap> implements ImageCache {

    public BitmapLru(int maxSize){
        super(maxSize);
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url,bitmap);
    }
}
