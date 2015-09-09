package com.example.realtalk.realtalk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by alexgomes on 2015-09-06.
 */
public class Utility{

    ProgressDialog progressDialog;

    public static boolean isNetworkStatusAvialable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
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
    public static void hidePDialog(final ProgressDialog progressDialog) {
        if (progressDialog != null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                    progressDialog.cancel();
                }
            },800);
        }
    }
}
