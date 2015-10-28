package com.example.realtalk.realtalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by alexgomes on 2015-09-29.
 */
public class ProfileNextStepsFragment extends Fragment {

    TextView yourNextStepsGoHere, txtCheckout;
    ImageView nextImageView;
    ImageButton expandCheckout;
    SharedPreferences sharedPreferences;
    CustomCard checkoutCard;
    String userID, requestURL, removeNextStepUrl;
    ArrayList<UserNextSteps> userNextStepsArrayList;
    LinearLayout loopedText;
    int counter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_next_steps, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        sharedPreferences = getActivity().getSharedPreferences(String.valueOf(R.string.tlpSharedPreference), Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/getAllNextSteps";
        removeNextStepUrl = getActivity().getResources().getString(R.string.serverURL) + "api/user/removeNextStepFromUser";

        yourNextStepsGoHere = (TextView) getActivity().findViewById(R.id.yourNextStepsHere);
        yourNextStepsGoHere.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        txtCheckout = (TextView) getActivity().findViewById(R.id.txtCheckout);
        txtCheckout.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        nextImageView = (ImageView) getActivity().findViewById(R.id.nextImageView);
        expandCheckout = (ImageButton) getActivity().findViewById(R.id.expandCheckout);

        expandCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (expandCheckout.getScaleY() == 1f) {
                    expandCheckout.setScaleY(-1f);
                    checkoutCard.doExpand();
                } else {
                    expandCheckout.setScaleY(1f);
                    checkoutCard.doCollapse();
                }
            }
        });


        if (userID == null || userID.isEmpty()) {
            nextImageView.setVisibility(View.VISIBLE);
            yourNextStepsGoHere.setVisibility(View.VISIBLE);
        }

        userNextStepsArrayList = new ArrayList<>();
        MakeRequest(requestURL);

    }


    public void MakeRequest(String requestURL) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userID);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        userNextStepsArrayList.clear();
                        JSONArray data = response.optJSONArray("data");
                        Log.v("Next Steps", data.toString());
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.optJSONObject(i);
                            userNextStepsArrayList.add(new UserNextSteps(object));
                        }

                        CheckOutCard(userNextStepsArrayList);

                        if (userNextStepsArrayList.size() > 0) {
                            nextImageView.setVisibility(View.INVISIBLE);
                            yourNextStepsGoHere.setVisibility(View.INVISIBLE);
                        } else {
                            nextImageView.setVisibility(View.VISIBLE);
                            yourNextStepsGoHere.setVisibility(View.VISIBLE);
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
        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    public void RemoveCheckOut(String userId, String talkId, String nextStepId) {

        HashMap<String, String> params = new HashMap<>();

        params.put("userId", userId);
        params.put("talkId", talkId);
        params.put("nextStepId", nextStepId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, removeNextStepUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("ResdeletedNextSteps", response.toString());
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

    public void RefreshLayout(final View view) {
        loopedText.post(new Runnable() {
            public void run() {
                view.setVisibility(View.GONE);
                loopedText.removeView(view);
                loopedText.invalidate();
                loopedText.forceLayout();
                loopedText.requestLayout();
                loopedText.removeAllViews();
                loopedText.refreshDrawableState();

                if(counter == 0){
                    nextImageView.setVisibility(View.VISIBLE);
                    yourNextStepsGoHere.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        MakeRequest(requestURL);
    }

    public void CheckOutCard(ArrayList<UserNextSteps> userNextStepsArrayList) {

        checkoutCard = new CustomCard(getActivity().getApplicationContext(), userNextStepsArrayList);
        CustomExpandCard expandCard = new CustomExpandCard(getActivity().getApplicationContext(), userNextStepsArrayList);
        checkoutCard.addCardExpand(expandCard);
        CardViewNative checkOutCardView = (CardViewNative) getActivity().findViewById(R.id.checkOutCard);
        ViewToClickToExpand viewToClickToExpand =
                ViewToClickToExpand.builder()
                        .highlightView(false)
                        .setupCardElement(ViewToClickToExpand.CardElementUI.THUMBNAIL);
        checkoutCard.setViewToClickToExpand(viewToClickToExpand);
        checkOutCardView.setCard(checkoutCard);
    }


    class CustomCard extends it.gmariotti.cardslib.library.internal.Card {

        public ArrayList<UserNextSteps> nextStepsList;
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        public CustomCard(Context context, ArrayList<UserNextSteps> nextStepsList) {
            super(context, R.layout.card_innder_expand);
            this.nextStepsList = nextStepsList;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            loopedText = (LinearLayout) view.findViewById(R.id.loopedText);
            loopedText.refreshDrawableState();
            LinearLayout topView, bottomView;

            for (int i = 0; i < nextStepsList.size(); i++) {
                final View mView = inflater.inflate(R.layout.profile_single_next_steps, null);
                topView = (LinearLayout) mView.findViewById(R.id.top_view);
                bottomView = (LinearLayout) mView.findViewById(R.id.bottom_view);

                mView.setTag(i);

                TextView title = (TextView) mView.findViewById(R.id.nextStepsTitle);
                title.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));
                title.setText(nextStepsList.get(i).nextStepObject.optJSONObject("nextStep").optString("action"));

                TextView url = (TextView) mView.findViewById(R.id.nextStepsUrl);
                url.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));
                url.setText(nextStepsList.get(i).nextStepObject.optJSONObject("nextStep").optString("url"));

                loopedText.addView(mView);
                counter = loopedText.getChildCount()+1;

                topView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("TopviewClicked", String.valueOf(mView.getParent()));
                    }
                });

                bottomView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String talkId = nextStepsList.get((Integer) mView.getTag()).nextStepObject.optString("talkId");
                        String nextStepsId = nextStepsList.get((Integer) mView.getTag()).nextStepObject.optString("nextStepId");

//                        RemoveCheckOut(userID, talkId, nextStepsId);
                        RefreshLayout(mView);

                        nextStepsList.remove(mView.getTag());
                        counter = counter -1;
                    }
                });
            }

            loopedText.setBackgroundResource(android.R.color.transparent);
            loopedText.setPadding(20, 0, 20, 0);
        }

    }


    class CustomExpandCard extends CardExpand {

        public ArrayList<UserNextSteps> nextStepsList;
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        public CustomExpandCard(Context context, ArrayList<UserNextSteps> nextStepsList) {
            super(context, R.layout.card_innder_expand);
            this.nextStepsList = nextStepsList;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            LinearLayout top_view = (LinearLayout) view.findViewById(R.id.loopedText);

//            View mView = inflater.inflate(R.layout.profile_single_next_steps, null);
//            TextView title = (TextView) mView.findViewById(R.id.nextStepsTitle);
//            title.setText(nextStepsList.get(1).nextStepObject.optString("nextStepId"));
//            top_view.addView(mView);

            top_view.setBackgroundResource(android.R.color.holo_red_dark);
            top_view.setPadding(20, 0, 20, 0);
        }
    }


}

class UserNextSteps {
    JSONObject nextStepObject;

    public UserNextSteps(JSONObject nextStepObject) {
        this.nextStepObject = nextStepObject;
    }
}




