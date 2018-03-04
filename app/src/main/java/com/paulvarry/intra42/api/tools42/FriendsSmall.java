package com.paulvarry.intra42.api.tools42;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
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

    public static List<FriendsSmall> getFriends(ApiService42Tools api) throws IOException {
        final List<FriendsSmall> friendsTmp = new ArrayList<>();

        int page = 1;
        int pageSize = 100;

        while (true) {
            Response<List<FriendsSmall>> r = api.getFriends(pageSize, page).execute();
            if (Tools.apiIsSuccessful(r)) {
                friendsTmp.addAll(r.body());
                double total = Double.parseDouble(r.headers().get("X-Total"));
                if (r.body().size() == pageSize) {
                    page++;
                } else
                    break;
                if (friendsTmp.size() == total)
                    return friendsTmp;
            }
        }

        return friendsTmp;
    }

}
