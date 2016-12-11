package com.paulvarry.intra42;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.AndroidRuntimeException;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.reflect.TypeToken;
import com.paulvarry.intra42.Tools.Pagination;
import com.paulvarry.intra42.Tools.Token;
import com.paulvarry.intra42.activity.MainActivity;
import com.paulvarry.intra42.api.AccessToken;
import com.paulvarry.intra42.api.Campus;
import com.paulvarry.intra42.api.Cursus;
import com.paulvarry.intra42.api.CursusUsers;
import com.paulvarry.intra42.api.Tags;
import com.paulvarry.intra42.api.User;
import com.paulvarry.intra42.cache.CacheSQLiteHelper;
import com.paulvarry.intra42.notifications.AlarmReceiverNotifications;
import com.paulvarry.intra42.oauth.ServiceGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppClass extends Application {

    public static final String PREFS_APP_VERSION = "prefs_app_version";
    public static final String PREFS_NAME = "intra_prefs";
    public static final String PREFS_API_TOKEN = "api_token";
    public static final String CACHE_API_ME = "cache_me";
    public static final String CACHE_API_CAMPUS = "cache_campus";
    public static final String CACHE_API_CURSUS = "cache_cursus";
    public static final String CACHE_API_TAGS = "cache_tags";

    private static AppClass sInstance;
    public List<CursusUsers> cursus;
    public List<Cursus> allCursus;
    public List<Campus> allCampus;
    public List<Tags> allTags;
    public User me;

    public AccessToken accessToken;
    public CacheSQLiteHelper usersDbHelper;
    @Nullable
    public DatabaseReference firebaseRefFriends;

    public static AppClass instance() {
        return sInstance;
    }

    public static <T> List<T> getCache(List<T> list, Call<List<T>> call, SharedPreferences.Editor editor, String cache_key) {
        try {
            Response<List<T>> c = call.execute();
            list = c.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null && editor != null) {
            editor.putString(cache_key, ServiceGenerator.getGson().toJson(list));
            editor.apply();
        }
        return list;
    }

    public static <T> List<T> saveCache(final TypeToken<List<T>> type, List<T> list, SharedPreferences.Editor editor, Call<List<T>> call, String cache_key, SharedPreferences sharedPreferences, boolean forceAPI) {
        String string = sharedPreferences.getString(cache_key, "");
        if (string.isEmpty() || forceAPI)
            list = getCache(list, call, editor, cache_key);
        else
            try {
                list = ServiceGenerator.getGson().fromJson(string, type.getType());
            } catch (Exception e) {
                Log.e("error", "json parsing error", e);
                list = getCache(list, call, editor, cache_key);
            }
        return list;
    }

    @Nullable
    public static List<Tags> getCacheTags(SharedPreferences.Editor editor, ApiService api) {
        List<Tags> list = new ArrayList<>();
        int i = 0;
        int pageSize = 100;

        try {
            while (i < 10 && Pagination.canAdd(list, pageSize)) {
                Response<List<Tags>> c = api.getTags(pageSize, Pagination.getPage(list, pageSize)).execute();
                List<Tags> tmp = c.body();
                if (!c.isSuccessful())
                    break;
                list.addAll(tmp);
                ++i;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!list.isEmpty() && editor != null) {
            editor.putString(CACHE_API_TAGS, ServiceGenerator.getGson().toJson(list));
            editor.apply();
        }
        if (list.isEmpty())
            return null;
        return list;
    }

    public static void scheduleAlarm(Context context) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, AlarmReceiverNotifications.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, AlarmReceiverNotifications.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        boolean notifications_allow = settings.getBoolean("notifications_allow", false);
        int notifications_frequency = Integer.parseInt(settings.getString("notifications_frequency", "-1"));

        if (notifications_allow && notifications_frequency != -1)
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis + 60000, 60000 * notifications_frequency, pIntent);

        Log.d("alarm", "schedule");
    }

    public static void unscheduleAlarm(Context context) {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, AlarmReceiverNotifications.class);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, AlarmReceiverNotifications.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);

        Log.d("alarm", "unschedule");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        accessToken = Token.getTokenFromShared(this);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String strUser = sharedPreferences.getString(CACHE_API_ME, "");
        if (!strUser.isEmpty())
            me = User.fromString(strUser);

        usersDbHelper = new CacheSQLiteHelper(this);

        initFirebase();
    }

    public ApiService getApiService() {
        return ServiceGenerator.createService(ApiService.class, accessToken, this, this);
    }

    /**
     * Method to init some data for user (/me, cursus, campus).
     *
     * @return status
     */
    public boolean initUser(boolean forceAPI) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ApiService api = getApiService();

        sInstance = this;

        String strUser = sharedPreferences.getString(CACHE_API_ME, "");
        if (strUser.isEmpty() || forceAPI) {
            me = User.me(api);
            if (me != null)
                editor.putString(CACHE_API_ME, me.toString());
        } else
            me = User.fromString(strUser);

        initFirebase();

        if (me == null)
            return false;
        else {
            cursus = me.cursusUsers;

            TypeToken<List<Cursus>> typeTokenCursus = new TypeToken<List<Cursus>>() {
            };
            TypeToken<List<Campus>> typeTokenCampus = new TypeToken<List<Campus>>() {
            };

            allCursus = saveCache(typeTokenCursus, allCursus, editor, api.getCursus(), CACHE_API_CURSUS, sharedPreferences, forceAPI);
            allCampus = saveCache(typeTokenCampus, allCampus, editor, api.getCampus(), CACHE_API_CAMPUS, sharedPreferences, forceAPI);

            String strTags = sharedPreferences.getString(CACHE_API_TAGS, "");
            if (strTags.isEmpty() || forceAPI)
                allTags = getCacheTags(editor, api);
            else
                try {
                    allTags = ServiceGenerator.getGson().fromJson(strTags, new TypeToken<List<Tags>>() {
                    }.getType());
                } catch (Exception e) {
                    Log.e("error", "json parsing error", e);
                    allTags = getCacheTags(editor, api);
                }
            editor.apply();
            return true;
        }

    }

    void initFirebase() {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            if (me != null)
                firebaseRefFriends = database.getReference("users").child(me.login).child("friends");
        } catch (IllegalStateException e) {

        }
    }

    public void refreshUser(final Runnable callback) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        getApiService().getUserMe().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    me = response.body();
                    if (me != null)
                        editor.putString(CACHE_API_ME, me.toString());
                    editor.apply();
                    callback.run();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

    }

    public void logout() {
        me = null;
        accessToken = null;
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(CACHE_API_ME);
        editor.apply();
        Token.removeToken(this);
    }

    public void logoutAndRedirect() {
        logout();

        Intent i = new Intent(this, MainActivity.class);
        try {
            startActivity(i);
        } catch (AndroidRuntimeException e) {
            e.printStackTrace();
        }

    }

//    interface getCall<T> {
//        Call<List<T>> get();
//    }
//    class Func1 implements getCall<Cursus>
//    {
//        @Override
//        public Call<List<Cursus>> get() {
//            return null;
//        }
//    }
}
