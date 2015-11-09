package com.serindlabs.realtalk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.linearlistview.LinearListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alexgomes on 2015-09-29.
 */
public class ProfileNextStepsFragment extends Fragment {

    static TextView yourNextStepsGoHere, txtCheckout;
    static TextView txtCompletedNextStep;
    static ImageView nextImageView;
    SharedPreferences sharedPreferences;
    static String userID, completedNextStepUrl, uncompletedNextStepUrl, removeNextStepUrl;
    String requestURL, prefFile;
    static ArrayList<UserNextSteps> userNextStepsArrayList, userCompletedNextSteps;
    LinearListView top_view, bottom_view;
    static TopAdapter topAdapter;
    static BottomAdapter bottomAdapter;
    static int counter;
    static boolean isVisible;

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
        uncompletedNextStepUrl = getActivity().getResources().getString(R.string.serverURL) + "api/user/uncompleteNextStep";

        top_view = (LinearListView) getActivity().findViewById(R.id.nextStepTopView);
        bottom_view = (LinearListView) getActivity().findViewById(R.id.nextStepBottomView);
        userNextStepsArrayList = new ArrayList<>();
        userCompletedNextSteps = new ArrayList<>();

        yourNextStepsGoHere = (TextView) getActivity().findViewById(R.id.yourNextStepsHere);
        yourNextStepsGoHere.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        txtCompletedNextStep = (TextView) getActivity().findViewById(R.id.txtCompletedNextStep);
        txtCompletedNextStep.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));

        txtCheckout = (TextView) getActivity().findViewById(R.id.txtCheckout);
        txtCheckout.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        nextImageView = (ImageView) getActivity().findViewById(R.id.nextImageView);

        MakeRequest(requestURL);

        isVisible = false;
        txtCompletedNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVisible) {
                    ResizeAnimation.expand(bottom_view);
                    isVisible = true;
                } else {
                    ResizeAnimation.collapse(bottom_view);
                    isVisible = false;
                }
            }
        });
    }

    public void SetTopItem(ArrayList<UserNextSteps> item) {
        topAdapter = new TopAdapter(getActivity(), item);
        topAdapter.notifyDataSetChanged();
        top_view.setAdapter(topAdapter);
    }

    public void SetBottomItem(ArrayList<UserNextSteps> item) {
        counter = item.size();
        txtCompletedNextStep.setText(item.size() + " COMPLETED RESOURCES");
        bottomAdapter = new BottomAdapter(getActivity(), item);
        bottom_view.setAdapter(bottomAdapter);
        bottomAdapter.notifyDataSetChanged();

        if (item.size() == 0) {
            txtCompletedNextStep.setVisibility(View.GONE);
        }
        if (item.size() > 0) {
            txtCompletedNextStep.setVisibility(View.VISIBLE);
        }
    }

    public static void AddToBottom(int position, UserNextSteps item) {
        counter++;
        txtCompletedNextStep.setText(counter + " COMPLETED RESOURCES");
        userNextStepsArrayList.remove(position);
        userCompletedNextSteps.add(item);
        topAdapter.notifyDataSetChanged();
        bottomAdapter.notifyDataSetChanged();
    }

    public static void AddToTop(int position, UserNextSteps item) {
        counter--;
        txtCompletedNextStep.setText(counter + " COMPLETED RESOURCES");
        userCompletedNextSteps.remove(position);
        userNextStepsArrayList.add(item);
        topAdapter.notifyDataSetChanged();
        bottomAdapter.notifyDataSetChanged();
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

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.optJSONObject(i);

                            if (object.optString("completed") == "false") {
                                userNextStepsArrayList.add(new UserNextSteps(object));
                            } else if (object.optString("completed") == "true") {
                                userCompletedNextSteps.add(new UserNextSteps(object));
                            }
                        }
                        SetTopItem(userNextStepsArrayList);
                        SetBottomItem(userCompletedNextSteps);

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

    public static void AddCompletedNextSteps(String userId, String talkId, String nextStepId) {
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

    public static void RemoveCheckOut(String userId, String talkId, String nextStepId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("talkId", talkId);
        params.put("nextStepId", nextStepId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, removeNextStepUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("RemovedNextStepUrl", response.toString());
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

    public static void UnCompleteNextStep(String userId, String talkId, String nextStepId) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("talkId", talkId);
        params.put("nextStepId", nextStepId);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, uncompletedNextStepUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("UnCompletedNextStepUrl", response.toString());
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

    @Override
    public void onResume() {
        super.onResume();
        MakeRequest(requestURL);
    }
}

class UserNextSteps {
    JSONObject nextStepObject;

    public UserNextSteps(JSONObject nextStepObject) {
        this.nextStepObject = nextStepObject;
    }
}

class TopAdapter extends BaseAdapter {

    ArrayList<UserNextSteps> item;
    Context context;
    LinearLayout topView, bottomView;

    public TopAdapter(Context context, ArrayList<UserNextSteps> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_single_next_steps, parent, false);
        }

        convertView.setTag(position);

        topView = (LinearLayout) convertView.findViewById(R.id.top_view);
        bottomView = (LinearLayout) convertView.findViewById(R.id.bottom_view);

        TextView title = (TextView) convertView.findViewById(R.id.nextStepsTitle);
        title.setTypeface(FontManager.setFont(context, FontManager.Font.MontSerratRegular));
        title.setText(item.get(position).nextStepObject.optJSONObject("nextStep").optString("action"));

        final TextView url = (TextView) convertView.findViewById(R.id.nextStepsUrl);
        url.setTypeface(FontManager.setFont(context, FontManager.Font.OpenSansRegular));
        url.setText(item.get(position).nextStepObject.optJSONObject("nextStep").optString("url"));

        ImageView btnCompletedNextSteps = (ImageView) convertView.findViewById(R.id.btnCompletedNextSteps);
        final View finalConvertView = convertView;
        btnCompletedNextSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("test", v.toString());
                String talkId = item.get((Integer) finalConvertView.getTag()).nextStepObject.optString("talkId");
                String nextStepsId = item.get((Integer) finalConvertView.getTag()).nextStepObject.optString("nextStepId");
                ProfileNextStepsFragment.AddCompletedNextSteps(ProfileNextStepsFragment.userID, talkId, nextStepsId);
                ProfileNextStepsFragment.AddToBottom(position, item.get(position));

                if (ProfileNextStepsFragment.userCompletedNextSteps.size() > 0) {
                    ProfileNextStepsFragment.txtCompletedNextStep.setVisibility(View.VISIBLE);
                }
            }
        });

        topView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sUrl = url.getText().toString();
                if (!sUrl.startsWith("http://") && !sUrl.startsWith("https://")) {
                    sUrl = "http://" + sUrl;
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(sUrl));
                    context.startActivity(intent);
                } else if (sUrl.startsWith("http://") || sUrl.startsWith("https://")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(sUrl));
                    context.startActivity(intent);
                }
            }
        });

        final View finalConvertView1 = convertView;
        bottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String talkId = item.get((Integer) finalConvertView1.getTag()).nextStepObject.optString("talkId");
                final String nextStepsId = item.get((Integer) finalConvertView1.getTag()).nextStepObject.optString("nextStepId");

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(R.string.deleteResources);
                alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProfileNextStepsFragment.RemoveCheckOut(ProfileNextStepsFragment.userID, talkId, nextStepsId);
                        finalConvertView.setVisibility(View.GONE);
                        ProfileNextStepsFragment.userNextStepsArrayList.remove(position);
                        ProfileNextStepsFragment.topAdapter.notifyDataSetChanged();

                        if (ProfileNextStepsFragment.userCompletedNextSteps.size() == 0 && ProfileNextStepsFragment.userNextStepsArrayList.size() == 0) {
                            ProfileNextStepsFragment.yourNextStepsGoHere.setVisibility(View.VISIBLE);
                            ProfileNextStepsFragment.nextImageView.setVisibility(View.VISIBLE);
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            }
        });

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return item.size();
    }
}

