package com.example.realtalk.realtalk;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;


public class YourAccount extends Fragment {

    TextView txtBlurb,txtLogin;
    Button btnSignUp;
    LoginButton btnConnectWithFacebook;
    ProfileTracker mProfileTracker;
    AccessTokenTracker mTokenTracker;
    String requestURL;


    CallbackManager callbackManager;
    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            final String email = object.optString("email");
                            final String id = object.optString("id");

                            final HashMap<String, String> params = new HashMap<>();
                            params.put("profileId", id);
                            params.put("email", email);

                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(String.valueOf(R.string.tlpSharedPreference), Context.MODE_PRIVATE).edit();
                                            editor.putString("facebookEmail", email);
                                            editor.putString("facebookId", id);
                                            editor.apply();

                                            getActivity().finish();
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            VolleyLog.d("Error", "Error: " + error.getMessage());
                                        }
                                    }
                            );
                            VolleyApplication.getInstance().getRequestQueue().add(request);
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email");
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
//        setupProfileTracker();
        setupTokenTracker();

        mTokenTracker.startTracking();
//        mProfileTracker.startTracking();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_account, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/registerByFacebook";

        ((Authentication) getActivity()).SetToolBarTitle("YOUR ACCOUNT");

        txtBlurb = (TextView)getActivity().findViewById(R.id.yourAccountBlurb);
        txtBlurb.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));

        btnSignUp= (Button)getActivity().findViewById(R.id.btnSignUp);
        btnSignUp.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));

        txtLogin = (TextView)getActivity().findViewById(R.id.txtLogin);
        txtLogin.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));
        txtLogin.setPaintFlags(txtLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


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

        btnConnectWithFacebook = (LoginButton)getActivity().findViewById(R.id.btnConnectWithFacebok);
        btnConnectWithFacebook.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));

        btnConnectWithFacebook.setReadPermissions(Arrays.asList("public_profile, email"));
        btnConnectWithFacebook.setFragment(this);
        btnConnectWithFacebook.registerCallback(callbackManager, callback);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
//        mTextDetails.setText(constructWelcomeMessage(profile));
    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
//        mProfileTracker.stopTracking();
    }


    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("VIVZ", "" + currentAccessToken);
            }
        };
    }

//    private void setupProfileTracker() {
//        mProfileTracker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//                Log.d("VIVZ", "" + currentProfile);
//                txtLogin.setText(constructWelcomeMessage(currentProfile));
//            }
//        };
//    }
//
//    private String constructWelcomeMessage(Profile profile) {
//        StringBuffer stringBuffer = new StringBuffer();
//        if (profile != null) {
//            stringBuffer.append("Welcome " + profile.getId());
//        }
//        return stringBuffer.toString();
//    }

}
