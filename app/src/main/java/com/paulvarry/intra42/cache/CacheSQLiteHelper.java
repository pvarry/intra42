package com.paulvarry.intra42.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CacheSQLiteHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Cache.db";

    public CacheSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CacheUsers.SQL_CREATE_TABLE);
        db.execSQL(CacheTags.SQL_CREATE_TABLE);
        db.execSQL(CacheCampus.SQL_CREATE_TABLE);
        db.execSQL(CacheCursus.SQL_CREATE_TABLE);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(CacheUsers.SQL_DROP_TABLE);
        db.execSQL(CacheTags.SQL_DROP_TABLE);
        db.execSQL(CacheCampus.SQL_DROP_TABLE);
        db.execSQL(CacheCursus.SQL_DROP_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}