package io.levelsoftware.xyzreader.ui;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.xyzreader.R;
import io.levelsoftware.xyzreader.data.Article;
import io.levelsoftware.xyzreader.data.ArticleContract;
import timber.log.Timber;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleCardViewHolder> {

    private OnClickListener listener;
    private Cursor cursor;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.US);

    public ArticleListAdapter(OnClickListener listener) {
        this.listener = listener;
    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public ArticleCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_article, parent, false);

        return new ArticleCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleCardViewHolder holder, int position) {
        cursor.moveToPosition(position);

        ImmutableList<String> columns = ArticleContract.Article.COLUMNS;

        Article article = Article.builder()
                .serverId(cursor.getLong(columns.indexOf(ArticleContract.Article.COLUMN_SERVER_ID)))
                .author(cursor.getString(columns.indexOf(ArticleContract.Article.COLUMN_AUTHOR)))
                .title(cursor.getString(columns.indexOf(ArticleContract.Article.COLUMN_TITLE)))
                .publishedDate(cursor.getString(columns.indexOf(ArticleContract.Article.COLUMN_PUBLISHED_DATE)))
                .body(cursor.getString(columns.indexOf(ArticleContract.Article.COLUMN_BODY)))
                .photoUrl(cursor.getString(columns.indexOf(ArticleContract.Article.COLUMN_PHOTO_URL)))
                .build();

        holder.setArticle(article);

        Picasso.with(holder.articleImageView.getContext())
                .load(article.photoUrl())
                .into(holder.articleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }

    class ArticleCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cv_list_item) CardView itemCardView;
        @BindView(R.id.fl_list_item) FrameLayout itemFrameLayout;
        @BindView(R.id.tv_list_title) TextView titleTextView;
        @BindView(R.id.tv_list_author) TextView authorTextView;
        @BindView(R.id.tv_list_date) TextView dateTextView;
        @BindView(R.id.iv_list_article_image) ImageView articleImageView;
        @BindView(R.id.iv_list_add_bookmark) ImageView bookmarkImageView;
        @BindView(R.id.iv_list_add_favorite) ImageView favoriteImageView;
        @BindView(R.id.iv_list_share) ImageView shareImageView;

        private Article article;

        ArticleCardViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            itemFrameLayout.setOnClickListener(this);
            bookmarkImageView.setOnClickListener(this);
            favoriteImageView.setOnClickListener(this);
            shareImageView.setOnClickListener(this);
        }

        public void setArticle(Article article) {
            this.article = article;

            titleTextView.setText(article.title());
            authorTextView.setText(article.author());

            String dateString = "";
            try {
                Date date = dateFormat.parse(article.publishedDate());

                dateString = DateUtils.getRelativeTimeSpanString(
                        date.getTime(),
                        System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL).toString();
            } catch (ParseException e) {
                Timber.e("Could not parse date information.");
            }

            dateTextView.setText(dateString);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.fl_list_item:
                    // Lift the card if we are on Lollipop or greater
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                        AnimatorSet liftAnimator =
                                (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(), R.animator.lift_on_touch);

                        liftAnimator.setTarget(itemCardView);
                        liftAnimator.start();
                    }

                    listener.clickListItem(article);
                break;

                case R.id.iv_list_add_bookmark:
                    listener.clickBookmark(article);
                break;

                case R.id.iv_list_add_favorite:
                    listener.clickFavorite(article);
                break;

                case R.id.iv_list_share:
                    listener.clickShare(article);
                break;
            }
        }
    }

    public interface OnClickListener {
        void clickListItem(Article article);
        void clickBookmark(Article article);
        void clickFavorite(Article article);
        void clickShare(Article article);
    }

}
