package com.example.realtalk.realtalk;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.sax.RootElement;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.realtalk.realtalk.Utility.KillApplicationDialog;
import static com.example.realtalk.realtalk.Utility.hidePDialog;
import static com.example.realtalk.realtalk.Utility.isNetworkStatusAvailable;

public class RealTalkFragment extends Fragment {

    RelativeLayout relativeLayout;
    TextView title,description,location,link,
            inBriefTitle,inBriefList,inSightsTitle,
            avgSalaryTitle,avgSalary, enoughToTitle,enoughTo,
            forcastedIndustryGrowth,highSchoolTitle;
    ImageButton btnGrowthUp,btnGrowthDown;
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

        if(!isNetworkStatusAvailable(this.getActivity().getApplicationContext())){
            KillApplicationDialog(getString(R.string.connectionError), this.getActivity());
        }

        progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        relativeLayout = (RelativeLayout)getActivity().findViewById(R.id.relativeLayout);

        title = (TextView)getActivity().findViewById(R.id.title);
        title.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        description = (TextView)getActivity().findViewById(R.id.description);
        description.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.OpenSansRegular));

        location = (TextView)getActivity().findViewById(R.id.location);
        location.setTypeface(FontManager.setFont(getActivity().getApplicationContext(),FontManager.Font.MontSerratRegular));

        link = (TextView)getActivity().findViewById(R.id.link);
        link.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));

        inBriefTitle = (TextView)getActivity().findViewById(R.id.inBriefTitle);
        inBriefTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));
        inBriefTitle.setText(getActivity().getResources().getString(R.string.inBriefTitle));

        inBriefList = (TextView)getActivity().findViewById(R.id.inBriefList);
        inBriefList.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratRegular));

        inSightsTitle = (TextView)getActivity().findViewById(R.id.inSightsTitle);
        inSightsTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));
        inSightsTitle.setText(getActivity().getResources().getString(R.string.inSightsTitle));

        avgSalaryTitle =(TextView)getActivity().findViewById(R.id.avgSalaryTitle);
        avgSalaryTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        avgSalary = (TextView)getActivity().findViewById(R.id.avgSalary);
        avgSalary.setTypeface(FontManager.setFont(getActivity().getApplicationContext(),FontManager.Font.JustAnotherHandRegular));

        enoughToTitle = (TextView)getActivity().findViewById(R.id.enoughToTitle);
        enoughToTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.MontSerratBold));

        enoughTo = (TextView)getActivity().findViewById(R.id.enoughTo);
        enoughTo.setTypeface(FontManager.setFont(getActivity().getApplicationContext(),FontManager.Font.MontSerratRegular));

        forcastedIndustryGrowth = (TextView)getActivity().findViewById(R.id.forcastedIndustryGrowth);
        forcastedIndustryGrowth.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));

        btnGrowthUp = (ImageButton)getActivity().findViewById(R.id.btnGrowthUp);
        btnGrowthUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Growth Up Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnGrowthDown = (ImageButton)getActivity().findViewById(R.id.btnGrowthDown);
        btnGrowthDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Growth Down Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        highSchoolTitle = (TextView)getActivity().findViewById(R.id.highSchoolTitle);
        highSchoolTitle.setTypeface(FontManager.setFont(getActivity().getApplicationContext(), FontManager.Font.JustAnotherHandRegular));


        ArrayList<String> nums = new ArrayList<String>();
        nums.add("One");
        nums.add("Two");
        nums.add("Three");
        nums.add("Four");
        nums.add("Four");
        nums.add("Four");
        nums.add("Four");
        nums.add("Four");
        nums.add(2, "Two and a half");

        LinearLayout linearLayout = (LinearLayout)getActivity().findViewById(R.id.myLinearLayout);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        int index = 0;
        for (String item : nums) {
            index++;
            View view  = inflater.inflate(R.layout.question_answer, linearLayout, false);
            view.setTag(item.indexOf(index));
            // set item content in view
            TextView t = (TextView)view.findViewById(R.id.question);
            t.setText(item);

            Button btn =(Button)view.findViewById(R.id.mybtn);
            btn.setText(item);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = (int) v.getTag();
                    System.out.println(id);
                    Toast.makeText(getActivity(),id, Toast.LENGTH_SHORT).show();
                }
            });

            linearLayout.addView(view);
        }



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
                            JSONArray enoughToArray = data.optJSONObject("insights").optJSONArray("enoughTo");

                            title.setText(data.optString("title"));
                            description.setText(data.optString("description"));
                            location.setText(data.optString("location"));
                            link.setText(urls.optString(0));

                            for(int i = 0;i<inBriefArray.length();i++){
                                inBriefList.append(inBriefArray.optString(i)+"\n\n");
                            }

                            for(int i = 0;i<enoughToArray.length();i++){
                                enoughTo.append(Html.fromHtml("&#8226;&nbsp;&nbsp;&nbsp;"+enoughToArray.optString(i)+"<br/>"));
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

        if(!isNetworkStatusAvailable(this.getActivity().getApplicationContext())){
            KillApplicationDialog(getString(R.string.connectionError), this.getActivity());
        }
    }

    public class QuestionAnswer extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
