package io.levelsoftware.xyzreader.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.util.LongSparseArray;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.xyzreader.R;
import io.levelsoftware.xyzreader.data.Article;
import io.levelsoftware.xyzreader.data.ArticleContract;
import io.levelsoftware.xyzreader.data.ArticleDateUtil;
import timber.log.Timber;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleCardViewHolder> {

    private OnClickListener listener;
    private Cursor cursor;

    public LongSparseArray<ArticleColorPalette> paletteCache = new LongSparseArray<>();

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

        int serverIdIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_SERVER_ID);
        int authorIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_AUTHOR);
        int titleIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_TITLE);
        int dateIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_PUBLISHED_DATE);
        int photoIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_PHOTO_URL);
        int aspectRatioIndex = cursor.getColumnIndex(ArticleContract.Article.COLUMN_ASPECT_RATIO);

        // Body is too large and was causing a TransactionTooLarge exception
        Article article = Article.builder()
                .serverId(cursor.getLong(serverIdIndex))
                .author(cursor.getString(authorIndex))
                .title(cursor.getString(titleIndex))
                .publishedDate(cursor.getString(dateIndex))
                .photoUrl(cursor.getString(photoIndex))
                .aspectRatio(cursor.getFloat(aspectRatioIndex))
                .build();

        holder.setArticle(article);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Make sure the scrim is visible
            holder.scrimImageView.setImageAlpha(255);
        }

        holder.articleImageView.setAspectRatio(article.aspectRatio());

        // If we don't have a palette cached for this image then we need to generate one
        if(paletteCache.get(article.serverId()) == null) {
            Timber.d("Palette for " + article.serverId() + " was not cached for image, generating new palette.");

            final long key = article.serverId();
            final DynamicHeightImageView imageView = holder.articleImageView;

            Picasso.with(imageView.getContext())
                    .load(article.photoUrl())
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                            Palette palette = Palette.from(bitmap).generate();
                            paletteCache.put(key, ArticleColorPalette.create(imageView.getContext(), palette));
                        }

                        @Override
                        public void onError() {
                        }
                    });
        } else {
            Timber.d("Palette for " + article.serverId() + " was cached, not creating.");

            Picasso.with(holder.articleImageView.getContext())
                    .load(article.photoUrl())
                    .into(holder.articleImageView);
        }


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
        @BindView(R.id.iv_list_bottom_scrim) ImageView scrimImageView;
        @BindView(R.id.iv_list_article_image) DynamicHeightImageView articleImageView;
        @BindView(R.id.iv_list_add_favorite) ImageView favoriteImageView;
        @BindView(R.id.iv_list_share) ImageView shareImageView;

        private Article article;

        ArticleCardViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            itemFrameLayout.setOnClickListener(this);
            favoriteImageView.setOnClickListener(this);
            shareImageView.setOnClickListener(this);
        }

        public void setArticle(Article article) {
            this.article = article;

            titleTextView.setText(article.title());
            authorTextView.setText(article.author());
            dateTextView.setText(ArticleDateUtil.formatArticleDate(article.publishedDate()));
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.fl_list_item:

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {

                        final List<Pair<View, String>> sharedElements = new ArrayList<>();

                        sharedElements.add(Pair.create((View) titleTextView, v.getContext().getString(R.string.transition_key_title)));
                        sharedElements.add(Pair.create((View) authorTextView, v.getContext().getString(R.string.transition_key_author)));
                        sharedElements.add(Pair.create((View) articleImageView, v.getContext().getString(R.string.transition_key_image)));
                        // For some strange reason trying to run a shared element transition on gradient scrim doesn't work
                        // sharedElements.add(Pair.create((View) scrimImageView, v.getContext().getString(R.string.transition_key_scrim)));

                        ValueAnimator scrimAnimator = ObjectAnimator.ofInt(scrimImageView, "imageAlpha", 0);
                        scrimAnimator.setDuration(150);
                        scrimAnimator.setInterpolator(new AccelerateInterpolator());
                        scrimAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                listener.clickListItem(getAdapterPosition(), article, paletteCache.get(article.serverId()), sharedElements);
                                //scrimImageView.setImageAlpha(255);
                            }

                            @Override public void onAnimationStart(Animator animation) {}
                            @Override public void onAnimationCancel(Animator animation) {}
                            @Override public void onAnimationRepeat(Animator animation) {}
                        });
                        scrimAnimator.start();
                    } else {
                        listener.clickListItem(getAdapterPosition(),article, paletteCache.get(article.serverId()), null);
                    }
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
        void clickListItem(int position, Article article, ArticleColorPalette palette, List<Pair<View, String>> sharedElements);
        void clickFavorite(Article article);
        void clickShare(Article article);
    }

}
