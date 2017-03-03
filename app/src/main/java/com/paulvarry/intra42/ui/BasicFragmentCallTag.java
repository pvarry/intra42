package com.paulvarry.intra42.ui;

import android.support.annotation.Nullable;
import android.widget.BaseAdapter;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Tags;
import com.paulvarry.intra42.cache.CacheTags;

import java.util.List;

public abstract class BasicFragmentCallTag<T, ADAPTER extends BaseAdapter> extends BasicFragmentCallSpinner<T, ADAPTER, Tags> {

    @Nullable
    @Override
    public List<Tags> getSpinnerElemList() {
        AppClass app = (AppClass) getActivity().getApplication();
        return CacheTags.get(app.cacheSQLiteHelper);
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
        AppClass app = (AppClass) getActivity().getApplication();
        return CacheTags.getAllowInternet(app.cacheSQLiteHelper, app);
    }
}
