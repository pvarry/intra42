package com.paulvarry.intra42.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.BaseListAdapterSub;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.tools42.Group;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FriendsGroupsActivity extends BasicThreadActivity implements BasicThreadActivity.GetDataOnThread, AdapterView.OnItemClickListener, BasicThreadActivity.GetDataOnMain, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private List<Group> groups;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_friends_groups);

        registerGetDataOnOtherThread(this);
        registerGetDataOnMainTread(this);

        listView = findViewById(R.id.listView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        listView.setOnItemClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        fabBaseActivity.setVisibility(View.VISIBLE);
        fabBaseActivity.setOnClickListener(this);

        super.onCreateFinished();
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public String getToolbarName() {
        return getString(R.string.title_activity_friends_groups);
    }

    @Override
    protected void setViewContent() {
        BaseListAdapterSub<Group> adapter = new BaseListAdapterSub<>(this, groups);
        listView.setAdapter(adapter);
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {

        ApiService42Tools api = app.getApiService42Tools();

        Call<List<Group>> call = api.getFriendsGroups();
        Response<List<Group>> ret = call.execute();
        if (Tools.apiIsSuccessful(ret))
            groups = ret.body();
        else
            groups = null;
    }

    @Override
    public final Object onRetainCustomNonConfigurationInstance() {
        return groups;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (groups.size() > position)
            FriendsGroupsEditActivity.open(this, groups.get(position));
    }

    @Override
    public ThreadStatusCode getDataOnMainThread() {
        Object o = getLastCustomNonConfigurationInstance();

        if (o instanceof List)
            groups = (List<Group>) o;
        if (groups != null)
            return ThreadStatusCode.FINISH;
        return ThreadStatusCode.CONTINUE;
    }

    @Override
    public void onClick(View v) {
        if (v == fabBaseActivity)
            FriendsGroupsEditActivity.open(this);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        refresh();
    }
}