class BottomAdapter extends BaseAdapter {

    ArrayList<UserNextSteps> item;
    Context context;
    LinearLayout topView, bottomView;

    public BottomAdapter(Context context, ArrayList<UserNextSteps> item) {
        this.context = context;
        this.item = item;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_single_next_steps, parent, false);
        }
        convertView.setTag(position);

        topView = (LinearLayout) convertView.findViewById(R.id.top_view);
        bottomView = (LinearLayout) convertView.findViewById(R.id.bottom_view);

        TextView title = (TextView) convertView.findViewById(R.id.nextStepsTitle);
        title.setTypeface(FontManager.setFont(context, FontManager.Font.MontSerratRegular));
        title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        title.setText(item.get(position).nextStepObject.optJSONObject("nextStep").optString("action"));

        final TextView url = (TextView) convertView.findViewById(R.id.nextStepsUrl);
        url.setTypeface(FontManager.setFont(context, FontManager.Font.OpenSansRegular));
        url.setText(item.get(position).nextStepObject.optJSONObject("nextStep").optString("url"));

        ImageView btnCompletedNextSteps = (ImageView) convertView.findViewById(R.id.btnCompletedNextSteps);
        btnCompletedNextSteps.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.iconcheckmark_blue, null));

        final View finalConvertView = convertView;
        btnCompletedNextSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("test", v.toString());
                String talkId = item.get((Integer) finalConvertView.getTag()).nextStepObject.optString("talkId");
                String nextStepsId = item.get((Integer) finalConvertView.getTag()).nextStepObject.optString("nextStepId");
                ProfileNextStepsFragment.UnCompleteNextStep(ProfileNextStepsFragment.userID, talkId, nextStepsId);
                ProfileNextStepsFragment.AddToTop(position, item.get(position));

                if (ProfileNextStepsFragment.counter <= 0 ) {
                    ProfileNextStepsFragment.txtCompletedNextStep.setVisibility(View.GONE);
                } else {
                    ProfileNextStepsFragment.txtCompletedNextStep.setVisibility(View.VISIBLE);
                }
            }
        });

        topView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sUrl = url.getText().toString();
                if (!sUrl.startsWith("http://") && !sUrl.startsWith("https://")) {
                    sUrl = "http://" + sUrl;
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(sUrl));
                    context.startActivity(intent);
                } else if (sUrl.startsWith("http://") || sUrl.startsWith("https://")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(sUrl));
                    context.startActivity(intent);
                }
            }
        });

        final View finalConvertView1 = convertView;
        bottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String talkId = item.get((Integer) finalConvertView1.getTag()).nextStepObject.optString("talkId");
                final String nextStepsId = item.get((Integer) finalConvertView1.getTag()).nextStepObject.optString("nextStepId");

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage(context.getString(R.string.deleteResources));
                alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProfileNextStepsFragment.RemoveCheckOut(ProfileNextStepsFragment.userID, talkId, nextStepsId);
                        finalConvertView.setVisibility(View.GONE);
                        ProfileNextStepsFragment.userCompletedNextSteps.remove(position);
                        ProfileNextStepsFragment.bottomAdapter.notifyDataSetChanged();
                        ProfileNextStepsFragment.counter--;
                        ProfileNextStepsFragment.txtCompletedNextStep.setText(ProfileNextStepsFragment.counter + " COMPLETED RESOURCES");

                        if (ProfileNextStepsFragment.counter <= 0) {
                            ProfileNextStepsFragment.txtCompletedNextStep.setVisibility(View.GONE);
                        } else {
                            ProfileNextStepsFragment.txtCompletedNextStep.setVisibility(View.VISIBLE);
                        }
                        if (ProfileNextStepsFragment.userCompletedNextSteps.size() == 0 && ProfileNextStepsFragment.userNextStepsArrayList.size() == 0) {
                            ProfileNextStepsFragment.yourNextStepsGoHere.setVisibility(View.VISIBLE);
                            ProfileNextStepsFragment.nextImageView.setVisibility(View.VISIBLE);
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();


            }
        });


        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return item.size();
    }


}

class ResizeAnimation extends Animation {

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}