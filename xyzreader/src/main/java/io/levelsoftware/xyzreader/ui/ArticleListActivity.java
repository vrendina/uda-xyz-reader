package io.levelsoftware.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.xyzreader.R;
import io.levelsoftware.xyzreader.data.Article;
import io.levelsoftware.xyzreader.data.ArticleContract;
import io.levelsoftware.xyzreader.sync.ArticleBroadcastReceiver;
import io.levelsoftware.xyzreader.sync.ArticleService;
import timber.log.Timber;

public class ArticleListActivity extends AppCompatActivity implements
        ArticleListAdapter.OnClickListener,
        ArticleBroadcastReceiver.OnStatusUpdateListener,
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.cl_list) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.tb_list_header) Toolbar toolbar;
    @BindView(R.id.srl_article_list) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_article_list) RecyclerView recyclerView;

    public static final int ARTICLE_LOADER = 0;

    private ArticleListAdapter adapter;
    private ArticleBroadcastReceiver receiver;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        ButterKnife.bind(this);

        receiver = new ArticleBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                ArticleBroadcastReceiver.getFilter(this));

        refreshLayout.setOnRefreshListener(this);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        adapter = new ArticleListAdapter(this);
        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(ARTICLE_LOADER, null, this);

//        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ArticleContract.Article.CONTENT_URI,
                ArticleContract.Article.COLUMNS.toArray(new String[]{}),
                null, null, ArticleContract.Article.COLUMN_PUBLISHED_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public void onRefresh() {
        refreshData();
    }

    public void refreshData() {
        if(snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }

        if(ArticleService.isRunning()) {
            statusRefreshing();
        } else {
            ArticleService.start(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public void clickListItem(Article article, ArticleColorPalette palette) {
        Intent intent = new Intent(this, ArticleDetailActivity.class);

        intent.putExtra(getString(R.string.intent_article_key), article);
        intent.putExtra(getString(R.string.intent_palette_key), palette);

        startActivity(intent);
    }

    @Override
    public void clickBookmark(Article article) {
        Timber.d("Clicked bookmark for: " + article.title());
        showSnackBar(R.string.action_added_bookmark, R.string.action_undo, Snackbar.LENGTH_SHORT);
    }

    @Override
    public void clickFavorite(Article article) {
        Timber.d("Clicked favorite for: " + article.title());
        showSnackBar(R.string.action_added_favorite, R.string.action_undo, Snackbar.LENGTH_SHORT);
    }

    @Override
    public void clickShare(Article article) {
        Timber.d("Clicked share for: " + article.title());
    }

    @Override
    public void statusRefreshing() {
        Timber.d("STATUS: Refreshing...");
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void statusComplete() {
        Timber.d("STATUS: Complete");
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void statusErrorNoNetwork() {
        Timber.d("STATUS: No network connectivity");
        showSnackBar(R.string.error_no_network, R.string.action_dismiss, Snackbar.LENGTH_LONG);
    }

    @Override
    public void statusErrorDataCurrent() {
        Timber.d("STATUS: Data has already been refreshed recently");
    }

    @Override
    public void statusErrorNetworkIssue(String message) {
        Timber.d("STATUS: Networking issue -- " + message);
        showSnackBar(R.string.error_network_issue, R.string.action_dismiss, Snackbar.LENGTH_LONG);
    }

    @Override
    public void statusErrorUnknown(int code, String message) {
        Timber.e("STATUS: Unknown error '" + code + "' occurred -- " + message);
        showSnackBar(R.string.error_unknown, R.string.action_dismiss, Snackbar.LENGTH_LONG);
    }

    private void showSnackBar(@StringRes int messageId, @StringRes int buttonId, int length) {
        snackbar = Snackbar.make(coordinatorLayout, messageId, length);
        snackbar.setAction(buttonId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        snackbar.show();
    }
}
