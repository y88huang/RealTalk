package com.example.realtalk.realtalk;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardViewNative;

import static com.example.realtalk.realtalk.Utility.KillApplicationDialog;
import static com.example.realtalk.realtalk.Utility.OpenThisLink;
import static com.example.realtalk.realtalk.Utility.hidePDialog;
import static com.example.realtalk.realtalk.Utility.isNetworkStatusAvailable;

public class RealTalkFragment extends Fragment {

    TextView txtTalkTitle, description, location, link,
            inBriefTitle, inBriefList, inSightsTitle,
            avgSalaryTitle, avgSalary, enoughToTitle, enoughTo,
            forcastedIndustryGrowth, highSchoolTitle,
            afterHighSchoolTitle, workTitle, wikiPediaTitle, wikiReadMore,
            twitterID, recommendTalkTitle, relatedTalkTitle, relatedTalkContentTitle,
            relatedTalkContentDescription, relatedTalkCat1, relatedTalkCat2, relatedTalkCat3;

    ImageButton btnGrowth, expandHighSchool, expandAfterHighSchool,
            btnWorkExpand, btnWikiPediaExpand;

    ImageView iconLink;
    RelativeLayout relatedTalkLayout,twitterCard;

    NetworkImageView imgHeader, imgAvatar, imgRelatedTalk;
    ImageLoader imgLoader;

    ProgressDialog progressDialog;
    String getTalkById, bookMarkId, wikiUrl,twitterId,prefFile,shortUrl;

    static String specificId, relatedTalkId;
    static ImageButton btnRecomLike, btnShare, btnRecomBookmark;

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
        prefFile = getResources().getString(R.string.tlpSharedPreference);

        if (!isNetworkStatusAvailable(this.getActivity().getApplicationContext())) {
            KillApplicationDialog(getString(R.string.connectionError), this.getActivity());
        }

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        imgHeader = (NetworkImageView) getActivity().findViewById(R.id.imgHeader);
        imgAvatar = (NetworkImageView) getActivity().findViewById(R.id.imgAvatar);

