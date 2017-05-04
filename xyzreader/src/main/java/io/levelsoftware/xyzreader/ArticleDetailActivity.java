package io.levelsoftware.xyzreader;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleDetailActivity extends AppCompatActivity {

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
