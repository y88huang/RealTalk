package com.serindlabs.realtalk;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by alexgomes on 2015-09-06.
 */
public class Utility{

    public static boolean isNetworkStatusAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }

    //Show dialog if there is no Internet connection
    public static void KillApplicationDialog(String message, Context context) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setIcon(R.mipmap.ic_launcher)
                .setTitle("Connection error")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
//                        android.os.Process.killProcess(android.os.Process.myPid());
                        dialoginterface.dismiss();
                        dialoginterface.cancel();
                    }
                }).show();
    }

    // Hide global dialog screen in 800 milliseconds
    public static void hidePDialog(final ProgressDialog progressDialog, int hideAfter) {
        if (progressDialog != null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    progressDialog.cancel();
                }
            },hideAfter);
        }
    }

    //Open any link in browser
    public static void OpenThisLink(Activity context,String url){
        if (!url.startsWith("http://") && !url.startsWith("https://")){
            url = "http://" + url;
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
            context.startActivity(intent);
        }else if(url.startsWith("http://") || url.startsWith("https://")){
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
            context.startActivity(intent);
        }
    }


    public static void SendNotification(Context context) {

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext());

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.mipmap.ic_launcher);

        // This intent is fired when notification is clicked
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://javatechig.com/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle("Notifications Title");

        // Content text, which appears in smaller text below the title
        builder.setContentText("Your notification content here.");

        // The subtext, which appears under the text on newer devices.
        // This will show-up in the devices with Android 4.2 and above only
        builder.setSubText("Tap to view documentation about notifications.");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar

        notificationManager.notify(3, builder.build());
    }
}
