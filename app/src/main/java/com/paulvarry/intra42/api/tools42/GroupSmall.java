package com.paulvarry.intra42.api.tools42;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GroupSmall {

    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;

    public void addToThisGroup(final BasicActivity activity, final List<FriendsSmall> list, final Runnable callback) {
        final ApiService42Tools api = activity.app.getApiService42Tools();

        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean success = true;
                try {
                    for (FriendsSmall friend : list) {
                        Call<Friends> call = api.addFriendToGroup(id, friend.id);

                        Response<Friends> ret = call.execute();
                        if (!Tools.apiIsSuccessfulNoThrow(ret))
                            success = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (callback != null)
                    activity.runOnUiThread(callback);
            }
        }).start();

    }

    public void removeFromGroup(final BasicActivity activity, final List<FriendsSmall> list, final Runnable callback) {
        final ApiService42Tools api = activity.app.getApiService42Tools();

        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean success = true;
                try {
                    for (FriendsSmall friend : list) {
                        Call<Void> call = api.deleteFriendFromGroup(id, friend.id);

                        Response<Void> ret = call.execute();
                        if (!Tools.apiIsSuccessfulNoThrow(ret))
                            success = false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (callback != null)
                    activity.runOnUiThread(callback);
            }
        }).start();
    }
}
