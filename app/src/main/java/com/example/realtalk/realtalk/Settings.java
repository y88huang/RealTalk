package com.example.realtalk.realtalk;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import static com.example.realtalk.realtalk.Utility.KillApplicationDialog;
import static com.example.realtalk.realtalk.Utility.isNetworkStatusAvailable;

/**
 * Created by alexgomes on 2015-09-29. - alex.09hg@gmail.com
 */
public class Settings extends Fragment {

    TextView txtSettings, txtEmail, txtAboutUs,
            txtPushNotifications, txtTermsOfUse,
            txtSignOut, txtUserEmail;
    ImageButton btnBack;

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_screen, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!isNetworkStatusAvailable(getActivity())) {
            KillApplicationDialog(getString(R.string.connectionError), getActivity());
        }

        sharedPreferences = getActivity().getSharedPreferences(String.valueOf(R.string.tlpSharedPreference), Context.MODE_PRIVATE);

        txtSettings = (TextView) getActivity().findViewById(R.id.txtSettings);
        txtSettings.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        txtEmail = (TextView) getActivity().findViewById(R.id.txtEmail);
        txtEmail.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

        txtAboutUs = (TextView) getActivity().findViewById(R.id.txtAboutUs);
        txtAboutUs.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

        txtPushNotifications = (TextView) getActivity().findViewById(R.id.txtPushNotifications);
        txtPushNotifications.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

        txtTermsOfUse = (TextView) getActivity().findViewById(R.id.txtTerms);
        txtTermsOfUse.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

        txtSignOut = (TextView) getActivity().findViewById(R.id.textSignOut);
        txtSignOut.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

        txtUserEmail = (TextView) getActivity().findViewById(R.id.txtUserEmail);
        txtUserEmail.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));
        if (sharedPreferences.getString("userEmail", "").isEmpty()) {
            txtUserEmail.setText("");
        } else {
            txtUserEmail.setText(sharedPreferences.getString("userEmail", ""));
        }

        btnBack = (ImageButton) getActivity().findViewById(R.id.btnBackButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        txtSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Are you sure you want to sign out");

                alertDialogBuilder.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPreferences.edit().remove("userID").apply();
                        sharedPreferences.edit().remove("userEmail").apply();
                        sharedPreferences.edit().remove("facebookId").apply();
                        sharedPreferences.edit().remove("facebookEmail").apply();
                        txtUserEmail.setText("");

                        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
                        LoginManager.getInstance().logOut();
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            }
        });

        txtAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getActivity().getResources().getString(R.string.aboutUsLink));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);
            }
        });


        txtTermsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getActivity().getResources().getString(R.string.privacyTermsLink));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isNetworkStatusAvailable(getActivity())) {
            KillApplicationDialog(getString(R.string.connectionError), getActivity());
        }
    }
}
