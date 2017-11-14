package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.ItemDecoration;
import com.paulvarry.intra42.adapters.RecyclerViewAdapterFriends;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ApiService42Tools;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.UsersLTE;
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
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class FriendsActivity
        extends BasicThreadActivity
        implements View.OnClickListener, BasicThreadActivity.GetDataOnThread, RecyclerViewAdapterFriends.OnItemClickListener, RecyclerViewAdapterFriends.SelectionListener, AdapterView.OnItemSelectedListener {

    List<FriendsSmall> list;
    SparseArray<FriendsSmall> listFriends;
    List<Group> groups;
    HashMap<String, Locations> locations;
    List<Integer> selection;

    RecyclerView recyclerView;
    ImageButton imageButtonSettings;

    ViewGroup linearLayoutHeader;
    ViewGroup linearLayoutHeaderSelection;

    Spinner spinner;

    RecyclerViewAdapterFriends adapter;

    boolean needUpdateFriends = false;

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

    @Override
    protected void refresh() {

        setViewState(StatusCode.LOADING);

        SharedPreferences pref = AppSettings.getSharedPreferences(this);
        if (pref.getBoolean("should_sync_friends", false) && app.firebaseRefFriends != null) {
            needUpdateFriends = true;

            getFriendsFromFirebase();

        } else {
            needUpdateFriends = false;
            super.refresh();
        }
    }

    public void getFriendsFromFirebase() {
        ValueEventListener friendsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {
                };
                final HashMap<String, String> messages = snapshot.getValue(t);

                if (messages == null) {
                    friendsDatabaseFinish(true);
                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Call<Friends> call;
                            boolean success = true;
                            boolean apiWorking = false;
                            try {

                                final ApiService apiIntra = app.getApiService();

                                Response<List<Locations>> retIntra = apiIntra.getLocations(1, 1, 1).execute();
                                if (!Tools.apiIsSuccessfulNoThrow(retIntra))
                                    return;

                                final ApiService42Tools api = app.getApiService42Tools();

                                Set<String> s = messages.keySet();
                                UsersLTE tmp = new UsersLTE();
                                int i = 1;
                                for (String k : s) {
                                    tmp.id = Integer.decode(k);
                                    tmp.login = messages.get(k);
                                    call = api.addFriend(tmp.id);

                                    String state = getString(R.string.friends) + " " + String.valueOf(i) + "/" + s.size();
                                    setLoadingProgress(state, i, s.size());

                                    Response<Friends> ret = call.execute();
                                    if (Tools.apiIsSuccessfulNoThrow(ret))
                                        app.firebaseRefFriends.child(String.valueOf(tmp.id)).removeValue();
                                    else
                                        success = false;
                                    if (ret != null && ret.code() == 102) {
                                        if (!apiWorking)
                                            Toast.makeText(app, "Friends API is currently getting new users, please try again in 2min", Toast.LENGTH_SHORT).show();
                                        apiWorking = true;
                                    }
                                    i++;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                success = false;
                            }
                            friendsDatabaseFinish(success);
                        }
                    }).start();

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("Firebase", "Failed to read value.", error.toException());
                friendsDatabaseFinish(false);
            }
        };

        setViewState(StatusCode.LOADING);
        setLoadingInfo("Friends database update");
        setLoadingProgress("calculating", 0, -1);

        app.firebaseRefFriends.addListenerForSingleValueEvent(friendsEventListener);
    }

    private void friendsDatabaseFinish(boolean success) {
        if (success) {
            SharedPreferences.Editor pref = AppSettings.getSharedPreferences(FriendsActivity.this).edit();
            pref.remove("should_sync_friends");
            pref.apply();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FriendsActivity.super.refresh();
            }
        });
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {
        setLoadingProgress(getString(R.string.info_loading_friends), 0, 2);

        ApiService42Tools api = app.getApiService42Tools();
        Call<List<FriendsSmall>> call = api.getFriends();
        try {
            Response<List<FriendsSmall>> ret = call.execute();
            if (Tools.apiIsSuccessful(ret)) {
                list = ret.body();

                listFriends = new SparseArray<>();
                for (FriendsSmall f : list) {
                    listFriends.put(f.id, f);
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

            if (recyclerView.getItemDecorationAt(0) == null)
                recyclerView.addItemDecoration(new ItemDecoration(getResources().getDimensionPixelSize(R.dimen.list_spacing), noOfColumns), 0);

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

        if (position > 0 && groups != null && position - 1 < groups.size() && position - 1 >= 0) {
            list = new ArrayList<>();
            group = groups.get(position - 1);
            for (Integer i : group.users) {
                list.add(listFriends.get(i));
            }
        }

        adapter = new RecyclerViewAdapterFriends(this, list, locations);
        adapter.setClickListener(this);
        adapter.setSelectionListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
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
