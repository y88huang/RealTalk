package com.example.realtalk.realtalk;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardViewNative;

import static com.example.realtalk.realtalk.Utility.KillApplicationDialog;
import static com.example.realtalk.realtalk.Utility.hidePDialog;
import static com.example.realtalk.realtalk.Utility.isNetworkStatusAvailable;

public class RealTalkFragment extends Fragment {

    TextView author, description, location, link,
            inBriefTitle, inBriefList, inSightsTitle,
            avgSalaryTitle, avgSalary, enoughToTitle, enoughTo,
            forcastedIndustryGrowth, highSchoolTitle,
            afterHighSchoolTitle, workTitle, wikiPediaTitle,
            twitterID,recommendTalkTitle,relatedTalkTitle,relatedTalkContent,
            relatedTalkReadMore;

    ImageButton btnGrowthUp, btnGrowthDown, expandHighSchool, expandAfterHighSchool,
            btnWorkExpand, btnWikiPediaExpand,btnRecomLike,btnShare,btnRecomBookmark;

    NetworkImageView imgHeader,imgAvatar,imgRelatedTalk;
    ImageLoader imgLoader;

    ProgressDialog progressDialog;
    String getTalkById,bookMarkId;

    public static CustomCard highSchoolCard, afterHeighSchoolCard, workCard, wikiPediaCard;
    ArrayList<QuestionAnswer> hsQuestionAnsList, ahsQuestionAnsList, workQestionAnsList, wikiPediaContent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_real_talk, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        getTalkById = getActivity().getResources().getString(R.string.serverURL) + "api/talk/getTalkById";

        if (!isNetworkStatusAvailable(this.getActivity().getApplicationContext())) {
            KillApplicationDialog(getString(R.string.connectionError), this.getActivity());
        }

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        imgHeader = (NetworkImageView)getActivity().findViewById(R.id.imgHeader);
        imgAvatar =(NetworkImageView)getActivity().findViewById(R.id.imgAvatar);

