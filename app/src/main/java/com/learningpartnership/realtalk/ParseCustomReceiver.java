package com.learningpartnership.realtalk;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by alexgomes on 2015-11-17. - alex.09hg@gmail.com
 */
public class ParseCustomReceiver extends ParsePushBroadcastReceiver {

    private static final String TAG = "MyCustomReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent == null)
            {
                Log.d(TAG, "Receiver intent null");
            }
            else
            {
                String action = intent.getAction();
                Log.d(TAG, "got action " + action);
                if (action.equals("com.iakremera.pushnotificationdemo.UPDATE_STATUS"))
                {
                    String channel = intent.getExtras().getString("com.parse.Channel");
                    JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

                    Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
                    Iterator itr = json.keys();
                    while (itr.hasNext()) {
                        String key = (String) itr.next();
                        if (key.equals("customdata"))
                        {
                            Intent pupInt = new Intent(context, RealTalk.class);
                            pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                            context.getApplicationContext().startActivity(pupInt);
                        }
                        Log.d(TAG, "..." + key + " => " + json.getString(key));
                    }
                }
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }

//    private void generateNotification(Context context, JSONObject json, String contenttext) {
//        Intent intent = new Intent(context, RealTalk.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//        NotificationManager mNotifM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher).
//                setContentTitle("TITLE").setContentText("contenttext").setNumber(++numMessages);
//
//        mBuilder.setContentIntent(contentIntent);
//        mNotifM.notify(NOTIFICATION_ID, mBuilder.build());
//
//    }
}
