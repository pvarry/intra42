package com.paulvarry.intra42;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.paulvarry.intra42.activities.MainActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.ApiServiceAuthServer;
import com.paulvarry.intra42.api.ApiServiceCantina;
import com.paulvarry.intra42.api.ApiServiceClusterMapContribute;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.Users;
import com.paulvarry.intra42.api.tools42.AccessToken;
import com.paulvarry.intra42.cache.CacheCampus;
import com.paulvarry.intra42.cache.CacheCursus;
import com.paulvarry.intra42.cache.CacheSQLiteHelper;
import com.paulvarry.intra42.cache.CacheUsers;
import com.paulvarry.intra42.notifications.AlarmReceiverNotifications;
import com.paulvarry.intra42.notifications.NotificationsJobService;
import com.paulvarry.intra42.notifications.NotificationsUtils;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.ThemeHelper;
import com.paulvarry.intra42.utils.Token;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class AppClass extends Application {

    public static final String PREFS_APP_VERSION = "prefs_app_version";
    public static final String PREFS_NAME = "intra_prefs";
    public static final String PREFS_CACHE_LAST_CHECK = "cache_last_check";
    public static final String PREFS_API_TOKEN = "api_token";
    public static final String API_ME_LOGIN = "me_login";

    private static AppClass sInstance;
    public List<CursusUsers> cursus;
    public Users me;

    public CacheSQLiteHelper cacheSQLiteHelper;
    @Nullable
    public DatabaseReference firebaseRefFriends;
    public FirebaseAnalytics mFirebaseAnalytics;

    public AppSettings.Theme.EnumTheme themeSettings;
    @StyleRes
    public
    int themeRes;

    public static AppClass instance() {
        return sInstance;
    }

    public static void scheduleAlarm(Context context) {

        if (!ServiceGenerator.have42Token())
            return;

        SharedPreferences settings = AppSettings.getSharedPreferences(context);
        if (!AppSettings.Notifications.getNotificationsAllow(settings))
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NotificationsJobService.schedule(context);
        } else {

            // Construct an intent that will execute the AlarmReceiver
            Intent intent = new Intent(context, AlarmReceiverNotifications.class);

            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntent = PendingIntent.getBroadcast(context, AlarmReceiverNotifications.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


            int notificationsFrequency = AppSettings.Notifications.getNotificationsFrequency(settings);

            int epoch = 1451607360; // Human time (GMT): Fri, 01 Jan 2016 00:16:00 GMT
            if (notificationsFrequency != -1)
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, epoch, 60000 * notificationsFrequency, pIntent);
        }
        Log.d("Notification", "schedule");
    }

    public static void unscheduleAlarm(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.cancel(1);
        } else {


            // Construct an intent that will execute the AlarmReceiver
            Intent intent = new Intent(context, AlarmReceiverNotifications.class);

            // Create a PendingIntent to be triggered when the alarm goes off
            final PendingIntent pIntent = PendingIntent.getBroadcast(context, AlarmReceiverNotifications.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pIntent);

        }

        Log.d("Notification", "unschedule");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ServiceGenerator.init(this);
        cacheSQLiteHelper = new CacheSQLiteHelper(this);
        sInstance = this;

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences(AppClass.PREFS_NAME, MODE_PRIVATE);
        int appVersion = sharedPreferences.getInt(AppClass.PREFS_APP_VERSION, 0);
        if (appVersion == 0 || appVersion != BuildConfig.VERSION_CODE) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt(AppClass.PREFS_APP_VERSION, BuildConfig.VERSION_CODE);

            if (appVersion <= 20171113) {
                SharedPreferences.Editor pref = AppSettings.getSharedPreferences(this).edit();
                pref.putBoolean("should_sync_friends", true);
                pref.apply();
            }
            if (appVersion <= 20171126) {
                if (cacheSQLiteHelper != null) {
                    cacheSQLiteHelper.getWritableDatabase().execSQL("DELETE FROM users;");
                }
            }
            if (appVersion <= 20171208) {
                Date now = new Date();
                edit.putString(AppClass.PREFS_CACHE_LAST_CHECK, ISO8601Utils.format(now));
            }
            if (appVersion <= 20180310)
                edit.remove("POEditor_activated");
            if (appVersion <= 20180330) {
                Log.d("Start param migration", "theme");
                SharedPreferences pref = AppSettings.getSharedPreferences(this);

                String string = pref.getString(AppSettings.Theme.THEME, "default");
                SharedPreferences.Editor editor = pref.edit();

                switch (string) {
                    case "order":
                        editor.putString(AppSettings.Theme.THEME, "red");
                        break;
                    case "assembly":
                        editor.putString(AppSettings.Theme.THEME, "purple");
                        break;
                    case "federation":
                        editor.putString(AppSettings.Theme.THEME, "blue");
                        break;
                    case "alliance":
                        editor.putString(AppSettings.Theme.THEME, "green");
                        break;
                }

                editor.commit();
            }

            edit.apply();
        }

        String login = sharedPreferences.getString(API_ME_LOGIN, "");

        if (CacheUsers.isCached(cacheSQLiteHelper, login))
            me = CacheUsers.get(cacheSQLiteHelper, login);

        ThemeHelper.setTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationsUtils.generateNotificationChannel(this);
        }

        initFirebase();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (isExternalStorageWritable() && (AppSettings.Advanced.getAllowSaveLogs(this) | BuildConfig.DEBUG)) {

            File appDirectory = getExternalFilesDir(null);
            File logDirectory = new File(appDirectory + "/logs");
            Date now = new Date();
            File logFile = new File(logDirectory, "logcat_" + ISO8601Utils.format(now) + ".txt");

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            // clear the previous logcat and then write the new one to the file
            try {
                logFile.createNewFile();
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (isExternalStorageReadable()) {
            // only readable
        } else {
            // not accessible
        }
    }

    public ApiService getApiService() {
        return ServiceGenerator.createService(ApiService.class, this, true);
    }

    public ApiService42Tools getApiService42Tools() {
        return ServiceGenerator.createService(ApiService42Tools.class, this, false);
    }

    public ApiService getApiServiceDisableRedirectActivity() {
        return ServiceGenerator.createService(ApiService.class, this, false);
    }

    public ApiServiceCantina getApiServiceCantina() {
        return ServiceGenerator.createService(ApiServiceCantina.class, this, false);
    }

    public ApiServiceAuthServer getApiServiceAuthServer() {
        return ServiceGenerator.createService(ApiServiceAuthServer.class, this, false);
    }

    public ApiServiceClusterMapContribute getApiServiceClusterMapContribute() {
        return ServiceGenerator.createService(ApiServiceClusterMapContribute.class, this, false);
    }

    /**
     * Method to init some data for user (/me, cursus, campus, tags).
     *
     * @return status
     */
    public boolean initCache(boolean forceAPI) {
        return initCache(forceAPI, null);
    }

    /**
     * Method to init some data for user (/me, cursus, campus, tags).
     *
     * @return status
     */
    public boolean initCache(boolean forceAPI, MainActivity mainActivity) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ApiService api = getApiService();

        String login = sharedPreferences.getString(API_ME_LOGIN, "");
        if (mainActivity != null)
            mainActivity.updateViewState(getString(R.string.info_loading_cache), getString(R.string.info_loading_current_user), 1, 6);

        if (login.isEmpty() || !CacheUsers.isCached(cacheSQLiteHelper, login) || forceAPI) {
            me = Users.me(api);
            if (me != null) {
                me.local_cachedAt = new Date();
                CacheUsers.put(cacheSQLiteHelper, me);
                editor.putString(API_ME_LOGIN, me.login);
            }
        } else
            me = CacheUsers.get(cacheSQLiteHelper, login);

        if (me == null)
            return false;

        cursus = me.cursusUsers;
        initFirebase();
        ThemeHelper.setTheme(this); // init theme

        String cacheLastCheckPref = sharedPreferences.getString(PREFS_CACHE_LAST_CHECK, null);
        Date cacheLastCheck = null;
        Calendar lastValidCache = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Date now = lastValidCache.getTime();

        lastValidCache.add(Calendar.DAY_OF_YEAR, -30);

        if (cacheLastCheckPref != null)
            try {
                cacheLastCheck = ISO8601Utils.parse(cacheLastCheckPref, new ParsePosition(0));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        if (cacheLastCheck == null)
            cacheLastCheck = new Date(0);

        if (cacheLastCheckPref == null || cacheLastCheck.before(lastValidCache.getTime())) {
            if (mainActivity != null)
                mainActivity.updateViewState(null, getString(R.string.cursus), 1, 3);
            CacheCursus.getAllowInternet(cacheSQLiteHelper, this);
            if (mainActivity != null)
                mainActivity.updateViewState(null, getString(R.string.campus), 2, 3);
            CacheCampus.getAllowInternet(cacheSQLiteHelper, this);
//            if (mainActivity != null) //remove tag caching
//                mainActivity.updateViewState(null, getString(R.string.tags), 3, 6);
//            CacheTags.refreshCache(cacheSQLiteHelper, this, cacheLastCheck, now);
            if (mainActivity != null)
                mainActivity.updateViewState(null, getString(R.string.info_api_finishing), 3, 3);
            //TODO: add integration to force use API with a cache manager in the UI !!
            editor.putString(PREFS_CACHE_LAST_CHECK, ISO8601Utils.format(now));
        }
        editor.apply();

        if (!ServiceGenerator.have42ToolsToken() && ServiceGenerator.have42Token()) {
            ApiService42Tools client = ServiceGenerator.createService(ApiService42Tools.class);
            Call<AccessToken> call = client.getAccessToken(ServiceGenerator.getToken().accessToken);
            try {
                Response<AccessToken> ret = call.execute();
                if (ret != null && ret.isSuccessful()) {
                    Token.save(this, ret.body());
                    ServiceGenerator.setToken(ret.body());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;

    }

    void initFirebase() {
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            if (me != null)
                firebaseRefFriends = database.getReference("users").child(me.login).child("friends");
        } catch (IllegalStateException | NullPointerException e) {
            Log.e("Firebase", "Fail to init friends with firebase");
        }
    }

    public void logout() {
        me = null;
        ServiceGenerator.logout();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(API_ME_LOGIN);
        editor.apply();
        Token.removeToken(this);
    }

    public void logoutAndRedirect() {
        logout();
        MainActivity.openActivity(this);
    }

    public boolean userIsLogged(boolean canOpenActivity) {
        if (me == null || !ServiceGenerator.have42Token()) {
            if (canOpenActivity)
                MainActivity.openActivity(this);
            return false;
        }
        return true;
    }

    public boolean userIsLogged() {
        return userIsLogged(true);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

}