        author = (TextView) getActivity().findViewById(R.id.author);
        author.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        description = (TextView) getActivity().findViewById(R.id.description);
        description.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.OpenSansRegular));

        location = (TextView) getActivity().findViewById(R.id.location);
        location.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));

        link = (TextView) getActivity().findViewById(R.id.link);
        link.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));

        inBriefTitle = (TextView) getActivity().findViewById(R.id.inBriefTitle);
        inBriefTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));
        inBriefTitle.setText(getActivity().getResources().getString(R.string.inBriefTitle));

        inBriefList = (TextView) getActivity().findViewById(R.id.inBriefList);
        inBriefList.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));

        inSightsTitle = (TextView) getActivity().findViewById(R.id.inSightsTitle);
        inSightsTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));
        inSightsTitle.setText(getActivity().getResources().getString(R.string.inSightsTitle));

        avgSalaryTitle = (TextView) getActivity().findViewById(R.id.avgSalaryTitle);
        avgSalaryTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        avgSalary = (TextView) getActivity().findViewById(R.id.avgSalary);
        avgSalary.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        enoughToTitle = (TextView) getActivity().findViewById(R.id.enoughToTitle);
        enoughToTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        enoughTo = (TextView) getActivity().findViewById(R.id.enoughTo);
        enoughTo.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));

        forcastedIndustryGrowth = (TextView) getActivity().findViewById(R.id.forcastedIndustryGrowth);
        forcastedIndustryGrowth.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        btnGrowthUp = (ImageButton) getActivity().findViewById(R.id.btnGrowthUp);
        btnGrowthUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Growth Up Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnGrowthDown = (ImageButton) getActivity().findViewById(R.id.btnGrowthDown);
        btnGrowthDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Growth Down Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        highSchoolTitle = (TextView) getActivity().findViewById(R.id.highSchoolTitle);
        highSchoolTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        afterHighSchoolTitle = (TextView) getActivity().findViewById(R.id.afterHighSchoolTitle);
        afterHighSchoolTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        workTitle = (TextView) getActivity().findViewById(R.id.workTitle);
        workTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        wikiPediaTitle = (TextView) getActivity().findViewById(R.id.wikiPediaTitle);
        wikiPediaTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        twitterID = (TextView)getActivity().findViewById(R.id.twitterID);
        twitterID.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        recommendTalkTitle = (TextView)getActivity().findViewById(R.id.recommendTalkTitle);
        recommendTalkTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        relatedTalkTitle = (TextView)getActivity().findViewById(R.id.relatedTalkTitle);
        relatedTalkTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        relatedTalkContent = (TextView)getActivity().findViewById(R.id.relatedTalkContent);
        relatedTalkContent.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));
        relatedTalkContent.setEllipsize(TextUtils.TruncateAt.END);
        relatedTalkContent.setLines(1);

        relatedTalkReadMore = (TextView)getActivity().findViewById(R.id.relatedTalkReadMore);
        relatedTalkReadMore.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));

        expandHighSchool = (ImageButton) getActivity().findViewById(R.id.expandHighSchool);
        expandAfterHighSchool = (ImageButton) getActivity().findViewById(R.id.expandAfterHighSchool);
        btnWorkExpand = (ImageButton) getActivity().findViewById(R.id.expandWork);
        btnWikiPediaExpand = (ImageButton) getActivity().findViewById(R.id.expandWikiPedia);

        btnRecomLike = (ImageButton)getActivity().findViewById(R.id.btnRecomLike);
        btnShare= (ImageButton)getActivity().findViewById(R.id.btnRecomShare);
        btnRecomBookmark = (ImageButton)getActivity().findViewById(R.id.btnRecomBookmark);

        btnRecomLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRecomLike.setBackgroundResource(R.drawable.iconheart_filled);
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(),"Share Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        btnRecomBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRecomBookmark.setBackgroundResource(R.drawable.iconbookmark_filled);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(String.valueOf(R.string.tlpSharedPreference), Context.MODE_PRIVATE);
                String userID = sharedPreferences.getString("userID", "");

                if(userID.isEmpty()){
                    Intent intent = new Intent(getActivity(),Authentication.class);
                    getActivity().startActivity(intent);
                }else{
                    final HashMap<String, String> params = new HashMap<>();
                    params.put("userId", userID);
                    params.put("talkId",bookMarkId);

                    String requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/addBookmarkToUser";

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(getActivity(), "Bookmarked", Toast.LENGTH_SHORT).show();
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
        });

        hsQuestionAnsList = new ArrayList<>();
        ahsQuestionAnsList = new ArrayList<>();
        workQestionAnsList = new ArrayList<>();
        wikiPediaContent = new ArrayList<>();

        imgLoader = new ImageLoader(VolleyApplication.getInstance().getRequestQueue(), new BitmapLru(6400));
        imgRelatedTalk = (NetworkImageView)getActivity().findViewById(R.id.imgRelatedTalk);

        //specific id being retrieved from homeScreen on list item click event.
        String specificId = getActivity().getIntent().getExtras().getString("talkID");

        //parameter being sent with body
        final HashMap<String, String> params = new HashMap<>();
        params.put("id", specificId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, getTalkById, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject data = response.optJSONObject("data");
                        JSONArray urls = data.optJSONArray("urls");
                        JSONArray inBriefArray = data.optJSONArray("inBrief");
                        JSONArray enoughToArray = data.optJSONObject("insights").optJSONArray("enoughTo");
                        JSONArray highSchoolQuesAns = data.optJSONArray("questions").optJSONObject(0).optJSONArray("answers");
                        JSONArray afterHighSchoolQuesAns = data.optJSONArray("questions").optJSONObject(1).optJSONArray("answers");
                        JSONArray workQuesAns = data.optJSONArray("questions").optJSONObject(2).optJSONArray("answers");
                        String imgHeaderUrl = data.optString("imageUrl");
                        String imgAvatarUrl = data.optString("profileUrl");
                        String imgRelatedTalkUrl = data.optJSONObject("relatedTalk").optString("imageUrl");
                        String relatedTalkDescription = data.optJSONObject("relatedTalk").optString("description");
                        bookMarkId = data.optString("_id");

                        imgHeader.setImageUrl(imgHeaderUrl,imgLoader);
                        imgAvatar.setImageUrl(imgAvatarUrl, imgLoader);

                        author.setText(data.optString("author"));
                        description.setText(data.optString("description"));
                        location.setText(data.optString("location"));
                        link.setText(urls.optString(0));

                        for (int i = 0; i < inBriefArray.length(); i++) {
                            inBriefList.append(inBriefArray.optString(i) + "\n\n");
                        }

                        avgSalary.setText(data.optJSONObject("insights").optString("salaryRange"));

                        for (int i = 0; i < enoughToArray.length(); i++) {
                            enoughTo.append(Html.fromHtml("&#8226;&nbsp;&nbsp;&nbsp;" + enoughToArray.optString(i) + "<br/>"));
                        }

                        for (int i = 0; i < highSchoolQuesAns.length(); i++) {
                            String question = highSchoolQuesAns.optJSONObject(i).optString("question");
                            String answer = highSchoolQuesAns.optJSONObject(i).optString("answer");
                            hsQuestionAnsList.add(new QuestionAnswer(question, answer));
                        }
                        HighSchoolCard(hsQuestionAnsList);

                        for (int i = 0; i < afterHighSchoolQuesAns.length(); i++) {
                            String question = afterHighSchoolQuesAns.optJSONObject(i).optString("question");
                            String answer = afterHighSchoolQuesAns.optJSONObject(i).optString("answer");
                            ahsQuestionAnsList.add(new QuestionAnswer(question, answer));
                        }
                        AfterHighSchoolCard(ahsQuestionAnsList);

                        for (int i = 0; i < workQuesAns.length(); i++) {
                            String question = workQuesAns.optJSONObject(i).optString("question");
                            String answer = workQuesAns.optJSONObject(i).optString("answer");
                            workQestionAnsList.add(new QuestionAnswer(question, answer));
                        }
                        WorkCard(workQestionAnsList);

                        String wikiTitle  = data.optString("wikipediaTxt");
                        String wikiContent  = "Content marketing is any marketing that involves the creation and sharing of media" +
                                " publishing content in order to acquire and retain customers.";
                        wikiPediaContent.add(new QuestionAnswer(wikiTitle, wikiContent));
                        WikiPediaCard(wikiPediaContent);

                        twitterID.setText("FOLLOW @" + author.getText());

                        imgRelatedTalk.setImageUrl(imgRelatedTalkUrl, imgLoader);

                        relatedTalkContent.setText(relatedTalkDescription);

                        hidePDialog(progressDialog);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "Error: " + error.getMessage());
                        Utility.hidePDialog(progressDialog);
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

        expandHighSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (expandHighSchool.getScaleY() == 1f) {
                    expandHighSchool.setScaleY(-1f);
                    highSchoolCard.doExpand();
                } else {
                    expandHighSchool.setScaleY(1f);
                    highSchoolCard.doCollapse();
                }
            }
        });

        expandAfterHighSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //HighSchool
                if (expandAfterHighSchool.getScaleY() == 1f) {
                    expandAfterHighSchool.setScaleY(-1f);
                    afterHeighSchoolCard.doExpand();
                } else {
                    expandAfterHighSchool.setScaleY(1f);
                    afterHeighSchoolCard.doCollapse();
                }
            }
        });

        btnWorkExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //HighSchool
                if (btnWorkExpand.getScaleY() == 1f) {
                    btnWorkExpand.setScaleY(-1f);
                    workCard.doExpand();
                } else {
                    btnWorkExpand.setScaleY(1f);
                    workCard.doCollapse();
                }
            }
        });

        btnWikiPediaExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnWikiPediaExpand.getScaleY() == 1f) {
                    btnWikiPediaExpand.setScaleY(-1f);
                    wikiPediaCard.doExpand();
                } else {
                    btnWikiPediaExpand.setScaleY(1f);
                    wikiPediaCard.doCollapse();
                }
            }
        });
    }

    public void HighSchoolCard(ArrayList<QuestionAnswer> hsQuestionAns) {
        highSchoolCard = new CustomCard(getActivity().getApplicationContext(), hsQuestionAns);
        CustomExpandCard expandCard = new CustomExpandCard(getActivity().getApplicationContext(), hsQuestionAns);
        highSchoolCard.addCardExpand(expandCard);
        CardViewNative hsCardView = (CardViewNative) getActivity().findViewById(R.id.highSchoolCard);
        ViewToClickToExpand viewToClickToExpand =
                ViewToClickToExpand.builder()
                        .highlightView(false)
                        .setupCardElement(ViewToClickToExpand.CardElementUI.THUMBNAIL);
        highSchoolCard.setViewToClickToExpand(viewToClickToExpand);
        hsCardView.setCard(highSchoolCard);
    }

    public void AfterHighSchoolCard(ArrayList<QuestionAnswer> ahsQuestionAns) {
        afterHeighSchoolCard = new CustomCard(getActivity(), ahsQuestionAnsList);
        CustomExpandCard afterHighSchoolexpandCard = new CustomExpandCard(getActivity().getApplicationContext(), ahsQuestionAns);
        afterHeighSchoolCard.addCardExpand(afterHighSchoolexpandCard);
        CardViewNative ahsCardView = (CardViewNative) getActivity().findViewById(R.id.afterHighSchoolCard);
        final ViewToClickToExpand afterHighSchoolExpand =
                ViewToClickToExpand.builder()
                        .highlightView(false)
                        .setupCardElement(ViewToClickToExpand.CardElementUI.THUMBNAIL);
        afterHeighSchoolCard.setViewToClickToExpand(afterHighSchoolExpand);
        ahsCardView.setCard(afterHeighSchoolCard);
    }

    public void WorkCard(ArrayList<QuestionAnswer> workQuestionAnsList) {
        workCard = new CustomCard(getActivity(), workQestionAnsList);
        CustomExpandCard workCardExpand = new CustomExpandCard(getActivity(), workQuestionAnsList);
        workCard.addCardExpand(workCardExpand);
        CardViewNative workCardView = (CardViewNative) getActivity().findViewById(R.id.workCard);
        final ViewToClickToExpand workCardExpandEvent =
                ViewToClickToExpand.builder()
                        .highlightView(false)
                        .setupCardElement(ViewToClickToExpand.CardElementUI.THUMBNAIL);
        workCard.setViewToClickToExpand(workCardExpandEvent);
        workCardView.setCard(workCard);
    }

    public void WikiPediaCard(ArrayList<QuestionAnswer> wikiPediaContent) {
        wikiPediaCard = new CustomCard(getActivity(),wikiPediaContent,true);
        CustomExpandCard wikiPediaCardExpand = new CustomExpandCard(getActivity(), wikiPediaContent,true);
        wikiPediaCard.addCardExpand(wikiPediaCardExpand);
        CardViewNative wikiPediaCardView = (CardViewNative) getActivity().findViewById(R.id.wikiPediaCard);
        final ViewToClickToExpand wikiPediaCardExpandEvent =
                ViewToClickToExpand.builder()
                        .highlightView(false)
                        .setupCardElement(ViewToClickToExpand.CardElementUI.THUMBNAIL);
        wikiPediaCard.setViewToClickToExpand(wikiPediaCardExpandEvent);
        wikiPediaCardView.setCard(wikiPediaCard);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isNetworkStatusAvailable(this.getActivity().getApplicationContext())) {
            KillApplicationDialog(getString(R.string.connectionError), this.getActivity());
        }
    }


}

