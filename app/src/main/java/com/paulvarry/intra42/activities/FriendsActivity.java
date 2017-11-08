package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.clusterMap.ClusterMapActivity;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.GridAdapterFriends;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.tools42.FriendsSmall;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsActivity extends BasicThreadActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener, BasicThreadActivity.GetDataOnThread {

    List<FriendsSmall> list;
    HashMap<String, Locations> locations;

    GridView gridView;
    ImageButton imageButtonSettings;
    GridAdapterFriends adapter;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, FriendsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friends);
        activeHamburger();

        super.onCreate(savedInstanceState);

        if (!app.userIsLogged())
            finish();

        registerGetDataOnOtherThread(this);

        navigationView.getMenu().getItem(5).getSubMenu().getItem(0).setChecked(true);
        gridView = findViewById(R.id.gridView);
        imageButtonSettings = findViewById(R.id.imageButtonSettings);
        imageButtonSettings.setOnClickListener(this);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {
        setLoadingProgress(getString(R.string.friends_loading_friends), 1, 2);

        ApiService42Tools api = app.getApiService42Tools();
        Call<List<FriendsSmall>> call = api.getFriends();
        try {
            Response<List<FriendsSmall>> ret = call.execute();
            if (Tools.apiIsSuccessful(ret))
                list = ret.body();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list == null)
            return;

        setLoadingProgress(getString(R.string.friends_loading_locations), 1, 2);

        StringBuilder searchOn = new StringBuilder();
        String separator = "";

        for (FriendsSmall u : list) {
            searchOn.append(separator).append(u.id);
            separator = ",";
        }

        Call<List<Locations>> c = app.getApiService().getLocationsUsers(AppSettings.getAppCampus(app), searchOn.toString(), 100, 1);
        try {
            Response<List<Locations>> response = c.execute();
            if (Tools.apiIsSuccessful(response)) {
                locations = new HashMap<>(response.body().size());
                for (Locations l : response.body())
                    locations.put(l.user.login, l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getToolbarName() {
        return null;
    }

    @Override
    public void setViewContent() {
        if (list == null)
            setViewState(StatusCode.EMPTY);
        else if (!list.isEmpty()) {

            adapter = new GridAdapterFriends(this, list, locations);
            gridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            gridView.setOnItemClickListener(this);
            gridView.setOnItemLongClickListener(this);
        } else
            setViewState(StatusCode.EMPTY);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.friends_nothing_found);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserActivity.openIt(this, adapter.getItem(position));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final UsersLTE user = adapter.getItem(position);
        int res;

        Callback<Void> removeFriend = new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (Tools.apiIsSuccessfulNoThrow(response))
                    Toast.makeText(app, getString(R.string.done), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(app, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(app, getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        };

        if (locations != null && locations.get(user.login) != null)
            res = R.array.alert_friends_open_long_click_location;
        else
            res = R.array.alert_friends_open_long_click;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_choose_action_colon) + user.login);
        builder.setItems(res, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    app.getApiService42Tools().deleteFriend(user.id);
                } else if (which == 1)
                    UserActivity.openIt(app, user);
                else if (which == 2)
                    ClusterMapActivity.openIt(FriendsActivity.this, locations.get(user.login).host);

            }
        });
        builder.show();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == imageButtonSettings) {
            Intent i = new Intent(this, FriendsGroupsActivity.class);
            startActivity(i);
        }
    }
}
