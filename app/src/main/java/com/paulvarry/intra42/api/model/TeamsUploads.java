package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Uploads by Moulinette
 */
public class TeamsUploads {

    private static final String API_ID = "id";
    private static final String API_FINAL_MARK = "final_mark";
    private static final String API_COMMENT = "comment";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_UPLOAD_ID = "upload_id";
    private static final String API_UPLOAD = "upload";

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
    @SerializedName(API_UPLOAD)
    public List<Upload> upload;

    class Upload {
        private static final String API_ID = "id";
        private static final String API_EVALUATION_ID = "evaluation_id";
        private static final String API_NAME = "name";
        private static final String API_DESCRIPTION = "description";
        private static final String API_CREATED_AT = "created_at";
        private static final String API_UPDATED_AT = "updated_at";

        @SerializedName(API_ID)
        public int id;
        @SerializedName(API_EVALUATION_ID)
        public int evaluationId;
        @SerializedName(API_NAME)
        public String name;
        @SerializedName(API_DESCRIPTION)
        public String description;
        @SerializedName(API_CREATED_AT)
        public Date createdAt;
        @SerializedName(API_UPDATED_AT)
        public Date updatedAt;
    }

}
