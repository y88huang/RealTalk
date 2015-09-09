package com.example.realtalk.realtalk;


import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.example.realtalk.realtalk.Utility.KillApplicationDialog;
import static com.example.realtalk.realtalk.Utility.hidePDialog;
import static com.example.realtalk.realtalk.Utility.isNetworkStatusAvialable;

public class RealTalkFragment extends Fragment {

    TextView title,description,location,link,inBriefTitle,inBriefList;
    ProgressDialog progressDialog;
    String getTalkById;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_real_talk, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getTalkById = getActivity().getResources().getString(R.string.serverURL)+"api/talk/getTalkById";

        if(!isNetworkStatusAvialable(this.getActivity().getApplicationContext())){
            KillApplicationDialog(getString(R.string.connectionError), this.getActivity());
        }

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

        inBriefTitle = (TextView)getActivity().findViewById(R.id.inBriefTitle);
        inBriefTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));
        inBriefTitle.setText(getActivity().getResources().getString(R.string.inBriefTitle));

        inBriefList = (TextView)getActivity().findViewById(R.id.inBriefList);
        inBriefList.setTypeface(FontManager.setFont(getActivity().getApplicationContext(),FontManager.Font.MontSerratRegular));

        //specific id being retrieved from homeScreen on list item click event.
        String specificId = getActivity().getIntent().getExtras().getString("talkID");

        //parameter being sent with body
        HashMap<String, String> params = new HashMap<>();
        params.put("id", specificId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,getTalkById,new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray urls = data.optJSONArray("urls");
                            JSONArray inBriefArray = data.optJSONArray("inBrief");

                            title.setText(data.optString("title"));
                            description.setText(data.optString("description"));
                            location.setText(data.optString("location"));
                            link.setText(urls.optString(0));

                            for(int i = 0;i<inBriefArray.length();i++){
                                inBriefList.append(inBriefArray.optString(i)+"\n\n");
                            }


                            hidePDialog(progressDialog);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hidePDialog(progressDialog);
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
