package io.levelsoftware.xyzreader.ui;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import io.levelsoftware.xyzreader.data.ArticleDateUtil;
import timber.log.Timber;

public class ArticleDetailActivity extends AppCompatActivity {

    @BindView(R.id.cl_detail) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.abl_detail_header) AppBarLayout appBarLayout;
    @BindView(R.id.ctbl_detail_header) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.tb_detail_header) Toolbar toolbar;
    @BindView(R.id.iv_detail_header) ImageView headerImageView;
    @BindView(R.id.nsv_detail_text_container) NestedScrollView scrollView;
    @BindView(R.id.fab_detail_share) FloatingActionButton fab;

    @BindView(R.id.tv_detail_title) TextView titleTextView;
    @BindView(R.id.tv_detail_author) TextView authorTextView;
    @BindView(R.id.tv_detail_date) TextView dateTextView;
    @BindView(R.id.tv_body_content) TextView bodyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ButterKnife.bind(this);

        setupScrollView();
        setupColors();

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        Article article = getIntent().getParcelableExtra(getString(R.string.intent_article_key));
        if(article != null) {
            titleTextView.setText(article.title());
            authorTextView.setText(article.author());
            dateTextView.setText(ArticleDateUtil.formatArticleDate(article.publishedDate()));

            // The body text causes the transition to be really slow if it is added in first
            //bodyTextView.setText(article.body());

            Picasso.with(this)
                    .load(article.photoUrl())
                    .into(headerImageView);
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            setupTransitions();
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
            public void onTransitionStart(Transition transition) {}

            @Override
            public void onTransitionEnd(Transition transition) {
                Timber.d("Enter finished...");

                toolbar.animate().setDuration(100).alpha(1).start();

                if(!fab.isShown()) {
                    fab.show();
                }

                enterTransition.removeListener(this);
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
                Snackbar.make(coordinatorLayout, R.string.action_added_bookmark, Snackbar.LENGTH_SHORT).show();
                break;

            case R.id.action_favorite:
                Snackbar.make(coordinatorLayout, R.string.action_added_favorite, Snackbar.LENGTH_SHORT).show();
                break;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupColors() {

        ArticleColorPalette palette = getIntent().getParcelableExtra(getString(R.string.intent_palette_key));
        if(palette == null) {
            palette = ArticleColorPalette.create(this, null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(palette.colorStatusBar());
            window.setNavigationBarColor(palette.colorStatusBar());
        }

        toolbarLayout.setContentScrimColor(palette.colorToolBar());
        fab.setBackgroundTintList(ColorStateList.valueOf(palette.colorHighlight()));
    }

    private void setupScrollView() {
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int dy = scrollY - oldScrollY;
                if(dy > 0) {
                    if(fab.isShown()) {
                        fab.hide();
                    }
                }
                if(dy < 0) {
                    if(!fab.isShown()) {
                        fab.show();
                    }
                }
            }
        });
    }
}
