package com.paulvarry.intra42.api;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.oauth.ServiceGenerator;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

public class User extends UserLTE {

    private final static String API_ID = "id";
    private final static String API_EMAIL = "email";
    private final static String API_LOGIN = "login";
    private final static String API_URL = "url";
    private final static String API_PHONE = "phone";
    private final static String API_NAME = "displayname";
    private final static String API_IMAGE_URL = "image_url";
    private final static String API_STAFF = "staff?";
    private final static String API_CORRECTION_POINT = "correction_point";
    private final static String API_POOL_MOUNT = "pool_month";
    private final static String API_POOL_YEAR = "pool_year";
    private final static String API_LOCATION = "location";
    private final static String API_WALLET = "wallet";
    private final static String API_GROUPS = "groups";
    private final static String API_CURSUS_USERS = "cursus_users";
    private final static String API_PROJECTS_USERS = "projects_users";
    private final static String API_ACHIEVEMENTS = "achievements";
    private final static String API_TITLES = "titles";
    private final static String API_PARTNERSHIPS = "partnerships";
    private final static String API_PATRONED = "patroned";
    private final static String API_PATRONING = "patroning";
    private final static String API_EXPERTISES_USERS = "expertises_users";
    private final static String API_CAMPUS = "campus";

    @SerializedName(API_EMAIL)
    public String email;
    @Nullable
    @SerializedName(API_PHONE)
    public String phone;
    @SerializedName(API_NAME)
    public String displayName;
    @SerializedName(API_IMAGE_URL)
    public String image_url;
    @SerializedName(API_STAFF)
    public boolean staff;
    @SerializedName(API_CORRECTION_POINT)
    public int correction_point;
    @Nullable
    @SerializedName(API_POOL_MOUNT)
    public String pool_month;
    @Nullable
    @SerializedName(API_POOL_YEAR)
    public String pool_year;
    @Nullable
    @SerializedName(API_LOCATION)
    public String location;
    @SerializedName(API_WALLET)
    public int wallet;
    @SerializedName(API_GROUPS)
    public List<Tags> groups;
    @Nullable
    @SerializedName(API_CURSUS_USERS)
    public List<CursusUsers> cursusUsers;
    @SerializedName(API_PROJECTS_USERS)
    public List<ProjectsUsers> projectsUsers;
    @SerializedName(API_ACHIEVEMENTS)
    public List<Achievements> achievements;
    @SerializedName(API_TITLES)
    public List<UserTitle> titles;
    @SerializedName(API_PARTNERSHIPS)
    public List<Partnerships> partnerships;

    public transient List<Object> patroned;
    public transient List<Object> patroning;

    @SerializedName(API_EXPERTISES_USERS)
    public List<ExpertisesUsers> expertisesUsers;
    @SerializedName(API_CAMPUS)
    public List<Campus> campus;

    static public User me(ApiService apiService) {

        Call<User> call = apiService.getUserMe();
        try {
            retrofit2.Response<User> ret = call.execute();
            if (ret.code() == 200)
                return ret.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public User fromString(String str) {
        return ServiceGenerator.getGson().fromJson(str, User.class);
    }

    @Override
    public String toString() {
        return ServiceGenerator.getGson().toJson(this);
    }

    static public class UserTitle {

        static final String API_ID = "id";
        static final String API_NAME = "name";
        static final String API_FORMATTER = "formatter";

        @SerializedName(API_ID)
        public int id;
        @SerializedName(API_NAME)
        public String name;
        @SerializedName(API_FORMATTER)
        public String formatter;
    }

}
