package io.levelsoftware.xyzreader.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ArticleDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "xyzreader.db";
    private static final int DATABASE_VERSION = 3;

    public ArticleDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ArticleContract.Article.TABLE_NAME + " ("
                + ArticleContract.Article._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ArticleContract.Article.COLUMN_SERVER_ID + " TEXT,"
                + ArticleContract.Article.COLUMN_TITLE + " TEXT NOT NULL,"
                + ArticleContract.Article.COLUMN_AUTHOR + " TEXT NOT NULL,"
                + ArticleContract.Article.COLUMN_BODY + " TEXT NOT NULL,"
                + ArticleContract.Article.COLUMN_THUMB_URL + " TEXT NOT NULL,"
                + ArticleContract.Article.COLUMN_PHOTO_URL + " TEXT NOT NULL,"
                + ArticleContract.Article.COLUMN_ASPECT_RATIO + " REAL NOT NULL DEFAULT 1.5,"
                + ArticleContract.Article.COLUMN_PUBLISHED_DATE + " TEXT NOT NULL"
                + ")" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ArticleContract.Article.TABLE_NAME);
        onCreate(db);
    }
}
