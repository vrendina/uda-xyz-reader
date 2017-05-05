package io.levelsoftware.xyzreader.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ArticleProvider extends ContentProvider{

    private ArticleDatabaseHelper dbHelper;

    public static final int CODE_ARTICLE = 100;
    public static final int CODE_ARTICLE_WITH_ID = 101;

    private static UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);

        // content://io.levelsoftware.xyzreader/articles
        matcher.addURI(ArticleContract.CONTENT_AUTHORITY, ArticleContract.Article.PATH,
                CODE_ARTICLE);

        // content://io.levelsoftware.xyzreader/articles/#
        matcher.addURI(ArticleContract.CONTENT_AUTHORITY, ArticleContract.Article.PATH + "/#",
                CODE_ARTICLE_WITH_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ArticleDatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch(matcher.match(uri)) {
            case CODE_ARTICLE:
                cursor = db.query(ArticleContract.Article.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_ARTICLE_WITH_ID:
                selectionArgs = new String[]{uri.getLastPathSegment()};

                cursor = db.query(ArticleContract.Article.TABLE_NAME,
                        projection,
                        ArticleContract.Article._ID + " = ? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Invalid uri for query: " + uri);
        }

        Context context = getContext();
        if(context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case CODE_ARTICLE:
                return ArticleContract.Article.CONTENT_TYPE;
            case CODE_ARTICLE_WITH_ID:
                return ArticleContract.Article.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        switch(matcher.match(uri)) {
            case CODE_ARTICLE:
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                long id = db.insert(ArticleContract.Article.TABLE_NAME, null, values);
                if(id != -1) {
                    Uri insertedUri = ArticleContract.Article.buildArticleUri(id);

                    Context context = getContext();
                    if(context != null) {
                        context.getContentResolver().notifyChange(insertedUri, null);
                    }
                    return insertedUri;
                }
                break;

            default:
                throw new IllegalArgumentException("Invalid uri for insert operation: " + uri);
        }
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (matcher.match(uri)) {
            case CODE_ARTICLE:
                db.beginTransaction();

                int insertCount = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                ArticleContract.Article.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                Context context = getContext();
                if (context != null) {
                    context.getContentResolver().notifyChange(uri, null);
                }

                return insertCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsDeleted;
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch(matcher.match(uri)) {

            case CODE_ARTICLE:
                numRowsDeleted = db.delete(ArticleContract.Article.TABLE_NAME, null, null);
                break;

            case CODE_ARTICLE_WITH_ID:
                selectionArgs = new String[]{uri.getLastPathSegment()};

                numRowsDeleted = db.delete(
                        ArticleContract.Article.TABLE_NAME,
                        ArticleContract.Article._ID + " = ? ",
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Invalid uri for delete operation: " + uri);
        }

        if (numRowsDeleted != 0) {
            Context context = getContext();
            if(context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new RuntimeException("ContentProvider 'update' will not be implemented, use delete then bulkInsert");
    }
}
