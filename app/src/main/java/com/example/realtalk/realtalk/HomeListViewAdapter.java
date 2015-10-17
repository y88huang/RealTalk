package com.example.realtalk.realtalk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by alexgomes on 2015-08-15.
 */
public class HomeListViewAdapter extends BaseAdapter {

    private ArrayList<Card> cardView;
    private LayoutInflater inflater;
    private Context context;
    private SharedPreferences sharedPreferences;
    String userID, facebookId;
    String requestURL;

    public HomeListViewAdapter(Context c, LayoutInflater layoutInflater, ArrayList<Card> item) {
        inflater = layoutInflater;
        context = c;
        cardView = item;

        requestURL = context.getResources().getString(R.string.serverURL) + "api/user/addBookmarkToUser";

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
            viewHolder.bg = (NetworkImageView) view.findViewById(R.id.bg);
            viewHolder.category1 = (TextView) view.findViewById(R.id.category1);
            viewHolder.category2 = (TextView) view.findViewById(R.id.category2);
            viewHolder.category3 = (TextView) view.findViewById(R.id.category3);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            view.setTag(viewHolder);
        }

        viewHolder.title.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.MontSerratRegular));
        viewHolder.tagline.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));

        viewHolder.category1.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));
        viewHolder.category2.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));
        viewHolder.category3.setTypeface(FontManager.setFont(view.getContext(), FontManager.Font.OpenSansRegular));

        final Drawable drawable1 = ContextCompat.getDrawable(context, R.drawable.iconbookmarked);
        final Drawable drawable2 = ContextCompat.getDrawable(context, R.drawable.iconbookmarked_filled);

        viewHolder.bookMark.setImageDrawable(drawable1);
        viewHolder.bookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewHolder.bookMark.setTag(position);
                if (((ImageButton) v).getDrawable() == drawable1) {
                    ((ImageButton) v).setImageDrawable(drawable2);

                } else {
                    ((ImageButton) v).setImageDrawable(drawable1);
                }

                sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.tlpSharedPreference), Context.MODE_PRIVATE);
                userID = sharedPreferences.getString("userID", "");
                facebookId = sharedPreferences.getString("facebookId", "");

                if (userID.isEmpty() && facebookId.isEmpty()) {
                    Intent intent = new Intent(context, Authentication.class);
                    context.startActivity(intent);
                } else {
                    final HashMap<String, String> params = new HashMap<>();
                    params.put("userId", userID);
                    params.put("talkId", cardView.get(position)._id);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, requestURL, new JSONObject(params),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
//                                    Log.v("response", response.toString());
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
        viewHolder.bg.setImageUrl(cardView.get(position).bg, HomeScreen.imgLoader);

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


//        Delete this on production
        viewHolder.likesBookmark = (TextView) view.findViewById(R.id.likexBookmark);
        if (!cardView.get(0).bookmark.isEmpty()) {
            viewHolder.likesBookmark.setText("Bookmark: " + cardView.get(position).bookmark);
        }

        return view;
    }

    static class ViewHolder {
        ImageButton bookMark, share;
        TextView title, tagline, category1, category2, category3, likesBookmark;
        NetworkImageView bg;
    }

    private void FacebookShare(int position) {
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareDialog shareDialog = new ShareDialog((HomeScreen) context);
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(cardView.get(position).title)
                    .setContentDescription(cardView.get(position).tagline)
                    .setImageUrl(Uri.parse(cardView.get(position).bg))
                    .setContentUrl(Uri.parse("http://tlpserver.herokuapp.com/#/tkId" + cardView.get(position)._id))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    private void TwitterShare(int position) {
        Intent shareIntent;
        PackageManager packageManger = context.getPackageManager();

        Intent tweet = new Intent(Intent.ACTION_VIEW);
        tweet.setData(Uri.parse("http://twitter.com/?status=" + Uri.encode("HEYYLLOO")));//where message is your string message
        context.startActivity(tweet);
//        try {
//            PackageInfo pkgInfo = packageManger.getPackageInfo("com.twitter.android", 0);
//
//            if(pkgInfo.toString().equals("com.twitter.android")){
//                shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setClassName("com.twitter.android",
//                        "com.twitter.android.PostActivity");
//                shareIntent.setType("text/*");
//                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "HEY TWITTER GOT IT ");
//                context.startActivity(shareIntent);
//            } else {
//                Intent tweet = new Intent(Intent.ACTION_VIEW);
//                tweet.setData(Uri.parse("http://twitter.com/?status=" + Uri.encode("HEYYLLOO")));//where message is your string message
//                context.startActivity(tweet);
//            }
//
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }


    }

    private void EmailShare(int position) {
        Intent send = new Intent(Intent.ACTION_SENDTO);
        send.setType("*/*");
        String uriText = "mailto:" + Uri.encode("") +
                "?subject=" + Uri.encode("RealTalk -" + cardView.get(position).title) +
                "&body=" + Uri.encode(cardView.get(position).tagline) + "\n\n"+
                Uri.encode("http://tlpserver.herokuapp.com/#/tkId" + cardView.get(position)._id);

        Uri uri = Uri.parse(uriText);
        send.setData(uri);
        context.startActivity(Intent.createChooser(send, "Share mail..."));

//        Intent sendIntent = new Intent();
//        sendIntent.setAction(Intent.ACTION_SENDTO);
//        sendIntent.setType("*/*");
//        sendIntent.putExtra(Intent.EXTRA_TITLE,cardView.get(position).title);
//        sendIntent.putExtra(Intent.EXTRA_SUBJECT, cardView.get(position).title);
//        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<p>" + cardView.get(position).tagline + "</p>"));
//        context.startActivity(Intent.createChooser(sendIntent, "Share Mail"));

    }

}

