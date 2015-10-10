package com.example.realtalk.realtalk;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class YourAccount extends Fragment {

    TextView txtBlurb,txtLogin;
    Button btnConnectWithFacebok,btnSignUp;
    UiLifecycleHelper uiLifecycleHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_account, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                FragmentTransaction signUpTransaction = getFragmentManager().beginTransaction();
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


//        ++++++++++++++++++


        View view = inflater.inflate(R.layout.splash, container, false);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        // If using in a fragment
        loginButton.setFragment(YourAccount.this);
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });




//        +++++++++++




    }

}
