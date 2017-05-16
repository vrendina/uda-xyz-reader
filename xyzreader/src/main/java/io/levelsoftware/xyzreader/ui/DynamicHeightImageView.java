package io.levelsoftware.xyzreader.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;


public class DynamicHeightImageView extends android.support.v7.widget.AppCompatImageView {

    private static final float DEFAULT_ASPECT_RATIO = 1.5f;
    private static final float MINIMUM_ASPECT_RATIO = 0.75f;
    private static final float MAXIMUM_ASPECT_RATIO = 2.0f;

    private float aspectRatio = DEFAULT_ASPECT_RATIO;

    public DynamicHeightImageView(Context context) {
        super(context);
    }

    public DynamicHeightImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicHeightImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = (aspectRatio < MINIMUM_ASPECT_RATIO) ?
                MINIMUM_ASPECT_RATIO : aspectRatio;

        this.aspectRatio = (aspectRatio > MAXIMUM_ASPECT_RATIO) ?
                MAXIMUM_ASPECT_RATIO : aspectRatio;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, (int) (measuredWidth / aspectRatio));
    }
}
