package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class QuestsUsers implements Serializable {

    private final static String API_ID = "id";
    private final static String API_END_AT = "end_at";
    private final static String API_QUEST_ID = "quest_id";
    private final static String API_VALIDATED_AT = "validated_at";
    private final static String API_PRCT = "prct";
    private final static String API_ADVANCEMENT = "advancement";
    private final static String API_CREATED_AT = "created_at";
    private final static String API_UPDATED_AT = "updated_at";
    private final static String API_USER = "user";
    private final static String API_QUEST = "quest";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_END_AT)
    public Date end_at;
    @SerializedName(API_QUEST_ID)
    public int questId;
    @SerializedName(API_VALIDATED_AT)
    public Date validatedAt;
    @SerializedName(API_PRCT)
    public int prct;
    @SerializedName(API_ADVANCEMENT)
    public String advancement;
    @SerializedName(API_CREATED_AT)
    public Date created_at;
    @SerializedName(API_UPDATED_AT)
    public Date updated_at;
    @SerializedName(API_USER)
    public UsersLTE user;
    @SerializedName(API_QUEST)
    public Quests quest;

}
