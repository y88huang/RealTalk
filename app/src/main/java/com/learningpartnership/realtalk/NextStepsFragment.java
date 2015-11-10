package com.learningpartnership.realtalk;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import static com.learningpartnership.realtalk.Utility.KillApplicationDialog;
import static com.learningpartnership.realtalk.Utility.isNetworkStatusAvailable;

public class NextStepsFragment extends Fragment {

    static TextView txtTalkTitle, description, location, link, txtRecommendedResources,
            nextRelatedTalkContentTitle, nextRelatedTalkContentDescription,
            nextCat1, nextCat2, nextCat3;
    static NetworkImageView nextImageHeader, nextAvatarImg, nextImgRelatedTalk;
    static ImageView nextIconLink;

    TextView txtRecomTalkTitle, nextRelatedTalkTitle;
    ImageView addNextStep;
    ImageButton btnNextRecomBookmark, btnNextRecomLike, btnNextRecomShare;
    ArrayList<NextSteps> nextStepsArrayList;
    String nextStepsUrl, nextStepUrl, removeNextStepUrl, prefFile, userID, facebookId;
    LayoutInflater layoutInflater;
    RelativeLayout nextRelatedTalkLayout;
    SharedPreferences sharedPreferences;
    Boolean nextStepAdded,isNextStepLiked,isNextBookMarked;

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

        if (!isNetworkStatusAvailable(getActivity())) {
            KillApplicationDialog(getString(R.string.connectionError), getActivity());
        }

        nextStepUrl = getActivity().getResources().getString(R.string.serverURL) + "api/user/addNextStepToUser";
        removeNextStepUrl = getActivity().getResources().getString(R.string.serverURL) + "api/user/removeNextStepFromUser";
        prefFile = getResources().getString(R.string.tlpSharedPreference);

        nextStepAdded = false;

