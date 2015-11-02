package com.example.realtalk.realtalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
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
    ImageView nextImageView, btnCompleteNextStep;
    SharedPreferences sharedPreferences;
    CustomCard checkoutCard;
    String userID, requestURL, removeNextStepUrl, completedNextStepUrl, prefFile;
    ArrayList<UserNextSteps> userNextStepsArrayList, userCompletedNextSteps;
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

        prefFile = getActivity().getResources().getString(R.string.tlpSharedPreference);
        sharedPreferences = getActivity().getSharedPreferences(prefFile, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", null);

        requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/getAllNextSteps";
        removeNextStepUrl = getActivity().getResources().getString(R.string.serverURL) + "api/user/removeNextStepFromUser";
        completedNextStepUrl = getActivity().getResources().getString(R.string.serverURL) + "api/user/completedNextStep";

        yourNextStepsGoHere = (TextView) getActivity().findViewById(R.id.yourNextStepsHere);
        yourNextStepsGoHere.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        txtCheckout = (TextView) getActivity().findViewById(R.id.txtCheckout);
        txtCheckout.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        nextImageView = (ImageView) getActivity().findViewById(R.id.nextImageView);

        MakeRequest(requestURL);

        userNextStepsArrayList = new ArrayList<>();
        userCompletedNextSteps = new ArrayList<>();
    }


    public void MakeRequest(String requestURL) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userID);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        userNextStepsArrayList.clear();
                        userCompletedNextSteps.clear();
                        JSONArray data = response.optJSONArray("data");

                        Log.v("Next Steps", data.toString());
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.optJSONObject(i);

                            Log.v("CompleteOrNot", object.optString("completed"));

                            if (object.optString("completed") == "false") {
                                userNextStepsArrayList.add(new UserNextSteps(object));
                            } else if (object.optString("completed") == "true") {
                                userCompletedNextSteps.add(new UserNextSteps(object));
                            }
                        }

                        CheckOutCard(userNextStepsArrayList);

                        if (userNextStepsArrayList.size() > 0 || userCompletedNextSteps.size() > 0) {
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

    public void AddCompletedNextSteps(String userId, String talkId, String nextStepId) {

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("talkId", talkId);
        params.put("nextStepId", nextStepId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, completedNextStepUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("CompletedNextSteps", response.toString());
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
                ((ViewManager)view.getParent()).removeView(view);
//                loopedText.getChildAt((Integer)view.getTag()).setVisibility(View.GONE);
                loopedText.forceLayout();
                loopedText.requestLayout();
                loopedText.removeAllViews();
                loopedText.refreshDrawableState();

                if (counter == 0) {
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
        CustomExpandCard expandCard = new CustomExpandCard(getActivity().getApplicationContext(), userCompletedNextSteps);
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

            if (nextStepsList != null && nextStepsList.size() > 0) {
                for (int i = 0; i < nextStepsList.size(); i++) {
                    final View mView = inflater.inflate(R.layout.profile_single_next_steps, null);
                    topView = (LinearLayout) mView.findViewById(R.id.top_view);
                    bottomView = (LinearLayout) mView.findViewById(R.id.bottom_view);

                    mView.setTag(i);

                    TextView title = (TextView) mView.findViewById(R.id.nextStepsTitle);
                    title.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));
                    title.setText(nextStepsList.get(i).nextStepObject.optJSONObject("nextStep").optString("action"));

                    final TextView url = (TextView) mView.findViewById(R.id.nextStepsUrl);
                    url.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));
                    url.setText(nextStepsList.get(i).nextStepObject.optJSONObject("nextStep").optString("url"));

                    loopedText.addView(mView);
                    counter = loopedText.getChildCount() + 1;

                    btnCompleteNextStep = (ImageView) mView.findViewById(R.id.btnCompletedNextSteps);
                    btnCompleteNextStep.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.iconnotcomplete, null));

                    topView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sUrl = url.getText().toString();
                            Utility.OpenThisLink(getActivity(), sUrl);
                        }
                    });

                    btnCompleteNextStep.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v("Completed clicked", String.valueOf(mView.findViewById(R.id.btnCompletedNextSteps)));
                            String talkId = nextStepsList.get((Integer) mView.getTag()).nextStepObject.optString("talkId");
                            String nextStepsId = nextStepsList.get((Integer) mView.getTag()).nextStepObject.optString("nextStepId");
                            AddCompletedNextSteps(userID, talkId, nextStepsId);
                        }
                    });


                    bottomView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String talkId = nextStepsList.get((Integer) mView.getTag()).nextStepObject.optString("talkId");
                            String nextStepsId = nextStepsList.get((Integer) mView.getTag()).nextStepObject.optString("nextStepId");

                            RemoveCheckOut(userID, talkId, nextStepsId);
                            RefreshLayout(mView);

                            nextStepsList.remove(mView.getTag());
                            counter = counter - 1;
                        }
                    });
                }
            }

            TextView t = new TextView(getActivity());
            t.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            t.setPadding(15, 20, 15, 20);
            loopedText.setGravity(Gravity.CENTER_HORIZONTAL);
            t.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));
            t.setText(userCompletedNextSteps.size() + " " + getResources().getString(R.string.completedNextStep));
            t.setGravity(Gravity.CENTER);
            t.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.completed_next_step_background));
            t.setTextAppearance(getActivity(), R.style.completedNextSteps);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkoutCard.doToogleExpand();
                }
            });

            if (userCompletedNextSteps.size() <= 0) {
                t.setVisibility(View.GONE);
            } else {
                t.setVisibility(View.VISIBLE);
            }

            loopedText.addView(t);
            loopedText.setBackgroundResource(android.R.color.transparent);
            loopedText.setPadding(20, 0, 20, 20);
        }
    }


    class CustomExpandCard extends CardExpand {

        public ArrayList<UserNextSteps> completedNextSteps;
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        public CustomExpandCard(Context context, ArrayList<UserNextSteps> userCompletedNextSteps) {
            super(context, R.layout.card_innder_expand);
            this.completedNextSteps = userCompletedNextSteps;
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {
            LinearLayout topView, bottomView;

            loopedText = (LinearLayout) view.findViewById(R.id.loopedText);

            if (completedNextSteps != null) {
                for (int i = 0; i < completedNextSteps.size(); i++) {
                    final View mView = inflater.inflate(R.layout.profile_single_next_steps, null);
                    topView = (LinearLayout) mView.findViewById(R.id.top_view);
                    bottomView = (LinearLayout) mView.findViewById(R.id.bottom_view);

                    TextView title = (TextView) mView.findViewById(R.id.nextStepsTitle);
                    title.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));
                    title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    title.setText(completedNextSteps.get(i).nextStepObject.optJSONObject("nextStep").optString("action"));

                    final TextView url = (TextView) mView.findViewById(R.id.nextStepsUrl);
                    url.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));
                    url.setText(completedNextSteps.get(i).nextStepObject.optJSONObject("nextStep").optString("url"));

                    mView.setTag(i);
                    loopedText.addView(mView);

                    btnCompleteNextStep = (ImageView) mView.findViewById(R.id.btnCompletedNextSteps);
                    btnCompleteNextStep.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.iconcheckmark_blue, null));

                    topView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String sUrl = url.getText().toString();
                            Utility.OpenThisLink(getActivity(),sUrl);
                        }
                    });

                    bottomView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String talkId = completedNextSteps.get((Integer) mView.getTag()).nextStepObject.optString("talkId");
                            String nextStepsId = completedNextSteps.get((Integer) mView.getTag()).nextStepObject.optString("nextStepId");

                            RemoveCheckOut(userID, talkId, nextStepsId);
                            RefreshLayout(mView);

                            completedNextSteps.remove(mView.getTag());
                            counter = counter - 1;
                        }
                    });

                }
            }
            loopedText.setBackgroundColor(getResources().getColor(R.color.transparent));
            parent.setBackgroundColor(getResources().getColor(R.color.transparent));
            loopedText.setPadding(20, 0, 20, 0);
        }
    }


}

class UserNextSteps {
    JSONObject nextStepObject;

    public UserNextSteps(JSONObject nextStepObject) {
        this.nextStepObject = nextStepObject;
    }
}




