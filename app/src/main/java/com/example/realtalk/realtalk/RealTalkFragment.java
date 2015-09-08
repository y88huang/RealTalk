package com.example.realtalk.realtalk;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.realtalk.realtalk.Utility.KillApplicationDialog;
import static com.example.realtalk.realtalk.Utility.isNetworkStatusAvialable;

public class RealTalkFragment extends Fragment {

    TextView title,description,location,link;
    ProgressDialog progressDialog;
    String getTalkById = "http://tlpserver.herokuapp.com/api/talk/getTalkById?_id";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_real_talk, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(!isNetworkStatusAvialable(this.getActivity().getApplicationContext())){
            KillApplicationDialog(getString(R.string.connectionError), this.getActivity());
        }

        String id = getTalkById+getActivity().getIntent().getExtras().getString("talkID");
         //getTalkById+"55ec3a8e0de3cf9a24fffc0f";

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        title = (TextView)getActivity().findViewById(R.id.title);
        title.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        description = (TextView)getActivity().findViewById(R.id.description);
        description.setTypeface(FontManager.setFont(getActivity().getApplicationContext(),FontManager.Font.OpenSansRegular));

        location = (TextView)getActivity().findViewById(R.id.location);
        location.setTypeface(FontManager.setFont(getActivity().getApplicationContext(),FontManager.Font.MontSerratRegular));

        link = (TextView)getActivity().findViewById(R.id.link);
        link.setTypeface(FontManager.setFont(getActivity().getApplicationContext(),FontManager.Font.MontSerratRegular));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,id,(String)null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            title.setText(data.optString("title"));
                            description.setText(data.optString("description"));
                            location.setText(data.optString("location"));
                            link.setText("url not found");

                            Utility.hidePDialog(progressDialog);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "Error: " + error.getMessage());
                        System.out.println(error);
                        Utility.hidePDialog(progressDialog);
                    }
                }
        );

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000 * 10,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        VolleyApplication.getInstance().getRequestQueue().add(request);

    }

    @Override
    public void onResume() {
        super.onResume();

        if(!isNetworkStatusAvialable(this.getActivity().getApplicationContext())){
            KillApplicationDialog(getString(R.string.connectionError), this.getActivity());
        }
    }
}
