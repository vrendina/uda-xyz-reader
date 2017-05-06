package io.levelsoftware.xyzreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.xyzreader.sync.ArticleBroadcastReceiver;
import io.levelsoftware.xyzreader.sync.ArticleService;
import timber.log.Timber;

public class ArticleListActivity extends AppCompatActivity implements
        ArticleListAdapter.OnClickListener,
        ArticleBroadcastReceiver.OnStatusUpdateListener,
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.cl_list) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.tb_list_header) Toolbar toolbar;
    @BindView(R.id.srl_article_list) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_article_list) RecyclerView recyclerView;

    private ArticleBroadcastReceiver receiver;

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

        recyclerView.setAdapter(new ArticleListAdapter(this));

        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    public void refreshData() {
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
    public void clickListItem(int position) {
        Intent intent = new Intent(this, ArticleDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void clickBookmark(int position) {
        Snackbar.make(coordinatorLayout, R.string.action_added_bookmark, Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void clickFavorite(int position) {
        Snackbar.make(coordinatorLayout, R.string.action_added_favorite, Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void clickShare(int position) {

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

    }

    @Override
    public void statusErrorDataCurrent() {
        Timber.d("STATUS: Data has already been refreshed recently");

    }

    @Override
    public void statusErrorNetworkIssue(String message) {
        Timber.d("STATUS: Networking issue -- " + message);

    }

    @Override
    public void statusErrorUnknown(int code, String message) {
        Timber.e("STATUS: Unknown error '" + code + "' occurred -- " + message);
    }
}
