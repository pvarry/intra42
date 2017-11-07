package com.paulvarry.intra42.api.tools42;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Friends extends FriendsSmall {

    @SerializedName("groups")
    public List<GroupSmall> groups;

}
