package com.paulvarry.intra42.api.model;


import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.R;

import java.util.ArrayList;
import java.util.List;

public class Language {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_IDENTIFIER = "identifier";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_IDENTIFIER)
    public String identifier;

    public Language() {
    }

    public Language(int id, String name, String identifier) {
        this.id = id;
        this.name = name;
        this.identifier = identifier;
    }

    public static List<Language> getListOfLanguage(Context context) {

        List<Language> list = new ArrayList<>();

        list.add(new Language(1, context.getString(R.string.french), "fr"));
        list.add(new Language(2, context.getString(R.string.english), "en"));
        list.add(new Language(3, context.getString(R.string.romanian), "ro"));
        list.add(new Language(5, context.getString(R.string.ukrainian), "uk"));

        return list;

    }

    public String getFlag() {
        switch (id) {
            case 1: //fr
                return "\uD83C\uDDEB\uD83C\uDDF7";
            case 2: //en
                return "\uD83C\uDDEC\uD83C\uDDE7";
            case 3: //ro
                return "\uD83C\uDDF7\uD83C\uDDF4";
            case 4: //
                return null;
            case 5: //Ukrainian
                return "\uD83C\uDDFA\uD83C\uDDE6";
            default:
                return null;
        }
    }

    public boolean equals(Language language) {

        return this == language || id == language.id;
    }

}
