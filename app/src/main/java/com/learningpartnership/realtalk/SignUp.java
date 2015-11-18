package com.learningpartnership.realtalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUp extends Fragment {

    EditText txtEmail, txtPassword;
    Button btnDone;
    TextView termsCondition;
    String requestURL,email, password,prefFile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((Authentication) getActivity()).SetToolBarTitle("SIGN UP");

        requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/registerByEmail";
        prefFile = getActivity().getResources().getString(R.string.tlpSharedPreference);

        btnDone = (Button) getActivity().findViewById(R.id.btnDone);
        btnDone.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));

        termsCondition = (TextView) getActivity().findViewById(R.id.termCondition);
        termsCondition.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));
        termsCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(getActivity().getResources().getString(R.string.privacyTermsLink));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);
            }
        });

        txtEmail = (EditText) getActivity().findViewById(R.id.txtEmail);
        txtPassword = (EditText) getActivity().findViewById(R.id.txtPassword);

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isValidEmail(s.toString())) {
                    btnDone.setEnabled(true);
                } else {
                    btnDone.setEnabled(false);
                }
            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0 || s.toString() == null) {
                    btnDone.setEnabled(false);
                } else {
                    btnDone.setEnabled(true);
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();

                //parameter being sent with body
                final HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                Log.v("response",response.toString());
                                String success = response.optString("success");

                                String message = response.optString("message");
                                if(!message.isEmpty()){
                                    Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, -20);
                                    toast.show();
                                }

                                if (success.equals("1")) {
                                    String userID = response.optJSONObject("data").optString("_id");
                                    String userEmail = response.optJSONObject("data").optString("email");

                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(prefFile, Context.MODE_PRIVATE).edit();
                                    editor.putString("userEmail", userEmail);
                                    editor.putString("userID", userID);
                                    editor.apply();

                                    getActivity().finish();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d("Error", "Error: " + error.getMessage());
                            }
                        }
                );
                request.setRetryPolicy(new DefaultRetryPolicy(
                        VolleyApplication.TIMEOUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                VolleyApplication.getInstance().getRequestQueue().add(request);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}