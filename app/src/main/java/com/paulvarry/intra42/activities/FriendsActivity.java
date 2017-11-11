package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.ItemDecoration;
import com.paulvarry.intra42.adapters.RecyclerViewAdapterFriends;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.tools42.Friends;
import com.paulvarry.intra42.api.tools42.FriendsSmall;
import com.paulvarry.intra42.api.tools42.Group;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class FriendsActivity
        extends BasicThreadActivity
        implements View.OnClickListener, BasicThreadActivity.GetDataOnThread, RecyclerViewAdapterFriends.OnItemClickListener, RecyclerViewAdapterFriends.SelectionListener, AdapterView.OnItemSelectedListener {

    List<FriendsSmall> list;
    SparseArray<FriendsSmall> listSoled;
    List<Group> groups;
    HashMap<String, Locations> locations;
    List<Integer> selection;

    RecyclerView recyclerView;
    ImageButton imageButtonSettings;

    ViewGroup linearLayoutHeader;
    ViewGroup linearLayoutHeaderSelection;

    Spinner spinner;

    RecyclerViewAdapterFriends adapter;

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

        spinner = findViewById(R.id.spinner);
        linearLayoutHeader = findViewById(R.id.linearLayoutHeader);
        linearLayoutHeaderSelection = findViewById(R.id.linearLayoutHeaderSelection);
        recyclerView = findViewById(R.id.recyclerView);
        imageButtonSettings = findViewById(R.id.imageButtonSettings);

        imageButtonSettings.setOnClickListener(this);
        linearLayoutHeader.setVisibility(View.VISIBLE);
        linearLayoutHeaderSelection.setVisibility(View.GONE);
        spinner.setOnItemSelectedListener(this);
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
            if (Tools.apiIsSuccessful(ret)) {
                list = ret.body();

                listSoled = new SparseArray<>();
                for (FriendsSmall f : list) {
                    listSoled.put(f.id, f);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Call<List<Group>> callGroup = api.getFriendsGroups();
        try {
            Response<List<Group>> ret = callGroup.execute();
            if (Tools.apiIsSuccessful(ret))
                groups = ret.body();

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

            if (selection != null) {
                linearLayoutHeaderSelection.setVisibility(View.VISIBLE);
                linearLayoutHeader.setVisibility(View.GONE);
            } else {
                linearLayoutHeaderSelection.setVisibility(View.GONE);
                linearLayoutHeader.setVisibility(View.VISIBLE);
            }

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            int noOfColumns = (int) (dpWidth / 90);

            recyclerView.setLayoutManager(new GridLayoutManager(this, noOfColumns));

            recyclerView.addItemDecoration(new ItemDecoration(getResources().getDimensionPixelSize(R.dimen.list_spacing), noOfColumns));

            adapter = new RecyclerViewAdapterFriends(this, list, locations);
            adapter.setClickListener(this);
            adapter.setSelectionListener(this);
            recyclerView.setAdapter(adapter);

            List<String> list = new ArrayList<>();
            list.add(getString(R.string.friends_groups_all));
            if (groups != null)
                for (Group g : groups) {
                    list.add(g.name);
                }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);

        } else
            setViewState(StatusCode.EMPTY);
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.friends_nothing_found);
    }

    @Override
    public void onClick(View v) {
        if (v == imageButtonSettings) {
            Intent i = new Intent(this, FriendsGroupsActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onItemClick(int position, FriendsSmall clicked) {
        UserActivity.openIt(this, clicked);
    }

    @Override
    public void onSelectionChanged(List<Integer> selected) {

        if (selected != null) {
            linearLayoutHeaderSelection.setVisibility(View.VISIBLE);
            linearLayoutHeader.setVisibility(View.GONE);
        } else {
            linearLayoutHeaderSelection.setVisibility(View.GONE);
            linearLayoutHeader.setVisibility(View.VISIBLE);
        }

        selection = selected;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        List<FriendsSmall> list = this.list;
        Group group;

        if (position > 0 && groups != null && groups.size() > position) {
            list = new ArrayList<>();
            group = groups.get(position - 1);
            for (Integer i : group.users) {
                list.add(listSoled.get(i));
            }
        }

        adapter = new RecyclerViewAdapterFriends(this, list, locations);
        adapter.setClickListener(this);
        adapter.setSelectionListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setToGroup(View view) {
        String[] group = getGroupStringList();
        if (group == null)
            Toast.makeText(app, R.string.friends_groups_nothing, Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(group, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Group clickedGroup = groups.get(which);

                List<FriendsSmall> list = getSelectedList();
                clickedGroup.addToThisGroup(FriendsActivity.this, list, new Runnable() {
                    @Override
                    public void run() {
                        selection = null;
                        Toast.makeText(app, R.string.done, Toast.LENGTH_SHORT).show();
                        refresh();
                    }
                });
            }
        });
        builder.setTitle(R.string.friends_select_group);
        builder.show();
    }

    public void removeFromGroup(View view) {
        String[] group = getGroupStringList();
        if (group == null)
            Toast.makeText(app, R.string.friends_groups_nothing, Toast.LENGTH_SHORT).show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setItems(group, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Group clickedGroup = groups.get(which);

                List<FriendsSmall> list = getSelectedList();
                clickedGroup.removeFromGroup(FriendsActivity.this, list, new Runnable() {
                    @Override
                    public void run() {
                        selection = null;
                        Toast.makeText(app, R.string.done, Toast.LENGTH_SHORT).show();
                        refresh();
                    }
                });
            }
        });
        builder.setTitle(R.string.friends_select_group);
        builder.show();
    }

    public void removeFromFriends(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                List<FriendsSmall> list = getSelectedList();
                Friends.deleteFriendsList(FriendsActivity.this, list, new Runnable() {
                    @Override
                    public void run() {
                        selection = null;
                        Toast.makeText(app, R.string.done, Toast.LENGTH_SHORT).show();
                        refresh();
                    }
                });
            }
        });
        builder.setTitle(R.string.friends_delete_friends_title);
        builder.show();
    }

    List<FriendsSmall> getSelectedList() {
        List<FriendsSmall> toAdd = new ArrayList<>();
        for (Integer i : selection) {
            toAdd.add(list.get(i));
        }
        return toAdd;
    }

    String[] getGroupStringList() {

        if (groups == null || groups.size() == 0)
            return null;

        String[] group = new String[groups.size()];
        for (int i = 0; groups.size() > i; i++)
            group[i] = groups.get(i).name;
        return group;
    }
}
