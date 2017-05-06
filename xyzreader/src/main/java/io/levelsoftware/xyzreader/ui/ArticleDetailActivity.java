package io.levelsoftware.xyzreader.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.xyzreader.R;

public class ArticleDetailActivity extends AppCompatActivity {

    @BindView(R.id.cl_detail) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.abl_detail_header) AppBarLayout appBarLayout;
    @BindView(R.id.ctbl_detail_header) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.tb_detail_header) Toolbar toolbar;
    @BindView(R.id.nsv_detail_text_container) NestedScrollView scrollView;
    @BindView(R.id.fab_detail_share) FloatingActionButton fab;

    @BindView(R.id.tv_detail_title) TextView titleTextView;
    @BindView(R.id.tv_detail_author) TextView authorTextView;
    @BindView(R.id.tv_detail_date) TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ButterKnife.bind(this);

        setupScrollView();
        setupColors();

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
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

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.BLUE);
        }
        toolbarLayout.setContentScrimColor(Color.YELLOW);
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
