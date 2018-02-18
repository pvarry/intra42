package com.paulvarry.intra42.utils.cluster_map_contribute;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.ApiServiceClusterMapContribute;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Master;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {

    static public void loadClusterMap(Context context, ApiServiceClusterMapContribute api, Master master, final LoadClusterMapCallback callback) {

        final ProgressDialog dialog = ProgressDialog.show(context, null,
                "Loading. Please wait...", true);
        dialog.show();

        Call<Cluster> call = api.getCluster(master.key);
        call.enqueue(new Callback<Cluster>() {
            @Override
            public void onResponse(Call<Cluster> call, Response<Cluster> response) {
                dialog.cancel();
                Cluster body;
                if ((body = response.body()) != null) {
                    String setCookie = response.headers().get("set-cookie");
                    callback.finish(body, setCookie.substring(0, setCookie.indexOf(';')));
                } else
                    callback.error("failed to parse content");
            }

            @Override
            public void onFailure(Call<Cluster> call, Throwable t) {
                dialog.cancel();
                callback.error(t.getMessage());
            }
        });
    }

    static public void saveClusterMap(final Activity activity, final AppClass app, final Master master, final Cluster cluster, final SaveClusterMapCallback callback) {

        final ProgressDialog dialog = ProgressDialog.show(activity, null,
                "Loading. Please wait...", true);
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
                        returnError(activity, "error", dialog, callback);
                        return;
                    }
                    String setCookieMaster = responseMaster.headers().get("set-cookie");
                    cookieMaster = setCookieMaster.substring(0, setCookieMaster.indexOf(';'));
                    for (Master m : bodyMaster)
                        if (m.key.contentEquals(master.key) && m.url.contentEquals(master.url))
                            apiMaster = m;

                    if (apiMaster == null || (apiMaster.locked_by != null && !apiMaster.locked_by.contentEquals(app.me.login))) {
                        returnError(activity, "data locked", dialog, callback);
                        return;
                    }

                    Response<Cluster> responseCluster = api.getCluster(master.key).execute();
                    if (!responseCluster.isSuccessful() || responseCluster.body() == null) {
                        returnError(activity, "error", dialog, callback);
                        return;
                    }
                    String setCookieCluster = responseCluster.headers().get("set-cookie");
                    cookieCluster = setCookieCluster.substring(0, setCookieCluster.indexOf(';'));

                    //save

                    HashMap<String, String> mapCluster = new HashMap<>();
                    mapCluster.put("key", apiMaster.key);
                    mapCluster.put("pad", ServiceGenerator.getGson().toJson(cluster));
                    mapCluster.put("monospace", "1");

                    Response<Void> responseSaveMap = api.save(mapCluster, cookieCluster).execute();
                    if (!responseSaveMap.isSuccessful()) {
                        returnError(activity, "error", dialog, callback);
                        return;
                    }

                    apiMaster.locked_by = null;
                    apiMaster.locked_at = null;
                    apiMaster.name = cluster.name;

                    HashMap<String, String> map = new HashMap<>();
                    map.put("key", "q79vdcc1t");
                    map.put("pad", ServiceGenerator.getGson().toJson(bodyMaster));
                    map.put("monospace", "1");

                    Response<Void> responseSaveMaster = api.save(map, cookieMaster).execute();
                    if (!responseSaveMaster.isSuccessful()) {
                        returnError(activity, "error", dialog, callback);
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

    private static void returnError(@NonNull Activity activity, final String error, final Dialog dialog, final SaveClusterMapCallback callback) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                callback.error(error);
            }
        });
    }

    public interface LoadClusterMapCallback {

        void finish(Cluster cluster, String cookie);

        void error(String error);
    }

    public interface SaveClusterMapCallback {

        void finish();

        void error(String error);

    }

}
