package io.levelsoftware.xyzreader.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.xyzreader.R;
import io.levelsoftware.xyzreader.data.Article;
import io.levelsoftware.xyzreader.data.ArticleContract;
import io.levelsoftware.xyzreader.data.ArticleDateUtil;
import timber.log.Timber;

public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    @Nullable @BindView(R.id.cl_detail) CoordinatorLayout coordinatorLayout;
    @Nullable @BindView(R.id.ctbl_detail_header) CollapsingToolbarLayout toolbarLayout;
    @Nullable @BindView(R.id.iv_detail_bottom_scrim) ImageView bottomScrimImageView;
    @Nullable @BindView(R.id.iv_detail_top_scrim) ImageView topScrimImageView;

    @BindView(R.id.tb_detail_header) Toolbar toolbar;
    @BindView(R.id.iv_detail_header) ImageView headerImageView;
    @BindView(R.id.fab_detail_share) FloatingActionButton fab;

    @BindView(R.id.tv_detail_title) TextView titleTextView;
    @BindView(R.id.tv_detail_author) TextView authorTextView;

    @BindView(R.id.rv_article_body) RecyclerView recyclerView;

    public static final int ARTICLE_DETAIL_LOADER = 1;

    private ArticleBodyAdapter adapter;
    private Article article;

    private int layoutWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ButterKnife.bind(this);

        layoutWidth = getResources().getInteger(R.integer.width_breakpoint);

        setupData();
        setupScrollView();
        setupColors();
        setupActionBar();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            hideViews();
            setupTransitions();
        }

        if(savedInstanceState != null) {
            showViews();
        }
    }

    private void hideViews() {
        // Hide elements so they can be animated in appropriately
        toolbar.setAlpha(0);
        recyclerView.setAlpha(0);

        if(bottomScrimImageView != null) {
            bottomScrimImageView.setImageAlpha(0);
        }

        if(topScrimImageView != null) {
            topScrimImageView.setImageAlpha(0);
        }
        fab.setVisibility(View.INVISIBLE);
    }

    private void showViews() {
        // Hide elements so they can be animated in appropriately
        toolbar.setAlpha(1);
        recyclerView.setAlpha(1);
        if(bottomScrimImageView != null) {
            bottomScrimImageView.setImageAlpha(255);
        }

        if(topScrimImageView != null) {
            topScrimImageView.setImageAlpha(255);
        }
        fab.setVisibility(View.VISIBLE);
    }

    private void setupData() {
        adapter = new ArticleBodyAdapter();
        recyclerView.setAdapter(adapter);

        Article article = getIntent().getParcelableExtra(getString(R.string.intent_article_key));
        if(article != null) {
            this.article = article;
            // The body text causes the transition to be really slow if it is added in first so
            // we will load it with a cursor loader then fade it in when we have it
            getSupportLoaderManager().initLoader(ARTICLE_DETAIL_LOADER, null, this);

            titleTextView.setText(article.title());
            authorTextView.setText(article.author());

            String dateString = getString(R.string.published_date,
                    ArticleDateUtil.formatArticleDate(article.publishedDate()));

            adapter.setHeader(dateString);

            Picasso.with(this)
                    .load(article.photoUrl())
                    .into(headerImageView);
        }
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupTransitions() {

        // Hack for preventing flashing
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

        final Transition enterTransition = getWindow().getEnterTransition();

        enterTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionEnd(Transition transition) {
                Timber.d("Enter transition complete, animating other views...");

                toolbar.animate().setDuration(600).alpha(1).start();

                ValueAnimator bottomScrimAnimator = ObjectAnimator.ofInt(bottomScrimImageView, "imageAlpha", 255);
                bottomScrimAnimator.setInterpolator(new FastOutSlowInInterpolator());
                bottomScrimAnimator.setDuration(2000);
                bottomScrimAnimator.start();

                ValueAnimator topScrimAnimator = ObjectAnimator.ofInt(topScrimImageView, "imageAlpha", 255);
                topScrimAnimator.setInterpolator(new FastOutSlowInInterpolator());
                topScrimAnimator.setDuration(2000);
                topScrimAnimator.start();

                enterTransition.removeListener(this);
            }

            @Override public void onTransitionStart(Transition transition) {
                float startY = recyclerView.getY();
                recyclerView.setY(startY + recyclerView.getHeight());

                recyclerView.animate().setDuration(1200).y(startY)
                        .setInterpolator(new FastOutSlowInInterpolator())
                        .start();

                recyclerView.animate().setDuration(1200).alpha(1).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(!fab.isShown()) {
                            fab.show();
                        }
                    }

                    @Override public void onAnimationStart(Animator animation) {}
                    @Override public void onAnimationCancel(Animator animation) {}
                    @Override public void onAnimationRepeat(Animator animation) {}
                }).start();

            }
            @Override public void onTransitionCancel(Transition transition) {}
            @Override public void onTransitionPause(Transition transition) {}
            @Override public void onTransitionResume(Transition transition) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        // Trying to reverse the shared element transition if things have moved around doesn't look good.
        // supportFinishAfterTransition();
        finish();

        //super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_bookmark:
                Snackbar snackbar = Snackbar.make(recyclerView, R.string.add_bookmark, Snackbar.LENGTH_SHORT);
                snackbar.setAction(R.string.action_undo, new View.OnClickListener() {@Override public void onClick(View v) {}});
                snackbar.show();
                break;

            case R.id.action_favorite:
                snackbar = Snackbar.make(recyclerView, R.string.add_favorite, Snackbar.LENGTH_SHORT);
                snackbar.setAction(R.string.action_undo, new View.OnClickListener() {@Override public void onClick(View v) {}});
                snackbar.show();
                break;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupColors() {

        ArticleColorPalette palette = getIntent().getParcelableExtra(getString(R.string.intent_palette_key));
        if (palette == null) {
            palette = ArticleColorPalette.create(this, null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(palette.colorStatusBar());
            window.setNavigationBarColor(palette.colorStatusBar());
        }

        if (toolbarLayout != null) {
            toolbarLayout.setContentScrimColor(palette.colorToolBar());
        }

        fab.setBackgroundTintList(ColorStateList.valueOf(palette.colorHighlight()));
    }

    private void setupScrollView() {

        if(layoutWidth < 720) {
            // For hiding the fab when the RecyclerView is scrolling down, but showing when we are going up
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0) {
                        if (fab.isShown()) {
                            fab.hide();
                        }
                    }
                    if (dy < 0) {
                        if (!fab.isShown()) {
                            fab.show();
                        }
                    }
                    super.onScrolled(recyclerView, dx, dy);
                }
            });


            // Set trigger height for toolbar
            if(toolbarLayout != null) {
                final int triggerHeight = (int) (2.5 * ViewCompat.getMinimumHeight(toolbarLayout));
                toolbarLayout.setScrimVisibleHeightTrigger(triggerHeight);
            }
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {ArticleContract.Article.COLUMN_BODY};
        String selection = ArticleContract.Article.COLUMN_SERVER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(article.serverId())};

        return new CursorLoader(this,
                ArticleContract.Article.CONTENT_URI,
                projection,
                selection, selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null) {
            data.moveToFirst();
            String bodyText = data.getString(0);

            Timber.d("Initial string length--- " + bodyText.length());
            /*
             * Adding the text data to a single TextView is not very fast so it is more efficient
             * to break it down into logical sections and place each of those sections into a
             * RecyclerView cell. Any time a double line break is encountered in the text it will
             * be split into a new cell.
             */
            String[] content = bodyText.split("\\r\\n\\r\\n");

            Timber.d("Split into cells: " + content.length);

            adapter.setData(content);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void clickedShare(View view) {
        String shareString = getString(R.string.share_content, article.title(), article.author());

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));
    }
}
