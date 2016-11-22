package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;
import com.plumillonforge.android.chipview.Chip;

import java.io.Serializable;

public class Tags implements Chip, Serializable {

    static final String API_ID = "id";
    static final String API_NAME = "name";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;

    @Override
    public String toString() {
        return name;
    }

    public boolean equals(Tags tag) {
        return tag != null && tag.id == id;
    }

    @Override
    public String getText() {
        return name;
    }
}
