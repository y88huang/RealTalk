package com.serindlabs.realtalk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alexgomes on 2015-08-15.
 */
public class HomeListViewAdapter extends BaseAdapter {

    private ArrayList<Card> cardView;
    private LayoutInflater inflater;
    private Context context;
    private SharedPreferences sharedPreferences;
    String userID, facebookId, requestURL, prefFile;
    ImageLoader imgLoader;
    Boolean bookmarked;
    ImageLoaderConfiguration configuration;
    DisplayImageOptions defaultOptions;

    public HomeListViewAdapter(Context c, LayoutInflater layoutInflater) {
        inflater = layoutInflater;
        context = c;
        prefFile = context.getResources().getString(R.string.tlpSharedPreference);

        imgLoader = ImageLoader.getInstance();
        defaultOptions = new DisplayImageOptions.Builder()
                .delayBeforeLoading(0)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
        configuration = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .writeDebugLogs()
                .build();
        imgLoader.init(configuration);
    }

    public void SetList(ArrayList<Card> item) {
        this.cardView = item;
    }

    @Override
    public int getCount() {
        return cardView.size();
    }

    @Override
    public Object getItem(int position) {
        return cardView.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View view = convertView;
        final ViewHolder viewHolder;

        if (view == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.home_list_single_row, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.tagline = (TextView) view.findViewById(R.id.tagline);
            viewHolder.share = (ImageButton) view.findViewById(R.id.share);
            viewHolder.bookMark = (ImageButton) view.findViewById(R.id.bookmark);
            viewHolder.bg = (ImageView) view.findViewById(R.id.bg);
            viewHolder.category1 = (TextView) view.findViewById(R.id.category1);
            viewHolder.category2 = (TextView) view.findViewById(R.id.category2);
            viewHolder.category3 = (TextView) view.findViewById(R.id.category3);
            viewHolder.bookMark = (ImageButton) view.findViewById(R.id.bookmark);
            viewHolder.newTalk = (ImageView) view.findViewById(R.id.newTalk);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.title.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.MontSerratRegular));
        viewHolder.tagline.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));

        viewHolder.category1.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));
        viewHolder.category2.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));
        viewHolder.category3.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));

        if (cardView.get(position).bookmarkedByUser) {
            viewHolder.bookMark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.iconbookmarked_filled));
        } else if (!cardView.get(position).bookmarkedByUser) {
            viewHolder.bookMark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.iconbookmarked));
        }

        viewHolder.bookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bookmarked = cardView.get(position).bookmarkedByUser;
                Log.v("BookMarkedByUser", cardView.get(position).bookmarkedByUser.toString());

                sharedPreferences = context.getSharedPreferences(prefFile, Context.MODE_PRIVATE);
                userID = sharedPreferences.getString("userID", "");
                facebookId = sharedPreferences.getString("facebookId", "");

                if (bookmarked == false) {
                    if (userID.isEmpty() && facebookId.isEmpty()) {
                        Intent intent = new Intent(context, Authentication.class);
                        context.startActivity(intent);
                    } else {
                        requestURL = context.getResources().getString(R.string.serverURL) + "api/user/addBookmarkToUser";

                        final HashMap<String, String> params = new HashMap<>();
                        params.put("userId", userID);
                        params.put("talkId", cardView.get(position)._id);

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        cardView.get(position).setBookmarkedByUser(true);
                                        viewHolder.bookMark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.iconbookmarked_filled));
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
                if (bookmarked == true) {
                    if (userID.isEmpty() && facebookId.isEmpty()) {
                        Intent intent = new Intent(context, Authentication.class);
                        context.startActivity(intent);
                    } else {
                        requestURL = context.getResources().getString(R.string.serverURL) + "api/user/removeBookmarkFromUser";

                        final HashMap<String, String> params = new HashMap<>();
                        params.put("userId", userID);
                        params.put("talkId", cardView.get(position)._id);

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        cardView.get(position).setBookmarkedByUser(false);
                                        viewHolder.bookMark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.iconbookmarked));
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

            }
        });

        viewHolder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] dialogItem = {"Facebook", "Twitter", "Email"};

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Share with");
                alertDialogBuilder
                        .setCancelable(true)
                        .setIcon(R.mipmap.ic_launcher)
                        .setSingleChoiceItems(dialogItem, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog1, int pos) {
                                if (pos == 0) {
                                    FacebookShare(position);
                                } else if (pos == 1) {
                                    TwitterShare(position);
                                } else if (pos == 2) {
                                    EmailShare(position);
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

        viewHolder.title.setText(cardView.get(position).title);
        viewHolder.tagline.setText(cardView.get(position).tagline);
        imgLoader.loadImage(cardView.get(position+2).bg, new SimpleImageLoadingListener());
        imgLoader.displayImage(cardView.get(position).bg, viewHolder.bg);

        Matrix matrix = viewHolder.bg.getImageMatrix();
        matrix.preTranslate(0, -100);
        viewHolder.bg.setImageMatrix(matrix);

        for (int i = 0; i < cardView.get(position).categories.length; i++) {
            String category1 = cardView.get(position).categories[0].optString("title");
            String category2 = cardView.get(position).categories[1].optString("title");
            String category3 = cardView.get(position).categories[2].optString("title");
            viewHolder.category1.setText(category1);
            viewHolder.category2.setText(category2);
            viewHolder.category3.setText(category3);
        }


        if (cardView.get(position).newTalk) {
            viewHolder.newTalk.setVisibility(View.VISIBLE);
        } else if (!cardView.get(position).newTalk) {
            viewHolder.newTalk.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    static class ViewHolder {
        ImageView newTalk;
        ImageButton bookMark, share;
        TextView title, tagline, category1, category2, category3;
        ImageView bg;
    }

    private void FacebookShare(int position) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareDialog shareDialog = new ShareDialog((HomeScreen) context);
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(cardView.get(position).title)
                    .setContentDescription(cardView.get(position).tagline)
                    .setImageUrl(Uri.parse(cardView.get(position).bg))
                    .setContentUrl(Uri.parse(context.getResources().getString(R.string.talkDetailsWebConnection) + cardView.get(position).shortUrl))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    private void TwitterShare(int position) {
        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                urlEncode(cardView.get(position).title + " "),
                urlEncode(context.getString(R.string.talkDetailsWebConnection) + cardView.get(position).shortUrl));

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

// Narrow down to official Twitter app, if available:
        List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }

        context.startActivity(intent);
    }

    private void EmailShare(int position) {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        send.setType("*/*");
        String uriText = "mailto:" + Uri.encode("") +
                "?subject=" + Uri.encode("RealTalk -" + cardView.get(position).title) +
                "&body=" + Uri.encode(cardView.get(position).tagline) + "\n\n" +
                Uri.encode(context.getResources().getString(R.string.talkDetailsWebConnection) + cardView.get(position).shortUrl);

        Uri uri = Uri.parse(uriText);
        send.setData(uri);
        context.startActivity(Intent.createChooser(send, "Share mail..."));
    }


}

