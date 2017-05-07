package io.levelsoftware.xyzreader.ui;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;

import com.google.auto.value.AutoValue;

import io.levelsoftware.xyzreader.R;

@AutoValue
public abstract class ArticleColorPalette implements Parcelable {
    public abstract int colorStatusBar();
    public abstract int colorToolBar();
    public abstract int colorHighlight();

    public static ArticleColorPalette create(Context context, @Nullable Palette palette) {

        int colorStatusBar = ContextCompat.getColor(context, R.color.detail_default_statusbar);
        int colorToolBar = ContextCompat.getColor(context, R.color.detail_default_toolbar);
        int colorHighlight = ContextCompat.getColor(context, R.color.detail_default_fab);

        if(palette != null) {
            colorStatusBar = palette.getDarkMutedColor(colorStatusBar);
            colorToolBar = palette.getMutedColor(colorToolBar);
            colorHighlight = palette.getDarkVibrantColor(colorHighlight);
        }

        return new AutoValue_ArticleColorPalette(colorStatusBar, colorToolBar, colorHighlight);
    }

}