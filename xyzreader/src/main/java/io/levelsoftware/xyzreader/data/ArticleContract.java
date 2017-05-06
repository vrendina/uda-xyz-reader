package io.levelsoftware.xyzreader.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableList;

public class ArticleContract {
    public static final String CONTENT_AUTHORITY = "io.levelsoftware.xyzreader";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class Article implements BaseColumns {

        public static final String PATH = "articles";
        public static final String TABLE_NAME = PATH;

        public static final Uri CONTENT_URI = BASE_URI.buildUpon()
                .appendPath(PATH)
                .build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.io.levelsoftware.xyzreader." + TABLE_NAME;
        public static final String CONTENT_TYPE_ITEM =
                "vnd.android.cursor.item/vnd.io.levelsoftware.xyzreader." + TABLE_NAME;

        public static final String COLUMN_SERVER_ID = "server_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_THUMB_URL = "thumb_url";
        public static final String COLUMN_PHOTO_URL = "photo_url";
        public static final String COLUMN_ASPECT_RATIO = "aspect_ratio";
        public static final String COLUMN_PUBLISHED_DATE = "published_date";

        public static final ImmutableList<String> COLUMNS = ImmutableList.of(
                _ID,
                COLUMN_SERVER_ID,
                COLUMN_TITLE,
                COLUMN_AUTHOR,
                COLUMN_BODY,
                COLUMN_THUMB_URL,
                COLUMN_PHOTO_URL,
                COLUMN_ASPECT_RATIO,
                COLUMN_PUBLISHED_DATE
        );

        public static Uri buildArticleUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(id)).build();
        }

    }
}