        sharedPreferences = getActivity().getSharedPreferences(prefFile, Context.MODE_PRIVATE);

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
                Utility.OpenThisLink(getActivity(), link.getText().toString());
            }
        });

        nextIconLink = (ImageView) getActivity().findViewById(R.id.nextIconLink);

        nextImageHeader = (NetworkImageView) getActivity().findViewById(R.id.nextImageHeader);
        nextAvatarImg = (NetworkImageView) getActivity().findViewById(R.id.nextImageAvatar);

        txtRecommendedResources = (TextView) getActivity().findViewById(R.id.txtRecommendedResources);
        txtRecommendedResources.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        txtRecomTalkTitle = (TextView) getActivity().findViewById(R.id.nextRecomTalkTitle);
        txtRecomTalkTitle.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        nextImgRelatedTalk = (NetworkImageView) getActivity().findViewById(R.id.nextImgRelatedTalk);

        nextRelatedTalkTitle = (TextView) getActivity().findViewById(R.id.nextRelatedTalkTitle);
        nextRelatedTalkTitle.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        nextRelatedTalkContentTitle = (TextView) getActivity().findViewById(R.id.nextRelatedTalkContentTitle);
        nextRelatedTalkContentTitle.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratBold));

        nextRelatedTalkContentDescription = (TextView) getActivity().findViewById(R.id.nextRelatedTalkContentDescription);
        nextRelatedTalkContentDescription.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

        nextCat1 = (TextView) getActivity().findViewById(R.id.nextCat1);
        nextCat1.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

        nextCat2 = (TextView) getActivity().findViewById(R.id.nextCat2);
        nextCat2.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

        nextCat3 = (TextView) getActivity().findViewById(R.id.nextCat3);
        nextCat3.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

        nextRelatedTalkLayout = (RelativeLayout) getActivity().findViewById(R.id.nextRelatedTalkLayout);
        nextRelatedTalkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RealTalk.class);
                intent.putExtra("talkID", RealTalkFragment.relatedTalkId);
                getActivity().startActivity(intent);
            }
        });

        isNextStepLiked = false;
        btnNextRecomLike = (ImageButton) getActivity().findViewById(R.id.btnNextRecomLike);
        btnNextRecomLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealTalkFragment.btnRecomLike.performClick();

                if(!isNextStepLiked){
                    btnNextRecomLike.setImageResource(R.drawable.iconheart_filled);
                    isNextStepLiked = true;
                }else{
                    btnNextRecomLike.setImageResource(R.drawable.iconheart_outline);
                    isNextStepLiked = false;
                }
            }
        });

        btnNextRecomShare = (ImageButton) getActivity().findViewById(R.id.btnNextRecomShare);
        btnNextRecomShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealTalkFragment.btnShare.performClick();
            }
        });

        isNextBookMarked = false;
        btnNextRecomBookmark = (ImageButton) getActivity().findViewById(R.id.btnNextRecomBookmark);
        btnNextRecomBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealTalkFragment.btnRecomBookmark.performClick();

                if(!isNextBookMarked){
                    btnNextRecomBookmark.setImageResource(R.drawable.iconbookmark_filled);
                    isNextBookMarked = true;
                }else{
                    btnNextRecomBookmark.setImageResource(R.drawable.iconbookmark_outline);
                    isNextBookMarked = false;
                }
            }
        });


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

    public void SetRecommendedResources(final ArrayList<NextSteps> listOfNextSteps) {

        final LinearLayout loopedText = (LinearLayout) getActivity().findViewById(R.id.listOfRecommendedResources);
        loopedText.removeAllViews();
        layoutInflater = (LayoutInflater) getActivity().getApplicationContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < listOfNextSteps.size(); i++) {

            final View view = layoutInflater.inflate(R.layout.single_next_steps_row, loopedText, false);

            TextView txtAction = (TextView) view.findViewById(R.id.txtAction);
            txtAction.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratBold));
            txtAction.setText(i + 1 + ". " + listOfNextSteps.get(i).action);

            final TextView txtNextStepsUrl = (TextView) view.findViewById(R.id.txtNextStepsUrl);
            txtNextStepsUrl.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));
            txtNextStepsUrl.setText(listOfNextSteps.get(i).url);

            txtNextStepsUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utility.OpenThisLink(getActivity(), txtNextStepsUrl.getText().toString());
                }
            });
            view.setTag(i);
            loopedText.addView(view);

            addNextStep = (ImageView) view.findViewById(R.id.addNextStep);
            final int finalI = i;
            addNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isNextStepAdded = listOfNextSteps.get(finalI).isNextStepAdded;
                    if (!isNextStepAdded) {
                        AddNextStepToUer(listOfNextSteps.get(finalI).id, view, loopedText, finalI);
                        listOfNextSteps.get(finalI).setIsNextStepAdded(true);
                    }
                    if(isNextStepAdded){
                        RemoveNextStepFromUser(listOfNextSteps.get(finalI).id,loopedText,finalI);
                        listOfNextSteps.get(finalI).setIsNextStepAdded(false);
                    }
                }
            });
        }
        loopedText.getChildAt(loopedText.getChildCount() - 1).findViewById(R.id.divider).setVisibility(View.INVISIBLE);
    }

    public void AddNextStepToUer(String nextStepId, final View view, final LinearLayout loopedText, final int position) {

        userID = sharedPreferences.getString("userID", "");
        facebookId = sharedPreferences.getString("facebookId", "");

        if (userID.isEmpty() && facebookId.isEmpty()) {
            Intent intent = new Intent(getActivity(), Authentication.class);
            getActivity().startActivity(intent);
        } else {
            final HashMap<String, String> params = new HashMap<>();
            params.put("userId", userID);
            params.put("talkId", RealTalkFragment.specificId);
            params.put("nextStepId", nextStepId);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, nextStepUrl, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ImageView checkmarkView = (ImageView) loopedText.getChildAt(position).findViewById(R.id.addNextStep);
                            checkmarkView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.iconcheckmark_blue2, null));
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
    }

    public void RemoveNextStepFromUser(String nextStepId, final LinearLayout loopedText, final int position) {
        userID = sharedPreferences.getString("userID", "");
        facebookId = sharedPreferences.getString("facebookId", "");

        if (userID.isEmpty() && facebookId.isEmpty()) {
            Intent intent = new Intent(getActivity(), Authentication.class);
            getActivity().startActivity(intent);
        } else {
            final HashMap<String, String> params = new HashMap<>();
            params.put("userId", userID);
            params.put("talkId", RealTalkFragment.specificId);
            params.put("nextStepId", nextStepId);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, removeNextStepUrl, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ImageView checkmarkView = (ImageView) loopedText.getChildAt(position).findViewById(R.id.addNextStep);
                            checkmarkView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.iconadd_blue, null));
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
    Boolean isNextStepAdded;

    public NextSteps(String id, String action, String url) {
        this.id = id;
        this.action = action;
        this.url = url.replaceFirst("^(http://|https://|www\\.)","");
        this.isNextStepAdded = false;
    }

    public Boolean getIsNextStepAdded() {
        return isNextStepAdded;
    }

    public void setIsNextStepAdded(Boolean isNextStepAdded) {
        this.isNextStepAdded = isNextStepAdded;
    }
}
