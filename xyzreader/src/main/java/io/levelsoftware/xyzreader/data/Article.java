package io.levelsoftware.xyzreader.data;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Article implements Parcelable {

    public abstract long serverId();
    public abstract String title();
    public abstract String author();
    // Body removed because it takes too much memory causing a crash
    // public abstract String body();
    public abstract String photoUrl();
    public abstract String publishedDate();
    public abstract float aspectRatio();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder serverId(long value);
        public abstract Builder title(String value);
        public abstract Builder author(String value);
//        public abstract Builder body(String value);
        public abstract Builder photoUrl(String value);
        public abstract Builder publishedDate(String value);
        public abstract Builder aspectRatio(float value);
        public abstract Article build();
    }

    public static Builder builder() {
        return new AutoValue_Article.Builder();
    }
}
