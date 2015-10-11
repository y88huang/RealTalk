package com.example.realtalk.realtalk;


import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;

;

public class YourAccount extends Fragment {

    TextView txtBlurb,txtLogin;
    Button btnConnectWithFacebok,btnSignUp;

    CallbackManager callbackManager;
    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            com.facebook.Profile profile = Profile.getCurrentProfile();

            txtLogin.setText(profile.getName());

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_account, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        ((Authentication)getActivity()).SetToolBarTitle("YOUR ACCOUNT");

        txtBlurb = (TextView)getActivity().findViewById(R.id.yourAccountBlurb);
        txtBlurb.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));

        btnConnectWithFacebok = (Button)getActivity().findViewById(R.id.btnConnectWithFacebok);
        btnConnectWithFacebok.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));

        btnSignUp= (Button)getActivity().findViewById(R.id.btnSignUp);
        btnSignUp.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));

        txtLogin = (TextView)getActivity().findViewById(R.id.txtLogin);
        txtLogin.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));
        txtLogin.setPaintFlags(txtLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnConnectWithFacebok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "FaceBook clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment signup = new SignUp();
                FragmentTransaction signUpTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                signUpTransaction.replace(R.id.fragmentReplacer, signup);
                signUpTransaction.addToBackStack(null);
                signUpTransaction.commit();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment login = new Login();
                FragmentTransaction loginTransaction = getFragmentManager().beginTransaction();
                loginTransaction.replace(R.id.fragmentReplacer, login);
                loginTransaction.addToBackStack(null);
                loginTransaction.commit();
            }
        });

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
//        loginButton.setPublishPermissions("user_friends");
//        loginButton.setFragment(this);
//        loginButton.registerCallback(callbackManager,callback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }
}
