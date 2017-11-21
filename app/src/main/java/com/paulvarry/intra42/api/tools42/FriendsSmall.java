package com.paulvarry.intra42.api.tools42;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FriendsSmall extends UsersLTE {

    @SerializedName("friends_group_ids")
    public List<Integer> groups;

    public static void deleteFriendsList(final BasicActivity activity, final List<FriendsSmall> list, final Runnable callback) {
        final ApiService42Tools api = activity.app.getApiService42Tools();

        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean success = true;
                try {
                    for (FriendsSmall friend : list) {
                        Call<Void> call = api.deleteFriend(friend.id);

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
