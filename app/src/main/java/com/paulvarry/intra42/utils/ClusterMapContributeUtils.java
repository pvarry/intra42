package com.paulvarry.intra42.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ApiServiceClusterMapContribute;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Master;
import com.paulvarry.intra42.api.model.Campus;
import com.paulvarry.intra42.cache.CacheCampus;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClusterMapContributeUtils {

    public static final int MINUTE_LOCK = 5;

    static public void loadClusterMap(final Activity activity, final AppClass app, final Master master, final LoadClusterMapCallback callback) {

        if (app == null)
            return;

        final ProgressDialog dialog = ProgressDialog.show(activity, null, activity.getString(R.string.info_loading_please_wait), true);
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response<Cluster> responseCluster;
                Response<List<Master>> responseMaster;
                ApiServiceClusterMapContribute api = app.getApiServiceClusterMapContribute();

                try {
                    responseMaster = api.getMasters().execute();

                    List<Master> bodyMaster;
                    if (!responseMaster.isSuccessful() || (bodyMaster = responseMaster.body()) == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    Master apiMaster = null;
                    for (Master m : bodyMaster)
                        if (m.key.contentEquals(master.key))
                            apiMaster = m;

                    if (apiMaster == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_data_locked), dialog, callback);
                        return;
                    }
                    if (!canIEdit(apiMaster, app)) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_data_locked), dialog, callback);
                        return;
                    }

                    responseCluster = api.getCluster(master.key).execute();

                    final Cluster body;
                    if ((body = responseCluster.body()) != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.finish(master, body);
                                dialog.cancel();
                            }
                        });
                    } else
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);

                } catch (IOException e) {
                    e.printStackTrace();
                    returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                }
            }
        }).start();
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

                Master apiMaster = null;

                try {

                    // get master
                    Response<List<Master>> responseMaster = api.getMasters().execute();
                    List<Master> bodyMaster;
                    if (!responseMaster.isSuccessful() || (bodyMaster = responseMaster.body()) == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    for (Master m : bodyMaster)
                        if (m.key.contentEquals(master.key))
                            apiMaster = m;
                    if (!canIEdit(apiMaster, app)) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_data_locked), dialog, callback);
                        return;
                    }

                    // lock
                    apiMaster.locked_at = new Date();
                    apiMaster.locked_by = app.me.login;

                    Response<Void> responseSaveMaster = api.updateMaster(bodyMaster).execute();
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
                    final Master finalApiMaster = apiMaster;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            callback.finish(finalApiMaster, responseCluster.body());
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

    static public void saveClusterMapMetadata(final Activity activity, final AppClass app, final Master master, final Cluster cluster, final CreateSaveClusterMapCallback callback) {

        final ProgressDialog dialog = ProgressDialog.show(activity, null, activity.getString(R.string.info_loading_please_wait), true);
        dialog.show();

        final ApiServiceClusterMapContribute api = app.getApiServiceClusterMapContribute();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Master masterFromApi = null;

                try {

                    // get all master to verify lock
                    Response<List<Master>> responseMaster = api.getMasters().execute();
                    final List<Master> bodyMaster;
                    if (!responseMaster.isSuccessful() || (bodyMaster = responseMaster.body()) == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    for (Master m : bodyMaster)
                        if (m.key.contentEquals(master.key)) {
                            masterFromApi = m;
                            break;
                        }

                    if (masterFromApi == null) { // master not found
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    if (!canIEdit(masterFromApi, app)) { // master locked TODO: add overdue lock check
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_data_locked), dialog, callback);
                        return;
                    }

                    //save cluster
                    Response<Void> responseSaveMap = api.updateCluster(cluster, masterFromApi.key).execute();
                    if (!responseSaveMap.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }

                    //save master
                    masterFromApi.locked_by = null;
                    masterFromApi.locked_at = null;

                    masterFromApi.name = buildMasterName(app, cluster);

                    Response<Void> responseSaveMaster = api.updateMaster(bodyMaster).execute();
                    if (!responseSaveMaster.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            callback.finish(bodyMaster);
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

    static public void saveClusterMap(final Activity activity, final AppClass app, final Master master, final Cluster cluster, final CreateSaveClusterMapCallback callback) {

        final ProgressDialog dialog = ProgressDialog.show(activity, null, activity.getString(R.string.info_loading_please_wait), true);
        dialog.show();

        final ApiServiceClusterMapContribute api = app.getApiServiceClusterMapContribute();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Master masterFromApi = null;

                try {

                    // get all master to verify lock
                    Response<List<Master>> responseMaster = api.getMasters().execute();
                    final List<Master> bodyMaster;
                    if (!responseMaster.isSuccessful() || (bodyMaster = responseMaster.body()) == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    for (Master m : bodyMaster)
                        if (m.key.contentEquals(master.key)) {
                            masterFromApi = m;
                            break;
                        }

                    if (masterFromApi == null) { // master not found
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    if (!canISaveMap(masterFromApi, app)) { // master locked
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_data_locked), dialog, callback);
                        return;
                    }

                    //save cluster
                    Response<Void> responseSaveMap = api.updateCluster(cluster, masterFromApi.key).execute();
                    if (!responseSaveMap.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }

                    //save master
                    masterFromApi.locked_by = null;
                    masterFromApi.locked_at = null;

                    masterFromApi.name = buildMasterName(app, cluster);

                    Response<Void> responseSaveMaster = api.updateMaster(bodyMaster).execute();
                    if (!responseSaveMaster.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            callback.finish(bodyMaster);
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

    public static void createCluster(final AppClass app, final Activity activity, final Cluster cluster, final CreateSaveClusterMapCallback callback) {
        final ProgressDialog dialog = ProgressDialog.show(activity, null, activity.getString(R.string.info_loading_please_wait), true);
        dialog.show();

        final ApiServiceClusterMapContribute api = app.getApiServiceClusterMapContribute();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    Response<Void> responseCreateCluster = api.create(cluster).execute();
                    if (!responseCreateCluster.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }
                    String key = responseCreateCluster.headers().get("x-jsonblob");

                    final Master newMaster = new Master(buildMasterName(app, cluster), key);


                    //get masters
                    Response<List<Master>> responseApiMasters = api.getMasters().execute();
                    if (!responseApiMasters.isSuccessful() || responseApiMasters.body() == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }

                    List<Master> master = responseApiMasters.body();
                    master.add(newMaster);

                    // save masters
                    Response<Void> response = api.updateMaster(master).execute();
                    if (!response.isSuccessful()) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, callback);
                        return;
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            callback.finish(null);
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

    private static void returnError(@NonNull Activity activity, final String error, final Dialog dialog, final DefaultCallback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                callback.error(error);
            }
        });
    }

    private static void returnError(@NonNull Activity activity, final String error, final Dialog dialog, final boolean showDialog, final DefaultCallback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (showDialog)
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

    private static boolean canISaveMap(Master master, AppClass app) {
        if (master == null || app == null || app.me == null)
            return false;

        if (master.locked_by == null || master.locked_at == null)
            return false;

        Calendar lockMustBeMadeAfter = Calendar.getInstance();
        lockMustBeMadeAfter.add(Calendar.MINUTE, -MINUTE_LOCK);
        lockMustBeMadeAfter.add(Calendar.SECOND, -5); // add 5s extra

        return master.locked_at.after(lockMustBeMadeAfter.getTime()) &&
                master.locked_at.before(new Date()) &&
                master.locked_by.contentEquals(app.me.login);
    }

    static private String buildMasterName(AppClass app, Cluster cluster) {
        String name;

        if (cluster == null)
            return null;

        name = "id: " + String.valueOf(cluster.campusId) + " - " + cluster.name;
        List<Campus> campus = CacheCampus.getAllowInternet(app.cacheSQLiteHelper, app);
        if (campus != null) {
            for (Campus c : campus)
                if (c.id == cluster.campusId) {
                    name = c.name + " - " + cluster.name;
                    break;
                }
        }
        return name;
    }

    public static int getResId(Context context, int campus) {

        if (context == null)
            return 0;
        Resources resources = context.getResources();
        if (resources == null)
            return 0;
        return resources.getIdentifier("cluster_map_campus_" + campus, "raw", context.getPackageName());
    }

    public static void unlockCluster(final Activity activity, final AppClass app, final Master master, final boolean showDialog, final NoReturnCallback callback) {
        final ProgressDialog dialog = ProgressDialog.show(activity, null, activity.getString(R.string.info_loading_please_wait), true);
        if (showDialog) {
            dialog.show();
        }

        final ApiServiceClusterMapContribute api = app.getApiServiceClusterMapContribute();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Master masterFromApi = null;

                try {

                    // get all master to verify lock
                    Response<List<Master>> responseMaster = api.getMasters().execute();
                    final List<Master> bodyMaster;
                    if (!responseMaster.isSuccessful() || (bodyMaster = responseMaster.body()) == null) {
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, showDialog, callback);
                        return;
                    }
                    for (Master m : bodyMaster)
                        if (m.key.contentEquals(master.key)) {
                            masterFromApi = m;
                            break;
                        }

                    if (masterFromApi == null) { // master not found
                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, showDialog, callback);
                        return;
                    }
                    if (!canISaveMap(masterFromApi, app)) { // master locked

                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_data_locked), dialog, showDialog, callback);
                        return;
                    }

                    //save master
                    masterFromApi.locked_by = null;
                    masterFromApi.locked_at = null;

                    Response<Void> responseSaveMaster = api.updateMaster(bodyMaster).execute();
                    if (!responseSaveMaster.isSuccessful()) {

                        returnError(activity, activity.getString(R.string.cluster_map_contribute_error_fail_to_retrieve_data_response), dialog, showDialog, callback);
                        return;
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (showDialog)
                                dialog.dismiss();
                            callback.finish();
                        }
                    });

                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (showDialog)
                                dialog.dismiss();
                            callback.error(e.getLocalizedMessage());
                        }
                    });
                }

            }
        }).start();
    }

    public interface DefaultCallback {
        void error(String error);
    }

    public interface LoadClusterMapCallback extends DefaultCallback {

        void finish(final Master master, final Cluster cluster);
    }

    public interface CreateSaveClusterMapCallback extends DefaultCallback {

        void finish(final List<Master> masters);

    }

    public interface LoadMasterCallback extends DefaultCallback {

        void finish(final List<Master> masters);

    }

    public interface NoReturnCallback extends DefaultCallback {

        void finish();

    }

}
