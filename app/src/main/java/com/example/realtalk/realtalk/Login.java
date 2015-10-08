package com.example.realtalk.realtalk;


import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Fragment;
import android.os.StrictMode;
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

public class Login extends Fragment {

    EditText txtEmail, txtPassword;
    Button btnDone;
    TextView termsCondition, forgotPassword;
    String email, password;
    String requestURL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((Authentication) getActivity()).SetToolBarTitle("LOG IN");

        requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/loginByEmail";

        btnDone = (Button) getActivity().findViewById(R.id.btnDone);
        btnDone.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));

        termsCondition = (TextView) getActivity().findViewById(R.id.termCondition);
        termsCondition.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));

        txtEmail = (EditText) getActivity().findViewById(R.id.txtEmail);
        txtPassword = (EditText) getActivity().findViewById(R.id.txtPassword);

        forgotPassword = (TextView) getActivity().findViewById(R.id.forgotPassword);
        forgotPassword.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));
        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment forgotPassword = new ForgotPassword();
                FragmentTransaction forgotPasswordTransaction = getFragmentManager().beginTransaction();
                forgotPasswordTransaction.replace(R.id.fragmentReplacer, forgotPassword);
                forgotPasswordTransaction.addToBackStack(null);
                forgotPasswordTransaction.commit();
            }
        });

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
                                String message = response.optString("message");
                                String success = response.optString("success");

                                if (!message.isEmpty()) {
                                    Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER, 0, 250);
                                    toast.show();
                                }

                                if (success.equals("1")) {
                                    String userID = response.optJSONObject("data").optString("_id");
                                    Log.v("userID", userID);

                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(String.valueOf(R.string.tlpSharedPreference), getActivity().MODE_PRIVATE).edit();
                                    editor.putString("userID", userID);
                                    editor.apply();
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

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
}
