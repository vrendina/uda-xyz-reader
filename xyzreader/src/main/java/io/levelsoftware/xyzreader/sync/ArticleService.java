package io.levelsoftware.xyzreader.sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import io.levelsoftware.xyzreader.R;
import io.levelsoftware.xyzreader.data.ArticleContract;
import io.levelsoftware.xyzreader.data.PreferenceUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;


public class ArticleService extends IntentService {

    public static final String CONTENT_URL = "https://go.udacity.com/xyz-reader-json";
    //public static final int MINIMUM_UPDATE_INTERVAL = 1000 * 60 * 10; // 10 minutes
    public static final int MINIMUM_UPDATE_INTERVAL = 1000 * 5; // 5 seconds

    public static final int STATUS_REFRESHING = 0;
    public static final int STATUS_COMPLETE = 1;

    public static final int STATUS_ERROR_DATA_CURRENT = 90;
    public static final int STATUS_ERROR_NO_NETWORK = 91;
    public static final int STATUS_ERROR_NETWORK_ISSUE = 92;
    public static final int STATUS_ERROR_UNKNOWN = 99;

    public static final String INTENT_STATUS_CODE = "statusCode";
    public static final String INTENT_STATUS_MESSAGE = "statusMessage";

    private static boolean isRunning = false;

    public ArticleService() {
        super(ArticleService.class.getSimpleName());
    }

    public static synchronized boolean isRunning() {
        return isRunning;
    }

    private static synchronized void setIsRunning(boolean isRunning) {
        ArticleService.isRunning = isRunning;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        setIsRunning(true);
        Timber.d("Starting service to load article data...");

        if(dataNeedsUpdate()) {
            updateData();
        } else {
            sendStatusBroadcast(STATUS_ERROR_DATA_CURRENT);
        }
        sendStatusBroadcast(STATUS_COMPLETE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setIsRunning(false);
    }

    public static void start(Context context) {
        context.startService(new Intent(context, ArticleService.class));
    }

    private void sendStatusBroadcast(int code, String message) {
        Intent intent = new Intent();
        intent.setAction(this.getString(R.string.broadcast_article_service));

        intent.putExtra(INTENT_STATUS_CODE, code);
        intent.putExtra(INTENT_STATUS_MESSAGE, message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendStatusBroadcast(int code) {
        sendStatusBroadcast(code, null);
    }

    private boolean dataNeedsUpdate() {
        long lastUpdate = PreferenceUtils.getLastUpdate(this);

        if(lastUpdate == -1) {
            return true;
        }
        long currentDate = (new Date()).getTime();
        return currentDate - lastUpdate > MINIMUM_UPDATE_INTERVAL;
    }

    private boolean networkIsAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void updateData() {
        if(networkIsAvailable()) {
            Timber.d("Network connection is availble, updating data...");
            sendStatusBroadcast(STATUS_REFRESHING);

            JSONArray data = fetchNetworkData();

            try {
                if(data == null) {
                    throw new JSONException("Problem parsing JSON data, no data received.");
                } else {

                    ArrayList<ContentValues> articles = new ArrayList<>();

                    for (int i = 0; i < data.length(); i++) {
                        ContentValues article = new ContentValues();
                        JSONObject object = data.getJSONObject(i);

                        article.put(ArticleContract.Article._ID, object.getString("id"));
                        article.put(ArticleContract.Article.COLUMN_SERVER_ID, object.getString("id"));
                        article.put(ArticleContract.Article.COLUMN_AUTHOR, object.getString("author"));
                        article.put(ArticleContract.Article.COLUMN_TITLE, object.getString("title"));
                        article.put(ArticleContract.Article.COLUMN_BODY, object.getString("body"));
                        article.put(ArticleContract.Article.COLUMN_THUMB_URL, object.getString("thumb"));
                        article.put(ArticleContract.Article.COLUMN_PHOTO_URL, object.getString("photo"));
                        article.put(ArticleContract.Article.COLUMN_ASPECT_RATIO, object.getString("aspect_ratio"));
                        article.put(ArticleContract.Article.COLUMN_PUBLISHED_DATE, object.getString("published_date"));

                        articles.add(article);
                    }

                    if(articles.size() > 0) {
                        // Should do an update instead of delete here, but fine for this app...
                        int count = getContentResolver().delete(ArticleContract.Article.CONTENT_URI, null, null);
                        Timber.d("Deleted " + count + " old articles.");

                        count = getContentResolver().bulkInsert(ArticleContract.Article.CONTENT_URI,
                                articles.toArray(new ContentValues[articles.size()]));

                        Timber.d("Inserted " + count + " new articles.");

                        PreferenceUtils.updateLastUpdate(this);
                    }
                }
            } catch (JSONException e) {
                sendStatusBroadcast(STATUS_ERROR_UNKNOWN, e.toString());
            }
        } else {
            Timber.d("Could not establish network connection, not updating.");
            sendStatusBroadcast(STATUS_ERROR_NO_NETWORK);
        }
    }

    private JSONArray fetchNetworkData() {
        URL url;
        try {
            url = new URL(CONTENT_URL);
        } catch (MalformedURLException e) {
            sendStatusBroadcast(STATUS_ERROR_NETWORK_ISSUE, e.toString());
            return null;
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        String responseString;
        try {
            Response response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch(IOException e) {
            sendStatusBroadcast(STATUS_ERROR_NETWORK_ISSUE, e.toString());
            return null;
        }

        try {
            JSONTokener tokener = new JSONTokener(responseString);
            Object val = tokener.nextValue();
            if (!(val instanceof JSONArray)) {
                throw new JSONException("Expected JSONArray, got " + val.getClass().getSimpleName());
            }
            return (JSONArray) val;
        } catch (JSONException e) {
            sendStatusBroadcast(STATUS_ERROR_UNKNOWN, e.toString());
            return null;
        }
    }
}
