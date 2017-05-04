package io.levelsoftware.xyzreader;

import android.content.Intent;
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

    @BindView(R.id.cl_list) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.tb_list_header) Toolbar toolbar;
    @BindView(R.id.rv_article_list) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        recyclerView.setAdapter(new ArticleListAdapter(this));
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
}
