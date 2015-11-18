package com.learningpartnership.realtalk;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by alexgomes on 2015-11-17. - alex.09hg@gmail.com
 */
public class ParseCustomReceiver extends ParsePushBroadcastReceiver {

    private static final String TAG = "MyCustomReceiver";
    String talkId;
    int NOTIFICATION_ID = 0;
    int numMessages = 0;
    NotificationManager mNotifM;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extra = intent.getExtras();
        String json = extra.getString("com.parse.Data");

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            String alert = jsonObject.getString("alert");
            String title = jsonObject.getString("title");
            String shortId = jsonObject.getString("shortId");
            MakeRequest(context, shortId, title, alert);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void generateNotification(Context context, String title, String alert, String talkId) {

        Intent intent = new Intent(context, RealTalk.class);
        intent.putExtra("talkID", talkId);
        PendingIntent contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(alert)
                .setAutoCancel(true)
                .setNumber(++numMessages);
        mBuilder.setContentIntent(contentIntent);
        mNotifM.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void MakeRequest(final Context context, String shortId, final String title, final String alert) {

        String url = context.getResources().getString(R.string.serverURL) + "api/talk/getTalkByShortUrl";

        RequestParams params = new RequestParams();
        params.put("shortUrl", shortId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        talkId = response.optJSONObject("data").optString("_id");
                        generateNotification(context, title, alert, talkId);
                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }
        });
    }
}
