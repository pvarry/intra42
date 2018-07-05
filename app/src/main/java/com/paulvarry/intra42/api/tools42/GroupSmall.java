package com.paulvarry.intra42.api.tools42;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.IBaseItemSmall;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class GroupSmall implements IBaseItemSmall {

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

    @Override
    public String getName(Context context) {
        if (context != null && (name == null || name.isEmpty()))
            return "<" + context.getString(R.string.friends_groups_edit_no_name_set) + " (id:" + String.valueOf(id) + ")>";
        else if (context == null)
            return "(id:" + String.valueOf(id) + ")";
        return name;
    }

    @Override
    public String getSub(Context context) {
        return "";
    }

    @Override
    public boolean openIt(Context context) {
        return false;
    }

    @Override
    public int getId() {
        return id;
    }
}
