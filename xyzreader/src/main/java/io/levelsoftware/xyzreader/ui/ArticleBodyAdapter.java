package io.levelsoftware.xyzreader.ui;

import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.levelsoftware.xyzreader.R;

public class ArticleBodyAdapter extends RecyclerView.Adapter<ArticleBodyAdapter.ArticleBodyViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_CONTENT = 1;

    ArticleColorPalette palette;

    private String header;
    private String[] data;

    public ArticleBodyAdapter(ArticleColorPalette palette) {
        this.palette = palette;
    }

    @Override
    public ArticleBodyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view;
        if(viewType == TYPE_HEADER) {
            view = inflater.inflate(R.layout.list_item_header, parent, false);
        } else {
            view = inflater.inflate(R.layout.list_item_body, parent, false);
        }
        return new ArticleBodyViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_CONTENT;
    }

    public void setData(String[] data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public void onBindViewHolder(ArticleBodyViewHolder holder, int position) {
        String clean;

        if(position == 0) {
            clean = header;
        } else {
            String raw = data[position - 1];
            clean = raw.trim().replaceAll("\\r\\n| +", " ");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.bodyContentBlock.setText(Html.fromHtml(clean, Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.bodyContentBlock.setText(Html.fromHtml(clean));
        }
    }

    @Override
    public int getItemCount() {
        if(data != null) {
            return data.length + 1;
        }
        return 0;
    }

    public class ArticleBodyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_body_content_block) TextView bodyContentBlock;

        public ArticleBodyViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            bodyContentBlock.setLinkTextColor(ColorStateList.valueOf(palette.colorHighlight()));
        }
    }
}
