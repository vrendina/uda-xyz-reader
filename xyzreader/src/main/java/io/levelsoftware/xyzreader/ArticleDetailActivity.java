package io.levelsoftware.xyzreader;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ArticleDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.abl_detail_header)
    AppBarLayout appBarLayout;

    @BindView(R.id.ctbl_detail_header)
    CollapsingToolbarLayout toolbarLayout;

    @BindView(R.id.tv_detail_title)
    TextView titleTextView;

    @BindView(R.id.tv_detail_author)
    TextView authorTextView;

    @BindView(R.id.tv_detail_date)
    TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ButterKnife.bind(this);

        appBarLayout.addOnOffsetChangedListener(this);

        // Set colors based on palette
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.BLUE);
        }
        toolbarLayout.setContentScrimColor(Color.YELLOW);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        Timber.d("Offset " + verticalOffset);
    }
}
