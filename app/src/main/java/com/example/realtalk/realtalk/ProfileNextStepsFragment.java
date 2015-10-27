package com.example.realtalk.realtalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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
    String userID, requestURL;
    ArrayList<UserNextSteps> userNextStepsArrayList;

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
                        JSONArray data = response.optJSONArray("data");
                        Log.v("Next Steps", data.toString());
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.optJSONObject(i);
                            userNextStepsArrayList.add(new UserNextSteps(object));
                        }
                        CheckOutCard(userNextStepsArrayList);
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

        public CustomCard(Context context, ArrayList<UserNextSteps> nextStepsList) {
            super(context, R.layout.profile_single_next_steps);
            this.nextStepsList = nextStepsList;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            TextView title = (TextView)view.findViewById(R.id.nextStepsTitle);
            title.setText(nextStepsList.get(0).nextStepObject.optString("nextStepId"));
        }
    }


    class CustomExpandCard extends CardExpand {

        public ArrayList<UserNextSteps> nextStepsList;

        public CustomExpandCard(Context context, ArrayList<UserNextSteps> nextStepsList) {
            super(context, R.layout.profile_single_next_steps);
            this.nextStepsList = nextStepsList;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            for (int i = 1; i < nextStepsList.size(); ++i) {

            }
        }
    }
}

class UserNextSteps {
    JSONObject nextStepObject;

    public UserNextSteps(JSONObject nextStepObject) {
        this.nextStepObject = nextStepObject;
    }
}




