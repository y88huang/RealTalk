package com.example.realtalk.realtalk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setIcon(R.mipmap.ic_launcher)
                .setTitle("Connection error")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid());
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

}
