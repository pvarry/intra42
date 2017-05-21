package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Apps {


    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("website")
    @Expose
    public String website;
    @SerializedName("public")
    @Expose
    public Boolean _public;
    @SerializedName("scopes")
    @Expose
    public List<Object> scopes = null;
    @SerializedName("created_at")
    @Expose
    public String createdAt;
    @SerializedName("updated_at")
    @Expose
    public String updatedAt;
    @SerializedName("owner")
    @Expose
    public UsersLTE owner;
    @SerializedName("rate_limit")
    @Expose
    public Integer rateLimit;
    @SerializedName("roles")
    @Expose
    public List<Role> roles = null;

    public class Role {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("description")
        @Expose
        public String description;

    }
}