        txtTalkTitle = (TextView) getActivity().findViewById(R.id.txtTalkTitle);
        txtTalkTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        description = (TextView) getActivity().findViewById(R.id.description);
        description.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.OpenSansRegular));

        location = (TextView) getActivity().findViewById(R.id.location);
        location.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));

        iconLink = (ImageView) getActivity().findViewById(R.id.iconLink);

        link = (TextView) getActivity().findViewById(R.id.link);
        link.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.OpenThisLink(getActivity(), link.getText().toString());
            }
        });

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

        btnGrowth = (ImageButton) getActivity().findViewById(R.id.btnGrowth);

        highSchoolTitle = (TextView) getActivity().findViewById(R.id.highSchoolTitle);
        highSchoolTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        afterHighSchoolTitle = (TextView) getActivity().findViewById(R.id.afterHighSchoolTitle);
        afterHighSchoolTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        workTitle = (TextView) getActivity().findViewById(R.id.workTitle);
        workTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        wikiPediaTitle = (TextView) getActivity().findViewById(R.id.wikiPediaTitle);
        wikiPediaTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        wikiReadMore = (TextView) getActivity().findViewById(R.id.wikiReadMore);
        wikiReadMore.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));

        twitterID = (TextView) getActivity().findViewById(R.id.twitterID);
        twitterID.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        twitterCard = (RelativeLayout) getActivity().findViewById(R.id.twitterCard);
        twitterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String twitterUrl = "https://twitter.com/"+ twitterId;
                Utility.OpenThisLink(getActivity(), twitterUrl);
            }
        });

        recommendTalkTitle = (TextView) getActivity().findViewById(R.id.recommendTalkTitle);
        recommendTalkTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        relatedTalkLayout = (RelativeLayout) getActivity().findViewById(R.id.relatedTalkLayout);
        relatedTalkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("relatedTalkId",relatedTalkId);
                Intent intent = new Intent(getActivity(), RealTalk.class);
                intent.putExtra("talkID", relatedTalkId);
                getActivity().startActivity(intent);
            }
        });
        relatedTalkTitle = (TextView) getActivity().findViewById(R.id.relatedTalkTitle);
        relatedTalkTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        relatedTalkContentTitle = (TextView) getActivity().findViewById(R.id.relatedTalkContentTitle);
        relatedTalkContentTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        relatedTalkContentDescription = (TextView) getActivity().findViewById(R.id.relatedTalkContentDescription);
        relatedTalkContentDescription.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.OpenSansRegular));

        relatedTalkCat1 = (TextView) getActivity().findViewById(R.id.relatedTalkCat1);
        relatedTalkCat1.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.OpenSansRegular));

        relatedTalkCat2 = (TextView) getActivity().findViewById(R.id.relatedTalkCat2);
        relatedTalkCat2.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.OpenSansRegular));

        relatedTalkCat3 = (TextView) getActivity().findViewById(R.id.relatedTalkCat3);
        relatedTalkCat3.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.OpenSansRegular));

        expandHighSchool = (ImageButton) getActivity().findViewById(R.id.expandHighSchool);
        expandAfterHighSchool = (ImageButton) getActivity().findViewById(R.id.expandAfterHighSchool);
        btnWorkExpand = (ImageButton) getActivity().findViewById(R.id.expandWork);
        btnWikiPediaExpand = (ImageButton) getActivity().findViewById(R.id.expandWikiPedia);

        btnRecomLike = (ImageButton) getActivity().findViewById(R.id.btnRecomLike);
        btnShare = (ImageButton) getActivity().findViewById(R.id.btnRecomShare);
        btnRecomBookmark = (ImageButton) getActivity().findViewById(R.id.btnRecomBookmark);

        btnRecomLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRecomLike.setBackgroundResource(R.drawable.iconheart_filled);
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] dialogItem = {"Facebook", "Twitter", "Email"};
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Share with");
                alertDialogBuilder
                        .setCancelable(true)
                        .setIcon(R.mipmap.ic_launcher)
                        .setSingleChoiceItems(dialogItem, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int pos) {
                                if (pos == 0) {
                                    FacebookShare();
                                } else if (pos == 1) {
                                    TwitterShare();
                                } else if (pos == 2) {
                                    EmailShare();
                                }
                                dialog1.cancel();
                            }
                        })
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            @SuppressLint("NewApi")
                            public void onClick(DialogInterface dialog1, int id) {
                                dialog1.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });
        btnRecomBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(prefFile, Context.MODE_PRIVATE);
                String userID = sharedPreferences.getString("userID", "");
                String facebookId = sharedPreferences.getString("facebookId", "");

                if (userID.isEmpty() && facebookId.isEmpty()) {
                    Intent intent = new Intent(getActivity(), Authentication.class);
                    getActivity().startActivity(intent);
                } else {
                    final HashMap<String, String> params = new HashMap<>();
                    params.put("userId", userID);
                    params.put("talkId", bookMarkId);

                    String requestURL = getActivity().getResources().getString(R.string.serverURL) + "api/user/addBookmarkToUser";

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    btnRecomBookmark.setBackgroundResource(R.drawable.iconbookmark_filled);
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
        imgRelatedTalk = (NetworkImageView) getActivity().findViewById(R.id.imgRelatedTalk);

        //specific id being retrieved from homeScreen on list item click event.
        specificId = getActivity().getIntent().getExtras().getString("talkID"); //"561ea161e59615005e000003"
        Log.v("SpecificTalkID", specificId);

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

                        int industryGrowth = data.optJSONObject("insights").optInt("industryGrowth");
                        shortUrl = data.optString("shortUrl");

                        JSONArray highSchoolQuesAns = data.optJSONArray("questions").optJSONObject(0).optJSONArray("answers");
                        JSONArray afterHighSchoolQuesAns = data.optJSONArray("questions").optJSONObject(1).optJSONArray("answers");
                        JSONArray workQuesAns = data.optJSONArray("questions").optJSONObject(2).optJSONArray("answers");

                        String imgHeaderUrl = data.optString("imageUrl");
                        String imgAvatarUrl = data.optString("profileUrl");
                        twitterId = data.optString("twitter");

                        String imgRelatedTalkUrl = data.optJSONObject("relatedTalk").optString("imageUrl");
                        String sRelatedTalkTitle = data.optJSONObject("relatedTalk").optString("title");
                        String sRelatedTalkDescription = data.optJSONObject("relatedTalk").optString("tagline");
                        String sCategory1 = data.optJSONObject("relatedTalk").optJSONArray("categories").optJSONObject(0).optString("title");
                        String sCategory2 = data.optJSONObject("relatedTalk").optJSONArray("categories").optJSONObject(1).optString("title");
                        String sCategory3 = data.optJSONObject("relatedTalk").optJSONArray("categories").optJSONObject(2).optString("title");

                        relatedTalkId = data.optJSONObject("relatedTalk").optString("_id");
                        bookMarkId = data.optString("_id");

                        imgHeader.setImageUrl(imgHeaderUrl, imgLoader);
                        imgAvatar.setImageUrl(imgAvatarUrl, imgLoader);

                        txtTalkTitle.setText(data.optString("title"));
                        description.setText(data.optString("description"));
                        location.setText(data.optString("location"));

                        if (urls.optString(0) == "" || urls.optString(0).isEmpty()) {
                            iconLink.setVisibility(View.GONE);
                            NextStepsFragment.nextIconLink.setVisibility(View.GONE);
                        } else {
                            link.setText(urls.optString(0));
                        }

                        for (int i = 0; i < inBriefArray.length(); i++) {
                            inBriefList.setText(inBriefArray.optString(i) + "\n\n");
                        }

                        avgSalary.setText(data.optJSONObject("insights").optString("salaryRange"));

                        for (int i = 0; i < enoughToArray.length(); i++) {
                            enoughTo.append(Html.fromHtml("&#8226;&nbsp;&nbsp;&nbsp;" + enoughToArray.optString(i) + "<br/>"));
                        }

                        if (industryGrowth > 50) {
                            btnGrowth.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.icongrowth));
                        } else {
                            btnGrowth.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.icondown));
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

                        String wikiTitle = data.optString("wikipediaTxt");
                        wikiPediaContent.add(new QuestionAnswer(wikiTitle, ""));
                        WikiPediaCard(wikiPediaContent);
                        wikiUrl = data.optString("wikipediaUrl");

                        if(data.optString("twitter").isEmpty()){
                            twitterCard.setVisibility(View.GONE);
                        }else{
                            twitterCard.setVisibility(View.VISIBLE);
                            twitterID.setText("FOLLOW " + twitterId);
                        }

                        imgRelatedTalk.setImageUrl(imgRelatedTalkUrl, imgLoader);

                        relatedTalkContentTitle.setText(sRelatedTalkTitle);
                        relatedTalkContentDescription.setText(sRelatedTalkDescription);
                        relatedTalkCat1.setText(sCategory1);
                        relatedTalkCat2.setText(sCategory2);
                        relatedTalkCat3.setText(sCategory3);

