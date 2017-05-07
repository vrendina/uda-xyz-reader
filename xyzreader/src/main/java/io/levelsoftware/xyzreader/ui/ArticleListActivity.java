package io.levelsoftware.xyzreader.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import java.util.List;

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
    @BindView(R.id.abl_list_header) AppBarLayout appBarLayout;
    @BindView(R.id.ctbl_list_header) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.tb_list_header) Toolbar toolbar;
    @BindView(R.id.srl_article_list) SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rv_article_list) RecyclerView recyclerView;

    public static final int ARTICLE_LOADER = 0;

    private ArticleListAdapter adapter;
    private ArticleBroadcastReceiver receiver;
    private Snackbar snackbar;

    private static final int MAX_RETRY_COUNT = 3;
    private int retryCount;

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

        // Hack to keep the navigation bar from flashing
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            postponeEnterTransition();

            final View decor = getWindow().getDecorView();
            decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    decor.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startPostponedEnterTransition();
                    }
                    return true;
                }
            });

            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.primaryDark));
        }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
//            Transition fade = new Fade();
//            fade.setDuration(2000);
//            getWindow().setExitTransition(fade);
//        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {

//            final Transition exitTransition = getWindow().getExitTransition();
//
//            exitTransition.addListener(new Transition.TransitionListener() {
//                @Override
//                public void onTransitionStart(Transition transition) {
//                    Timber.d("Started exit transition");
//                }
//
//                @Override
//                public void onTransitionEnd(Transition transition) {
//
//                }
//
//                @Override
//                public void onTransitionCancel(Transition transition) {
//
//                }
//
//                @Override
//                public void onTransitionPause(Transition transition) {
//
//                }
//
//                @Override
//                public void onTransitionResume(Transition transition) {
//
//                }
//            });

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {ArticleContract.Article.COLUMN_SERVER_ID,
                ArticleContract.Article.COLUMN_AUTHOR,
                ArticleContract.Article.COLUMN_TITLE,
                ArticleContract.Article.COLUMN_PUBLISHED_DATE,
                ArticleContract.Article.COLUMN_PHOTO_URL};

//        int serverIdIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_SERVER_ID);
//        int authorIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_AUTHOR);
//        int titleIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_TITLE);
//        int dateIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_PUBLISHED_DATE);
//        int photoIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_PHOTO_URL);


        return new CursorLoader(this,
                ArticleContract.Article.CONTENT_URI,
                projection,
                null, null, ArticleContract.Article.COLUMN_PUBLISHED_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() == 0 && retryCount < MAX_RETRY_COUNT) {
            refreshData();
            retryCount++;
        }
        adapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        refreshLayout.setRefreshing(false);
        adapter.setCursor(null);
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
    public void clickListItem(Article article, ArticleColorPalette palette, List<Pair<View, String>> sharedElements) {

        final Intent intent = new Intent(this, ArticleDetailActivity.class);

        intent.putExtra(getString(R.string.intent_article_key), article);
        intent.putExtra(getString(R.string.intent_palette_key), palette);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            View statusBar = findViewById(android.R.id.statusBarBackground);
            View navigationBar = findViewById(android.R.id.navigationBarBackground);

            if(navigationBar != null) {
                sharedElements.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
            }

            if(statusBar != null) {
                sharedElements.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
            }

            sharedElements.add(Pair.create((View) toolbar, getString(R.string.transition_key_toolbar)));
//            sharedElements.add(Pair.create((View) collapsingToolbarLayout, getString(R.string.transition_key_toolbar)));

            @SuppressWarnings("unchecked")
            final Bundle options = ActivityOptions.makeSceneTransitionAnimation(this,
                    sharedElements.toArray(new Pair[sharedElements.size()])).toBundle();


            startActivity(intent, options);

//            toolbar.animate().alpha(0).setListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    startActivity(intent, options);
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            }).start();

        } else {
            startActivity(intent);
        }
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
