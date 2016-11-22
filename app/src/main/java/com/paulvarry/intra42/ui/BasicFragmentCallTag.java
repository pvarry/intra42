package com.paulvarry.intra42.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;

import com.google.gson.reflect.TypeToken;
import com.paulvarry.intra42.ApiService;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.Tags;
import com.paulvarry.intra42.oauth.ServiceGenerator;

import java.util.List;

public abstract class BasicFragmentCallTag<T, ADAPTER extends BaseAdapter> extends BasicFragmentCallSpinner<T, ADAPTER, Tags> {

    @Nullable
    @Override
    public List<Tags> getSpinnerElemList() {
        Activity a = getActivity();
        if (a != null)
            return ((AppClass) a.getApplication()).allTags;
        else {
            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            String strTags = sharedPref.getString(AppClass.CACHE_API_TAGS, "");

            if (!strTags.isEmpty())
                return ServiceGenerator.getGson().fromJson(strTags, new TypeToken<List<Tags>>() {
                }.getType());
        }
        return null;
    }

    @Override
    public int getSpinnerDefaultId(List<Tags> list) {
        return 8;
    }

    @Override
    public int getSpinnerElemId(Tags tags) {
        return tags.id;
    }

    @Override
    public String getSpinnerElemName(Tags tags) {
        return tags.name;
    }

    @Override
    public List<Tags> getSpinnerItems(ApiService apiService) {
        return AppClass.getCacheTags(null, apiService);
    }
}
