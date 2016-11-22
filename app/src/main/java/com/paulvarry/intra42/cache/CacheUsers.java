package com.paulvarry.intra42.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.paulvarry.intra42.api.User;
import com.paulvarry.intra42.oauth.ServiceGenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CacheUsers {

    private static final String TAG = "Cache users";

    private static final String TABLE_NAME = "users";

    static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_LOGIN_TYPE = "TEXT";
    private static final String COLUMN_LOGIN = "login";

    private static final String COLUMN_DATA_TYPE = "TEXT";
    private static final String COLUMN_DATA = "data";

    private static final String COLUMN_CACHED_TYPE = "datetime";
    private static final String COLUMN_CACHED = "cached_at";

    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " " + COLUMN_ID_TYPE + COMMA_SEP +
                    COLUMN_LOGIN + " " + COLUMN_LOGIN_TYPE + COMMA_SEP +
                    COLUMN_DATA + " " + COLUMN_DATA_TYPE + COMMA_SEP +
                    COLUMN_CACHED + " " + COLUMN_CACHED_TYPE +
                    ")";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CacheUsers() {
    }

    public static boolean isCached(CacheSQLiteHelper base, String login) {
        SQLiteDatabase db = base.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_ID,
                COLUMN_DATA,
                COLUMN_CACHED
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = COLUMN_LOGIN + " = ?";
        String[] selectionArgs = {login};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                COLUMN_ID + " DESC";

        Cursor c = db.query(
                TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        int columnIndexData = c.getColumnIndex(COLUMN_DATA);
        c.moveToFirst();

        if (columnIndexData == -1 || c.getCount() == 0 || c.isNull(columnIndexData)) {
            c.close();
            return false;
        } else {
            c.close();
            return true;
        }
    }

    public static long put(CacheSQLiteHelper base, User user) {
        return put(base, user, ServiceGenerator.getGson().toJson(user));
    }

    public static long put(CacheSQLiteHelper base, User user, String gson) {
        // Gets the data repository in write mode
        SQLiteDatabase db = base.getWritableDatabase();

        String[] l = {String.valueOf(user.id)};
        db.delete(TABLE_NAME, COLUMN_ID + "=?", l);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, user.id);
        values.put(COLUMN_LOGIN, user.login);
        values.put(COLUMN_DATA, gson);

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        values.put(COLUMN_CACHED, date.format(new Date()));

        // Insert the new row, returning the primary key value of the new row
        return db.insert(TABLE_NAME, null, values);
    }

    public static User get(CacheSQLiteHelper base, String login) {
        SQLiteDatabase db = base.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_ID,
                COLUMN_DATA,
                COLUMN_CACHED
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = COLUMN_LOGIN + " = ?";
        String[] selectionArgs = {login};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                COLUMN_ID + " DESC";

        Cursor c = db.query(
                TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        int columnIndexData = c.getColumnIndex(COLUMN_DATA);
        int columnIndexCachedAt = c.getColumnIndex(COLUMN_CACHED);
        c.moveToFirst();

        if (columnIndexData == -1 || c.getCount() == 0 || c.isNull(columnIndexData))
            return null;

        String item = c.getString(columnIndexData);

        User user = ServiceGenerator.getGson().fromJson(item, User.class);
        if (columnIndexCachedAt != -1 &&
                columnIndexCachedAt != 0 &&
                !c.isNull(columnIndexCachedAt)) {
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                user.local_cachedAt = date.parse(c.getString(columnIndexCachedAt));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        c.close();
        return user;
    }

}