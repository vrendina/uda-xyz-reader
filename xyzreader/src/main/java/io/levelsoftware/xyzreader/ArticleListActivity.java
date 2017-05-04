package io.levelsoftware.xyzreader;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleListActivity extends AppCompatActivity implements ArticleListAdapter.ArticleListClickHandler {

    @BindView(R.id.cl_list) CoordinatorLayout listCoordinatorLayout;
    @BindView(R.id.tb_list_header) Toolbar listToolbar;
    @BindView(R.id.rv_article_list) RecyclerView articleRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        ButterKnife.bind(this);

        setSupportActionBar(listToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        articleRecyclerView.setAdapter(new ArticleListAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public void clickListItem(int position) {

    }

    @Override
    public void clickBookmark(int position) {
        Snackbar.make(listCoordinatorLayout, R.string.action_added_bookmark, Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void clickFavorite(int position) {
        Snackbar.make(listCoordinatorLayout, R.string.action_added_favorite, Snackbar.LENGTH_SHORT).show();

    }

    @Override
    public void clickShare(int position) {

    }
}
