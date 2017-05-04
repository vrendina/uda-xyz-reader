package io.levelsoftware.xyzreader;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleCardViewHolder> {

    private ArticleListClickHandler clickHandler;

    public ArticleListAdapter(ArticleListClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public ArticleCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_article, parent, false);

        return new ArticleCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleCardViewHolder holder, int position) {

        holder.titleTextView.setText("Testing!!!");
        holder.authorTextView.setText("Victor Rendina");
        holder.dateTextView.setText("Jun 16, 2016");

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class ArticleCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cv_list_item) CardView itemCardView;
        @BindView(R.id.fl_list_item) FrameLayout itemFrameLayout;
        @BindView(R.id.tv_list_title) TextView titleTextView;
        @BindView(R.id.tv_list_author) TextView authorTextView;
        @BindView(R.id.tv_list_date) TextView dateTextView;
        @BindView(R.id.iv_list_add_bookmark) ImageView bookmarkImageView;
        @BindView(R.id.iv_list_add_favorite) ImageView favoriteImageView;
        @BindView(R.id.iv_list_share) ImageView shareImageView;

        ArticleCardViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            itemFrameLayout.setOnClickListener(this);
            bookmarkImageView.setOnClickListener(this);
            favoriteImageView.setOnClickListener(this);
            shareImageView.setOnClickListener(this);
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

                    clickHandler.clickListItem(getAdapterPosition());
                break;

                case R.id.iv_list_add_bookmark:
                    clickHandler.clickBookmark(getAdapterPosition());
                break;

                case R.id.iv_list_add_favorite:
                    clickHandler.clickFavorite(getAdapterPosition());
                break;

                case R.id.iv_list_share:
                    clickHandler.clickShare(getAdapterPosition());
                break;
            }
        }
    }

    public interface ArticleListClickHandler {
        void clickListItem(int position);
        void clickBookmark(int position);
        void clickFavorite(int position);
        void clickShare(int position);
    }

}
