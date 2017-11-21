package com.paulvarry.intra42.api.tools42;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.model.UsersLTE;

import java.util.List;

public class GroupLarge extends Group {

    @SerializedName("users")
    public List<UsersLTE> users;

}