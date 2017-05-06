package io.levelsoftware.xyzreader.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import io.levelsoftware.xyzreader.R;
import timber.log.Timber;


public class ArticleBroadcastReceiver extends BroadcastReceiver {

    private OnStatusUpdateListener listener;

    public ArticleBroadcastReceiver(OnStatusUpdateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(ArticleService.INTENT_STATUS_CODE, -1);
        String message = intent.getStringExtra(ArticleService.INTENT_STATUS_MESSAGE);

        Timber.d("Received broadcast with status code: " + status);

        switch (status) {
            case ArticleService.STATUS_COMPLETE:
                listener.statusComplete();
                break;

            case ArticleService.STATUS_REFRESHING:
                listener.statusRefreshing();
                break;

            case ArticleService.STATUS_ERROR_DATA_CURRENT:
                listener.statusErrorDataCurrent();
                break;

            case ArticleService.STATUS_ERROR_NO_NETWORK:
                listener.statusErrorNoNetwork();
                break;

            case ArticleService.STATUS_ERROR_NETWORK_ISSUE:
                listener.statusErrorNetworkIssue(message);

            default:
                listener.statusErrorUnknown(status, message);
        }

    }

    public static IntentFilter getFilter(Context context) {
       return new IntentFilter(context.getString(R.string.broadcast_article_service));
    }

    public interface OnStatusUpdateListener {
        void statusRefreshing();
        void statusComplete();
        void statusErrorNoNetwork();
        void statusErrorDataCurrent();
        void statusErrorNetworkIssue(String message);
        void statusErrorUnknown(int code, String message);
    }
}