//                      Set next teps text from this fragment
                        NextStepsFragment.txtTalkTitle.setText(txtTalkTitle.getText());
                        NextStepsFragment.description.setText(description.getText());
                        NextStepsFragment.location.setText(location.getText());
                        NextStepsFragment.link.setText(link.getText());
                        NextStepsFragment.nextImageHeader.setImageUrl(imgHeaderUrl, imgLoader);
                        NextStepsFragment.nextAvatarImg.setImageUrl(imgAvatarUrl, imgLoader);

                        NextStepsFragment.nextRelatedTalkContentTitle.setText(sRelatedTalkTitle);
                        NextStepsFragment.nextRelatedTalkContentDescription.setText(sRelatedTalkDescription);
                        NextStepsFragment.nextCat1.setText(sCategory1);
                        NextStepsFragment.nextCat2.setText(sCategory2);
                        NextStepsFragment.nextCat3.setText(sCategory3);
                        NextStepsFragment.nextImgRelatedTalk.setImageUrl(imgRelatedTalkUrl, imgLoader);

                        hidePDialog(progressDialog, 300);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Error", "Error: " + error.getMessage());
                        Utility.hidePDialog(progressDialog, 800);
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

        wikiReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenThisLink(getActivity(), wikiUrl);
            }
        });

        expandHighSchool.setScaleY(-1);
        expandAfterHighSchool.setScaleY(-1);
        btnWorkExpand.setScaleY(-1);

        expandHighSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (expandHighSchool.getScaleY() == -1f) {
                    expandHighSchool.setScaleY(1f);
                    highSchoolCard.doCollapse();
                } else {
                    expandHighSchool.setScaleY(-1f);
                    highSchoolCard.doExpand();
                }
            }
        });

        expandAfterHighSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //HighSchool
                if (expandAfterHighSchool.getScaleY() == -1f) {
                    expandAfterHighSchool.setScaleY(1f);
                    afterHeighSchoolCard.doCollapse();
                } else {
                    expandAfterHighSchool.setScaleY(-1f);
                    afterHeighSchoolCard.doExpand();

                }
            }
        });

        btnWorkExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //HighSchool
                if (btnWorkExpand.getScaleY() == -1f) {
                    btnWorkExpand.setScaleY(1f);
                    workCard.doCollapse();
                } else {
                    btnWorkExpand.setScaleY(-1f);
                    workCard.doExpand();
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
        highSchoolCard.doExpand();
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
        afterHeighSchoolCard.doExpand();
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
        wikiPediaCard = new CustomCard(getActivity(), wikiPediaContent, true);
        CustomExpandCard wikiPediaCardExpand = new CustomExpandCard(getActivity(), wikiPediaContent, true);
        wikiPediaCard.addCardExpand(wikiPediaCardExpand);
        CardViewNative wikiPediaCardView = (CardViewNative) getActivity().findViewById(R.id.wikiPediaCard);
        final ViewToClickToExpand wikiPediaCardExpandEvent =
                ViewToClickToExpand.builder()
                        .highlightView(false)
                        .setupCardElement(ViewToClickToExpand.CardElementUI.THUMBNAIL);
        wikiPediaCard.setViewToClickToExpand(wikiPediaCardExpandEvent);
        wikiPediaCardView.setCard(wikiPediaCard);
        workCard.doExpand();
    }

    @Override
    public void onResume() {
        super.onResume();

        //this helps not appending again on resume activity.
        enoughTo.setText("");

        if (!isNetworkStatusAvailable(this.getActivity().getApplicationContext())) {
            KillApplicationDialog(getString(R.string.connectionError), this.getActivity());
        }
    }


    public void FacebookShare() {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareDialog shareDialog = new ShareDialog(getActivity());
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(txtTalkTitle.getText().toString())
                    .setContentDescription(description.getText().toString())
                    .setImageUrl(Uri.parse(imgHeader.getImageURL()))
                    .setContentUrl(Uri.parse(getResources().getString(R.string.talkDetailsWebConnection) + shortUrl))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    public void TwitterShare() {
        String tweetUrl = "https://twitter.com/intent/tweet?text=" + txtTalkTitle.getText();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

        List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }
        getActivity().startActivity(intent);
    }

    public void EmailShare() {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        send.setType("*/*");
        String uriText = "mailto:" + Uri.encode("") +
                "?subject=" + Uri.encode("RealTalk -" + txtTalkTitle.getText().toString()) +
                "&body=" + Uri.encode(description.getText().toString()) + "\n";

        Uri uri = Uri.parse(uriText);
        send.setData(uri);
        getActivity().startActivity(Intent.createChooser(send, "Send mail..."));
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

    public CustomCard(Context context, ArrayList<QuestionAnswer> hsQAList, Boolean isWiki) {
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

        if (isWikipedia) {
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

    public CustomExpandCard(Context context, ArrayList<QuestionAnswer> hsQAList, Boolean isWiki) {
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


