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
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListAdapter.ArticleCardViewHolder> {

    @Override
    public ArticleCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.article_list_item, parent, false);

        return new ArticleCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleCardViewHolder holder, int position) {

        holder.titleTextView.setText("Testing!!!");

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class ArticleCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cv_list_item)
        CardView itemCardView;

        @BindView(R.id.fl_list_item)
        FrameLayout itemFrameLayout;

        @BindView(R.id.tv_list_title)
        TextView titleTextView;

        ArticleCardViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            itemFrameLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Timber.d("Clicked on view!!!" + getAdapterPosition());

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                AnimatorSet liftAnimator =
                        (AnimatorSet)AnimatorInflater.loadAnimator(v.getContext(), R.animator.lift_on_touch);

                liftAnimator.setTarget(itemCardView);
                liftAnimator.start();
            }
        }
    }


}
