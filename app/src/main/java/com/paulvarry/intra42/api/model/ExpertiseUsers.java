package com.paulvarry.intra42.api.model;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.Pagination;
import com.paulvarry.intra42.utils.Tools;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpertiseUsers {

    private static final String API_ID = "id";
    private static final String API_EXPERTISE_ID = "expertise_id";
    private static final String API_INTERESTED = "interested";
    private static final String API_VALUE = "value";
    private static final String API_CONTACT_ME = "contact_me";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_USER_ID = "user_id";
    private static final String API_EXPERTISE = "expertise";
    private static final String API_USER = "user";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_EXPERTISE_ID)
    public int expertiseId;
    @SerializedName(API_INTERESTED)
    public boolean interested;
    @SerializedName(API_VALUE)
    public int value;
    @SerializedName(API_CONTACT_ME)
    public boolean contactMe;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_USER_ID)
    public int userId;
    @SerializedName(API_EXPERTISE)
    public Expertises expertise;
    @SerializedName(API_USER)
    public UsersLTE user;

    @Nullable
    public static List<ExpertiseUsers> getExpertiseUsers(ApiService api, Users user) throws IOException, BasicThreadActivity.UnauthorizedException, BasicThreadActivity.ErrorServerException {
        List<ExpertiseUsers> list = new ArrayList<>();
        int i = 0;
        int pageSize = 100;

        while (i < 10 && Pagination.canAdd(list, pageSize)) {
            Response<List<ExpertiseUsers>> response = api.getUserExpertises(user.login, Pagination.getPage(list, pageSize)).execute();
            if (!Tools.apiIsSuccessful(response))
                break;
            List<ExpertiseUsers> tmp = response.body();
            list.addAll(tmp);
            ++i;
        }

        if (list.isEmpty())
            return null;
        return list;
    }

}