class CustomCard extends Card {

    public ArrayList<QuestionAnswer> QuestionAnswers;
    public TextView answer, question;
    public boolean isWikipedia;

    public CustomCard(Context context, ArrayList<QuestionAnswer> hsQAList) {
        super(context, R.layout.card_innder_expand);
        this.QuestionAnswers = hsQAList;
    }
    public CustomCard(Context context, ArrayList<QuestionAnswer> hsQAList,Boolean isWiki) {
        super(context, R.layout.card_innder_expand);
        this.QuestionAnswers = hsQAList;
        this.isWikipedia = isWiki;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        LinearLayout loopedText = (LinearLayout) view.findViewById(R.id.loopedText);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        question = new TextView(getContext());
        question.setTextAppearance(getContext(), R.style.questionText);
        question.setTypeface(FontManager.setFont(getContext().getApplicationContext(), FontManager.Font.MontSerratBold));
        question.setText(QuestionAnswers.get(0).question);
        layoutParams.setMargins(0, 19, 0, 21);
        question.setLayoutParams(layoutParams);

        answer = new TextView(getContext());
        answer.setTag("answer");
        answer.setText(QuestionAnswers.get(0).answer);
        answer.setTextAppearance(getContext(), R.style.answerText);
        answer.setTypeface(FontManager.setFont(getContext(), FontManager.Font.OpenSansRegular));
        layoutParams.setMargins(0, 21, 0, 28);
        answer.setLayoutParams(layoutParams);

        loopedText.addView(question);
        loopedText.addView(answer);

        if(isWikipedia){
            question.setTextColor(getContext().getResources().getColor(R.color.white));
            answer.setTextColor(getContext().getResources().getColor(R.color.white));
            loopedText.setBackgroundColor(getContext().getResources().getColor(R.color.wikiPediaCardBgColor));
        }
    }

}

