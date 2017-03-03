package com.paulvarry.intra42.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.Expertises;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CacheExpertises {

    private static final String TAG = "Cache expertises";

    private static final String TABLE_NAME = "expertises";

    static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_NAME_TYPE = "TEXT";
    private static final String COLUMN_NAME = "name";

    private static final String COLUMN_DATA_TYPE = "TEXT";
    private static final String COLUMN_DATA = "data";

    private static final String COLUMN_CACHED_TYPE = "datetime";
    private static final String COLUMN_CACHED = "cached_at";

    static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " " + COLUMN_ID_TYPE + COMMA_SEP +
                    COLUMN_NAME + " " + COLUMN_NAME_TYPE + COMMA_SEP +
                    COLUMN_DATA + " " + COLUMN_DATA_TYPE + COMMA_SEP +
                    COLUMN_CACHED + " " + COLUMN_CACHED_TYPE +
                    ")";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private CacheExpertises() {
    }

    private static boolean isCached(CacheSQLiteHelper base, Expertises expertises) {
        return isCached(base, expertises.id);
    }

    public static boolean isCached(CacheSQLiteHelper base, int id) {
        SQLiteDatabase db = base.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_ID,
                COLUMN_DATA,
                COLUMN_CACHED
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

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

    public static long put(CacheSQLiteHelper base, Expertises expertises) {
        return put(base, expertises, ServiceGenerator.getGson().toJson(expertises));
    }

    public static long put(CacheSQLiteHelper base, Expertises expertises, String gson) {
        // Gets the data repository in write mode
        SQLiteDatabase db = base.getWritableDatabase();

        String[] l = {String.valueOf(expertises.id)};
        db.delete(TABLE_NAME, COLUMN_ID + "=?", l);

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, expertises.id);
        values.put(COLUMN_NAME, expertises.name);
        values.put(COLUMN_DATA, gson);

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        values.put(COLUMN_CACHED, date.format(new Date()));

        // Insert the new row, returning the primary key value of the new row
        return db.insert(TABLE_NAME, null, values);
    }

    public static void put(CacheSQLiteHelper base, List<Expertises> list) {
        if (list == null)
            return;
        for (Expertises t : list) {
            put(base, t);
        }
    }

    public static Expertises get(CacheSQLiteHelper base, int id) {
        SQLiteDatabase db = base.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_ID,
                COLUMN_DATA,
                COLUMN_CACHED
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

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

        if (columnIndexData == -1 || c.getCount() == 0 || c.isNull(columnIndexData))
            return null;

        String item = c.getString(columnIndexData);

        Expertises expertises = ServiceGenerator.getGson().fromJson(item, Expertises.class);

        c.close();
        return expertises;
    }

    public static List<Expertises> get(CacheSQLiteHelper base) {
        SQLiteDatabase db = base.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_ID,
                COLUMN_DATA,
                COLUMN_CACHED
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = COLUMN_NAME + " COLLATE NOCASE ASC";

        Cursor cursor = db.query(
                TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        List<Expertises> tagsList = new ArrayList<>();

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String data = cursor.getString(cursor.getColumnIndex(COLUMN_DATA));
            tagsList.add(ServiceGenerator.getGson().fromJson(data, Expertises.class));

            cursor.moveToNext();
        }

        cursor.close();
        if (tagsList.size() == 0)
            return null;
        return tagsList;
    }

    public static List<Expertises> getAllowInternet(CacheSQLiteHelper base, AppClass app) {
        SQLiteDatabase db = base.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_ID,
                COLUMN_DATA,
                COLUMN_CACHED
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = COLUMN_NAME + " COLLATE NOCASE ASC";

        Cursor cursor = db.query(
                TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        List<Expertises> tagsList = new ArrayList<>();
        Date lastAdded = null;

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            String data = cursor.getString(cursor.getColumnIndex(COLUMN_DATA));
            tagsList.add(ServiceGenerator.getGson().fromJson(data, Expertises.class));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date dateTmp = simpleDateFormat.parse(cursor.getString(cursor.getColumnIndex(COLUMN_CACHED)));
                if (lastAdded != null) {
                    if (dateTmp.after(lastAdded))
                        lastAdded = dateTmp;
                } else
                    lastAdded = dateTmp;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            cursor.moveToNext();
        }

        cursor.close();

        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_YEAR, -4);
        if (lastAdded == null || now.getTime().after(lastAdded)) {

            List<Expertises> tagsFromApi = Expertises.getAll(app.getApiService());
            put(base, tagsFromApi);
            return tagsFromApi;
        }

        return tagsList;
    }

}