package com.paulvarry.intra42.api.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class Users extends UsersLTE {

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
    private final static String API_LANGUAGES_USERS = "languages_users";
    private final static String API_ACHIEVEMENTS = "achievements";
    private final static String API_TITLES = "titles";
    private final static String API_TITLES_USERS = "titles_users";
    private final static String API_PARTNERSHIPS = "partnerships";
    private final static String API_PATRONED = "patroned";
    private final static String API_PATRONING = "patroning";
    private final static String API_EXPERTISES_USERS = "expertises_users";
    private final static String API_CAMPUS = "campus";
    private final static String API_CAMPUS_USER = "campus_users";

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
    @SerializedName(API_LANGUAGES_USERS)
    public List<LanguagesUsers> languagesUsers;
    @SerializedName(API_ACHIEVEMENTS)
    public List<Achievements> achievements;
    @SerializedName(API_TITLES)
    public List<Title> titles;
    @SerializedName(API_TITLES_USERS)
    public List<TitleUser> titlesUsers;
    @SerializedName(API_PARTNERSHIPS)
    public List<Partnerships> partnerships;

    public transient List<Object> patroned;
    public transient List<Object> patroning;

    @SerializedName(API_EXPERTISES_USERS)
    public List<ExpertiseUsers> expertisesUsers;
    @SerializedName(API_CAMPUS)
    public List<Campus> campus;
    @SerializedName(API_CAMPUS_USER)
    public List<CampusUsers> campusUsers;

    @Nullable
    @SerializedName("custom_coalitions")
    public List<Coalitions> coalitions;

    static public Users me(ApiService apiService) {

        Call<Users> call = apiService.getUserMe();
        try {
            retrofit2.Response<Users> ret = call.execute();
            if (ret.code() == 200) {
                Users u = ret.body();

                Response<List<Coalitions>> retCoalition = apiService.getUsersCoalitions(u.login).execute();
                if (Tools.apiIsSuccessfulNoThrow(retCoalition))
                    u.coalitions = retCoalition.body();
                return u;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public Users fromString(String str) {
        return ServiceGenerator.getGson().fromJson(str, Users.class);
    }

    @Override
    public String toString() {
        return ServiceGenerator.getGson().toJson(this);
    }

    /**
     * get a CampusUsers from this user to display on the app. If a forced Campus is found and if
     * the user is register to this campus, so this campus is returned. In the other case, the main
     * campus is returned.
     * <p>
     * NOTE: Is the user have set force campus on the settings, this campus is not returned in any
     * case, it is returned only if the user is subscribed to this campus. To have the forced Campus
     * even if the user is not subscribe use : {@link AppSettings#getAppCampus(AppClass)}.
     *
     * @param context The Context
     * @return The CampusUsers to use on the app.
     */
    @Nullable
    public CampusUsers getCampusUsersToDisplay(Context context) {
        int appForce = AppSettings.Advanced.getContentForceCampus(context);
        CampusUsers mainCampus = null;

        if (this.campusUsers == null || this.campusUsers.size() == 0)
            return null;

        if (appForce > 0) { // use when a cursus if force on the setting
            for (CampusUsers c : this.campusUsers) {
                if (c.campusId == appForce) {
                    mainCampus = c;
                    break;
                }
            }
            if (mainCampus != null) // only return a cursus if it is found
                return mainCampus;
        }

        for (CampusUsers campusUsers : this.campusUsers) {
            if (campusUsers.isPrimary) {
                mainCampus = campusUsers;
                break;
            }
        }
        return mainCampus;
    }

    public int getCampusUsersToDisplayID(Context context) {
        CampusUsers tmp = getCampusUsersToDisplay(context);
        if (tmp == null)
            return 0;
        else
            return tmp.campusId;
    }

    /**
     * get a CursusUsers from this user to display on the app. If a forced Cursus is found and if
     * the user is register to this cursus, so this cursus is returned. In the other case, the main
     * cursus is found (with end_at data) and returned.
     * <p>
     * NOTE: Is the user have set force cursus on the settings, this cursus is not returned in any
     * case, it is returned only if the user is subscribed to this cursus. To have the forced Cursus
     * even if the user is not subscribe use : {@link AppSettings#getAppCursus(AppClass)}.
     *
     * @param context The Context
     * @return The CursusUsers to use on the app.
     */
    @Nullable
    public CursusUsers getCursusUsersToDisplay(Context context) {
        int appForce = AppSettings.Advanced.getContentForceCursus(context);
        CursusUsers mainCursus = null;
        CursusUsers cursus42IfIsActive = null;
        CursusUsers cursusActiveMaxLevel = null;

        if (cursusUsers == null || campusUsers.size() == 0)
            return null;

        if (appForce > 0) { // use when a cursus if force on the setting
            for (CursusUsers c : cursusUsers) {
                if (c.cursusId == appForce) {
                    mainCursus = c;
                    break;
                }
            }
            if (mainCursus != null) // only return a cursus if it is found
                return mainCursus;
        }

        for (CursusUsers cursusUsersFor : cursusUsers) { // check for active campus and add then on a list
            if (cursusUsersFor.end_at == null || cursusUsersFor.end_at.after(new Date())) {
                if (cursusUsersFor.cursusId == 1)
                    cursus42IfIsActive = cursusUsersFor;
                if (cursusActiveMaxLevel == null || cursusUsersFor.level > cursusActiveMaxLevel.level)
                    cursusActiveMaxLevel = cursusUsersFor;
            }
        }

        if (cursus42IfIsActive != null)
            return cursus42IfIsActive;
        if (cursusActiveMaxLevel != null)
            return cursusActiveMaxLevel;
        else if (cursusUsers.size() != 0)
            return cursusUsers.get(0);
        else
            return null;
    }

    public int getCursusUsersToDisplayID(Context context) {
        CursusUsers tmp = getCursusUsersToDisplay(context);
        if (tmp == null)
            return 0;
        else
            return tmp.cursusId;
    }

    static public class Title {

        static final String API_ID = "id";
        static final String API_NAME = "name";

        @SerializedName(API_ID)
        public int id;
        @SerializedName(API_NAME)
        public String name;
    }

    static public class TitleUser {

        static final String API_ID = "id";
        static final String API_USER_ID = "user_id";
        static final String API_TITLE_ID = "title_id";
        static final String API_SELECTED = "selected";

        @SerializedName(API_ID)
        public int id;
        @SerializedName(API_USER_ID)
        public int userId;
        @SerializedName(API_TITLE_ID)
        public int titleId;
        @SerializedName(API_SELECTED)
        public boolean selected;
    }

}
