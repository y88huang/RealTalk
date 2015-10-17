package com.example.realtalk.realtalk;


import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.realtalk.realtalk.Utility.KillApplicationDialog;
import static com.example.realtalk.realtalk.Utility.isNetworkStatusAvailable;

public class NextStepsFragment extends Fragment {

    static TextView txtTalkTitle, description, location, link, txtRecommendedResources;
    static NetworkImageView nextImageHeader, nextAvatarImg;
    ArrayList<NextSteps> nextStepsArrayList;
    String nextStepsUrl;
    LayoutInflater layoutInflater;

    public NextStepsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_next_steps, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtTalkTitle = (TextView) getActivity().findViewById(R.id.nextTalkTitle);
        txtTalkTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        description = (TextView) getActivity().findViewById(R.id.nextDescription);
        description.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.OpenSansRegular));

        location = (TextView) getActivity().findViewById(R.id.nextLocation);
        location.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));

        link = (TextView) getActivity().findViewById(R.id.nextLink);
        link.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("link", link.getText().toString());
                Utility.OpenThisLink(getActivity(), link.getText().toString());
            }
        });

        nextImageHeader = (NetworkImageView) getActivity().findViewById(R.id.nextImageHeader);
        nextAvatarImg = (NetworkImageView) getActivity().findViewById(R.id.nextImageAvatar);

        txtRecommendedResources = (TextView) getActivity().findViewById(R.id.txtRecommendedResources);
        txtRecommendedResources.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));


        nextStepsUrl = getActivity().getResources().getString(R.string.serverURL) + "api/talk/getTalkNextSteps";

        HashMap<String, String> params = new HashMap<>();
        params.put("id", RealTalkFragment.specificId);
        nextStepsArrayList = new ArrayList<>();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, nextStepsUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject data = response.optJSONObject("data");
                        JSONArray nextSteps = data.optJSONArray("nextSteps");

                        for (int i = 0; i < nextSteps.length(); i++) {
                            String _id = nextSteps.optJSONObject(i).optString("_id");
                            String action = nextSteps.optJSONObject(i).optString("action");
                            String url = nextSteps.optJSONObject(i).optString("url");
                            nextStepsArrayList.add(new NextSteps(_id, action, url));
                        }
                        SetRecommendedResources(nextStepsArrayList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "Error: " + error.getMessage());
                        Log.v("error", error.toString());
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

    public void SetRecommendedResources(ArrayList<NextSteps> listOfNextSteps) {

        LinearLayout loopedText = (LinearLayout) getActivity().findViewById(R.id.listOfRecommendedResources);
        loopedText.removeAllViews();
        layoutInflater = (LayoutInflater) getActivity().getApplicationContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < listOfNextSteps.size(); i++) {

            View view = layoutInflater.inflate(R.layout.single_next_steps_row, loopedText, false);

            TextView txtAction = (TextView) view.findViewById(R.id.txtAction);
            txtAction.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratBold));
            txtAction.setText(i + 1 + ". " + listOfNextSteps.get(i).action);

            final TextView txtNextStepsUrl = (TextView) view.findViewById(R.id.txtNextStepsUrl);
            txtNextStepsUrl.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));
            txtNextStepsUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.OpenThisLink(getActivity(), txtNextStepsUrl.getText().toString());
                }
            });
            loopedText.addView(view);
        }
        loopedText.getChildAt(loopedText.getChildCount() -1).findViewById(R.id.divider).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isNetworkStatusAvailable(this.getActivity().getApplicationContext())) {
            KillApplicationDialog(getString(R.string.connectionError), this.getActivity());
        }
    }
}

class NextSteps {
    String id, action, url;

    public NextSteps(String id, String action, String url) {
        this.id = id;
        this.action = action;
        this.url = url;
    }
}
