package com.learningpartnership.realtalk;


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

public class ForgotPassword extends Fragment {

    EditText txtEmail;
    Button btnSendEmail;
    String requestURL,email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((Authentication) getActivity()).SetToolBarTitle("FORGOT PASSWORD");

        requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/forgotPassword";


        btnSendEmail = (Button) getActivity().findViewById(R.id.btnDone);
        btnSendEmail.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));

        txtEmail = (EditText) getActivity().findViewById(R.id.txtEmail);

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
                    btnSendEmail.setEnabled(true);
                } else {
                    btnSendEmail.setEnabled(false);
                }
            }
        });


        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = txtEmail.getText().toString();


                //parameter being sent with body
                final HashMap<String, String> params = new HashMap<>();
                params.put("emailAddress", email);

                Log.v("params", params.toString());

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(response.optString("data").length() > 0){
                                    Toast data = Toast.makeText(getActivity(), response.optString("data"), Toast.LENGTH_SHORT);
                                    data.setGravity(Gravity.CENTER_HORIZONTAL, 0, -20);
                                    data.show();
                                }
                                if(response.optString("message").length() >0){
                                    String message = response.optString("message");
                                    Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -20);
                                    toast.show();
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
