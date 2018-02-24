package com.paulvarry.intra42.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ApiServiceClusterMapContribute;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Master;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.cache.CacheCampus;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClusterMapContributeUtils {

    public static final int MINUTE_LOCK = 5;
    private static final String KEY_POST_KEY = "key";
    private static final String KEY_POST_PAD = "pad";
    private static final String KEY_POST_MONOSPACE = "monospace";

    static public void loadClusterMap(final Context context, ApiServiceClusterMapContribute api, final Master master, final LoadClusterMapCallback callback) {

        final ProgressDialog dialog = ProgressDialog.show(context, null,
                context.getString(R.string.info_loading_please_wait), true);
        dialog.show();

        Call<Cluster> call = api.getCluster(master.key);
        call.enqueue(new Callback<Cluster>() {
            @Override
            public void onResponse(Call<Cluster> call, Response<Cluster> response) {
                dialog.cancel();
                Cluster body;
                if ((body = response.body()) != null)
                    callback.finish(master, body, cookieRetriever(response));
                else
                    callback.error(context.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response));
            }

            @Override
            public void onFailure(Call<Cluster> call, Throwable t) {
                dialog.cancel();
                callback.error(t.getMessage());
            }
        });
    }

    static public void loadMaster(final Context context, ApiServiceClusterMapContribute api, final LoadMasterCallback callback) {

        final ProgressDialog dialog = ProgressDialog.show(context, null,
                context.getString(R.string.info_loading_please_wait), true);
        dialog.show();

        Call<List<Master>> call = api.getMasters();
        call.enqueue(new Callback<List<Master>>() {
            @Override
            public void onResponse(Call<List<Master>> call, Response<List<Master>> response) {
                dialog.cancel();
                List<Master> body;
                if ((body = response.body()) != null)
                    callback.finish(body);
                else
                    callback.error(context.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response));
            }

            @Override
            public void onFailure(Call<List<Master>> call, Throwable t) {
                dialog.cancel();
                callback.error(t.getMessage());
            }
        });
    }

    static public void loadClusterMapAndLock(final Activity activity, final AppClass app, final Master master, final LoadClusterMapCallback callback) {

        final ProgressDialog dialog = ProgressDialog.show(activity, null, activity.getString(R.string.info_loading_please_wait), true);
        dialog.show();

        final ApiServiceClusterMapContribute api = app.getApiServiceClusterMapContribute();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String cookieMaster;

                Master apiMaster = null;

                try {

                    // get master
                    Response<List<Master>> responseMaster = api.getMasters().execute();
                    List<Master> bodyMaster;
                    if (!responseMaster.isSuccessful() || (bodyMaster = responseMaster.body()) == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    cookieMaster = cookieRetriever(responseMaster);
                    for (Master m : bodyMaster)
                        if (m.key.contentEquals(master.key) && m.url.contentEquals(master.url))
                            apiMaster = m;
                    if (!canIEdit(apiMaster, app)) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_data_locked), dialog, callback);
                        return;
                    }

                    // lock
                    apiMaster.locked_at = new Date();
                    apiMaster.locked_by = app.me.login;

                    Response<Void> responseSaveMaster = api.save(getBody(bodyMaster), cookieMaster).execute();
                    if (!responseSaveMaster.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }

                    // get cluster
                    final Response<Cluster> responseCluster = api.getCluster(master.key).execute();
                    if (!responseCluster.isSuccessful() || responseCluster.body() == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    final String cookieCluster = cookieRetriever(responseCluster);
                    final Master finalApiMaster = apiMaster;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            callback.finish(finalApiMaster, responseCluster.body(), cookieCluster);
                        }
                    });

                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            callback.error(e.getLocalizedMessage());
                        }
                    });
                }


            }
        }).start();
    }

    static public void saveClusterMap(final Activity activity, final AppClass app, final Master master, final Cluster cluster, final SaveClusterMapCallback callback) {
        saveClusterMap(activity, app, master, cluster, false, callback);
    }

    static public void saveClusterMap(final Activity activity, final AppClass app, final Master master, final Cluster cluster, final boolean createCluster, final SaveClusterMapCallback callback) {

        final ProgressDialog dialog = ProgressDialog.show(activity, null, activity.getString(R.string.info_loading_please_wait), true);
        dialog.show();

        final ApiServiceClusterMapContribute api = app.getApiServiceClusterMapContribute();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String cookieMaster;
                String cookieCluster;

                Master apiMaster = null;

                try {

                    Response<List<Master>> responseMaster = api.getMasters().execute();
                    List<Master> bodyMaster;
                    if (!responseMaster.isSuccessful() || (bodyMaster = responseMaster.body()) == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    cookieMaster = cookieRetriever(responseMaster);
                    //if (!createCluster) {
                    for (Master m : bodyMaster)
                        if (m.key.contentEquals(master.key) && m.url.contentEquals(master.url))
                            apiMaster = m;
                    //} else {
                    //    bodyMaster.add(master);
                    //    apiMaster = master;
                    //}

                    if (apiMaster == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    if (!canIEdit(apiMaster, app)) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_data_locked), dialog, callback);
                        return;
                    }

                    Response responseCluster = api.getClusterEmpty(apiMaster.key).execute();
                    if (!responseCluster.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    cookieCluster = cookieRetriever(responseCluster);

                    //save cluster
                    Response<Void> responseSaveMap = api.save(getBody(apiMaster, cluster), cookieCluster).execute();
                    if (!responseSaveMap.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }

                    //save master
                    apiMaster.locked_by = null;
                    apiMaster.locked_at = null;

                    apiMaster.name = "id: " + String.valueOf(cluster.campusId) + " - " + cluster.name;
                    List<Campus> campus = CacheCampus.getAllowInternet(app.cacheSQLiteHelper, app);
                    if (campus != null) {
                        for (Campus c : campus)
                            if (c.id == cluster.campusId)
                                apiMaster.name = c.name + " - " + cluster.name;
                    }

                    Response<Void> responseSaveMaster = api.save(getBody(bodyMaster), cookieMaster).execute();
                    if (!responseSaveMaster.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            callback.finish();
                        }
                    });

                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            callback.error(e.getLocalizedMessage());
                        }
                    });
                }

            }
        }).start();

    }

    public static void createCluster(final AppClass app, final Activity activity, final CreateClusterMapCallback callback) {
        final ProgressDialog dialog = ProgressDialog.show(activity, null, activity.getString(R.string.info_loading_please_wait), true);
        dialog.show();

        final ApiServiceClusterMapContribute api = app.getApiServiceClusterMapContribute();

        new Thread(new Runnable() {
            @Override
            public void run() {

                final String regex = "pad_key\\s+=\\s+'([^']+)?';\\s*url_key\\s+=\\s+'([^']+)?';"; // with name "pad_key\s+=\s+'(?<padkey>[^']+)?';\s*url_key\s+=\s+'(?<urlkey>[^']+)?';"

                try {

                    Response<ResponseBody> responseFirst = api.getMainPage().execute();
                    if (!responseFirst.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    String bodyFirst = responseFirst.body().string();

                    final Pattern patternFirst = Pattern.compile(regex);
                    final Matcher matcherFirst = patternFirst.matcher(bodyFirst);

                    if (!matcherFirst.find())
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);

                    final Master newMaster = new Master(null, matcherFirst.group(1), matcherFirst.group(2));

                    //save cluster
                    String cookie = cookieRetriever(responseFirst);
                    Response<Void> responseSaveMap = api.save(getBody(newMaster, "{}"), cookie).execute();
                    if (!responseSaveMap.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }


                    // -------


                    Response<ResponseBody> response = api.getMainPage().execute();
                    if (!response.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    String body = response.body().string();

                    final Pattern pattern = Pattern.compile(regex);
                    final Matcher matcher = pattern.matcher(body);

                    if (!matcher.find())
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);

                    newMaster.key = matcher.group(1);
                    newMaster.url = matcher.group(2);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            callback.finish(newMaster);
                        }
                    });

                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            callback.error(e.getLocalizedMessage());
                        }
                    });
                }

            }
        }).start();

    }

    private static void returnError(@NonNull Activity activity, final String error, final Dialog dialog, final SaveClusterMapCallback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                callback.error(error);
            }
        });
    }

    private static void returnError(@NonNull Activity activity, final String error, final Dialog dialog, final LoadClusterMapCallback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                callback.error(error);
            }
        });
    }

    private static void returnError(@NonNull Activity activity, final String error, final Dialog dialog, final CreateClusterMapCallback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                callback.error(error);
            }
        });
    }

    static public boolean canIEdit(Master master, AppClass app) {
        if (master == null || app == null || app.me == null)
            return false;

        if (master.locked_by == null || master.locked_at == null)
            return true;

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, -MINUTE_LOCK);

        // in case of over du || date is still in lock time
        return master.locked_at.before(c.getTime()) || master.locked_by.contentEquals(app.me.login);
    }

    static private HashMap<String, String> getBody(Master master, Object data) {
        HashMap<String, String> mapCluster = new HashMap<>();
        mapCluster.put(KEY_POST_KEY, master.key);
        mapCluster.put(KEY_POST_PAD, ServiceGenerator.getGson().toJson(data));
        mapCluster.put(KEY_POST_MONOSPACE, "1");
        return mapCluster;
    }

    static private HashMap<String, String> getBody(Object data) {
        HashMap<String, String> mapCluster = new HashMap<>();
        mapCluster.put(KEY_POST_KEY, "q79vdcc1t");
        mapCluster.put(KEY_POST_PAD, ServiceGenerator.getGson().toJson(data));
        mapCluster.put(KEY_POST_MONOSPACE, "1");
        return mapCluster;
    }

    static private String cookieRetriever(Response response) {
        if (response == null)
            return null;
        String cookie = response.headers().get("set-cookie");
        if (cookie == null)
            return null;
        return cookie.substring(0, cookie.indexOf(';'));

    }

    public interface LoadClusterMapCallback {

        void finish(final Master master, final Cluster cluster, String cookie);

        void error(String error);
    }

    public interface SaveClusterMapCallback {

        void finish();

        void error(String error);

    }

    public interface CreateClusterMapCallback {

        void finish(final Master masters);

        void error(String error);

    }

    public interface LoadMasterCallback {

        void finish(final List<Master> masters);

        void error(String error);

    }

}
