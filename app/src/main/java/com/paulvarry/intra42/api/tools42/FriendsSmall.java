package com.paulvarry.intra42.api.tools42;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.model.UsersLTE;

import java.util.List;

public class FriendsSmall extends UsersLTE {

    @SerializedName("friends_group_ids")
    public List<Integer> groups;

}
