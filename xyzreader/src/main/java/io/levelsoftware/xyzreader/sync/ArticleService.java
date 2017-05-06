package io.levelsoftware.xyzreader.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Date;

import io.levelsoftware.xyzreader.R;
import io.levelsoftware.xyzreader.data.PreferenceUtils;
import timber.log.Timber;


public class ArticleService extends IntentService {

    public static final int MINIMUM_UPDATE_INTERVAL = 5000;

    public static final int STATUS_REFRESHING = 0;
    public static final int STATUS_COMPLETE = 1;

    public static final int STATUS_ERROR_DATA_CURRENT = 90;
    public static final int STATUS_ERROR_NO_NETWORK = 91;
    public static final int STATUS_ERROR_NETWORK_ISSUE = 92;

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
            Timber.d("Data is up to date, not refreshing.");
            sendStatusBroadcast(STATUS_ERROR_DATA_CURRENT);
        }
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

            // Do some work...
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sendStatusBroadcast(STATUS_COMPLETE);
        } else {
            Timber.d("Could not establish network connection, not updating.");
            sendStatusBroadcast(STATUS_ERROR_NO_NETWORK);
        }
    }
}