class CustomExpandCard extends CardExpand {

    public ArrayList<QuestionAnswer> QuestionAnswers;
    public boolean isWikipedia;

    //Use your resource ID for your inner layout
    public CustomExpandCard(Context context, ArrayList<QuestionAnswer> hsQAList) {
        super(context, R.layout.card_innder_expand);
        this.QuestionAnswers = hsQAList;
    }

    public CustomExpandCard(Context context, ArrayList<QuestionAnswer> hsQAList,Boolean isWiki) {
        super(context, R.layout.card_innder_expand);
        this.QuestionAnswers = hsQAList;
        this.isWikipedia = isWiki;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        LinearLayout loopedText = (LinearLayout) view.findViewById(R.id.loopedText);

        for (int i = 1; i < QuestionAnswers.size(); ++i) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            TextView question = new TextView(getContext());
            question.setTextAppearance(getContext(), R.style.questionText);
            question.setTypeface(FontManager.setFont(getContext().getApplicationContext(), FontManager.Font.MontSerratBold));
            question.setText(QuestionAnswers.get(i).question);
            layoutParams.setMargins(0, 19, 0, 21);
            question.setLayoutParams(layoutParams);

            TextView answer = new TextView(getContext());
            answer.setTextAppearance(getContext(), R.style.answerText);
            answer.setTypeface(FontManager.setFont(getContext(), FontManager.Font.OpenSansRegular));
            answer.setText(QuestionAnswers.get(i).answer);
            layoutParams.setMargins(0, 21, 0, 28);
            answer.setLayoutParams(layoutParams);

            loopedText.addView(question);
            loopedText.addView(answer);
        }
    }
}

class QuestionAnswer {
    String question, answer;

    public QuestionAnswer(String ques, String ans) {
        this.question = ques;
        this.answer = ans;
    }
}


