package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;

public class CoalitionsDataIntra {

    private static final String API_NAME = "name";
    private static final String API_COLOR = "color";
    private static final String API_DATA = "data";

    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_COLOR)
    public String color;
    @SerializedName(API_DATA)
    public long[][] data;

}