package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Uploads by Moulinette
 */
public class TeamsUploads {

    private static final String API_ID = "id";
    private static final String API_FINAL_MARK = "final_mark";
    private static final String API_COMMENT = "comment";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_UPLOAD_ID = "upload_id";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_FINAL_MARK)
    public int finalMark;
    @SerializedName(API_COMMENT)
    public String comment;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_UPLOAD_ID)
    public int uploadId;

}
