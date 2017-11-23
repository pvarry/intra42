package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class CoalitionsBlocs {

    private static final String API_ID = "id";
    private static final String API_CAMPUS_ID = "campus_id";
    private static final String API_CURSUS_ID = "cursus_id";
    private static final String API_SQUAD_SIZE = "squad_size";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_UPDATED_AT = "updated_at";
    private static final String API_COALITIONS = "coalitions";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_CAMPUS_ID)
    public int cursusId;
    @SerializedName(API_CURSUS_ID)
    public int campusId;
    @SerializedName(API_SQUAD_SIZE)
    public int squadSize;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_UPDATED_AT)
    public Date updatedAt;
    @SerializedName(API_COALITIONS)
    public List<Coalitions> coalitions;

}
