package com.paulvarry.intra42.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.paulvarry.intra42.AppClass;
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
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

public class FriendsActivity
        extends BasicThreadActivity
        implements View.OnClickListener, BasicThreadActivity.GetDataOnThread, RecyclerViewAdapterFriends.OnItemClickListener, RecyclerViewAdapterFriends.SelectionListener, AdapterView.OnItemSelectedListener, BasicThreadActivity.GetDataOnMain, SwipeRefreshLayout.OnRefreshListener {

    private final String SAVED_STATE_SPINNER_GROUP_SELECTED = "spinnerGroupSelected";
    List<FriendsSmall> list;
    SparseArray<FriendsSmall> listFriends;
    List<Group> groups;
    HashMap<String, Locations> locations;
    List<Integer> selection;
    DataWrapper dataWrapper;
    RecyclerView recyclerView;
    ImageButton imageButtonSettings;
    ViewGroup linearLayoutHeader;
    ViewGroup linearLayoutHeaderSelection;
    SwipeRefreshLayout swipeRefreshLayout;
    Spinner spinner;
    RecyclerViewAdapterFriends adapter;
    boolean needUpdateFriends = false;
    int spinnerGroupSelected;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, FriendsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_friends);
        super.setSelectedMenu(5, 0);
        super.setActionBarToggle(ActionBarToggle.HAMBURGER);

        registerGetDataOnMainTread(this);
        registerGetDataOnOtherThread(this);

        spinner = findViewById(R.id.spinner);
        linearLayoutHeader = findViewById(R.id.linearLayoutHeader);
        linearLayoutHeaderSelection = findViewById(R.id.linearLayoutHeaderSelection);
        recyclerView = findViewById(R.id.recyclerView);
        imageButtonSettings = findViewById(R.id.imageButtonSettings);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        imageButtonSettings.setOnClickListener(this);
        linearLayoutHeader.setVisibility(View.VISIBLE);
        linearLayoutHeaderSelection.setVisibility(View.GONE);
        spinner.setOnItemSelectedListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        dataWrapper = (DataWrapper) getLastCustomNonConfigurationInstance();
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_STATE_SPINNER_GROUP_SELECTED))
            spinnerGroupSelected = savedInstanceState.getInt(SAVED_STATE_SPINNER_GROUP_SELECTED);

        setViewState(StatusCode.LOADING);

        SharedPreferences pref = AppSettings.getSharedPreferences(this);
        if (pref.getBoolean("should_sync_friends", false) && app.firebaseRefFriends != null) {
            needUpdateFriends = true;

            getFriendsFromFirebase();

        } else {
            needUpdateFriends = false;
            onCreateFinished();
        }

        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.fetch(AppClass.FIREBASE_REMOTE_CONFIG_CACHE_EXPIRATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        }

                        boolean warning_42tools_enable = mFirebaseRemoteConfig.getBoolean(getString(R.string.firebase_remote_config_warning_42tools_enable));
                        if (warning_42tools_enable) {

                            String message = mFirebaseRemoteConfig.getString(getString(R.string.firebase_remote_config_warning_42tools_message));
                            if (message == null || message.isEmpty())
                                message = getString(R.string.friends_warning);
                            Toast.makeText(app, message, Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private void getFriendsFromFirebase() {
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
                                    if (Tools.apiIsSuccessfulNoThrow(ret) && app.firebaseRefFriends != null)
                                        app.firebaseRefFriends.child(String.valueOf(tmp.id)).removeValue();
                                    else
                                        success = false;
                                    if (ret != null && ret.code() == 102) {
                                        if (!apiWorking)
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(app, R.string.friends_info_api_try_again, Toast.LENGTH_SHORT).show();
                                                }
                                            });
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
        setLoadingProgress(R.string.friends_info_database_update, 0, -1);

        if (app.firebaseRefFriends != null) {
            app.firebaseRefFriends.addListenerForSingleValueEvent(friendsEventListener);
        }
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
                onCreateFinished();
            }
        });
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public ThreadStatusCode getDataOnMainThread() {
        if (dataWrapper != null) {
            listFriends = dataWrapper.listFriends;
            groups = dataWrapper.groups;
            locations = dataWrapper.locations;
            dataWrapper = null;

            if (listFriends == null)
                return ThreadStatusCode.CONTINUE;

            list = new ArrayList<>(listFriends.size());

            for (int i = 0; i < listFriends.size(); i++)
                list.add(listFriends.valueAt(i));

            TreeSet<FriendsSmall> haveLocation = new TreeSet<>();
            TreeSet<FriendsSmall> noLocation = new TreeSet<>();
            for (FriendsSmall f : list) {
                if (locations != null && locations.containsKey(f.login))
                    haveLocation.add(f);
                else
                    noLocation.add(f);
            }

            list = new ArrayList<>();
            list.addAll(haveLocation);
            list.addAll(noLocation);

            return ThreadStatusCode.FINISH;
        }
        dataWrapper = null;

        return ThreadStatusCode.CONTINUE;
    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {
        setLoadingProgress(getString(R.string.info_loading_friends), 0, 2);
        ApiService42Tools api = app.getApiService42Tools();

        StringBuilder searchOnLocation = new StringBuilder();
        String separator = "";
        HashMap<String, FriendsSmall> tmp = new HashMap<>();

        List<FriendsSmall> data = Friends.getFriends(api);
        if (data != null) {
            listFriends = new SparseArray<>(data.size());
            for (FriendsSmall f : data) {
                listFriends.put(f.id, f);
                tmp.put(f.login, f);

                searchOnLocation.append(separator).append(f.id);
                separator = ",";
            }
        }

        Call<List<Group>> callGroup = api.getFriendsGroups();
        Response<List<Group>> responseGroup = callGroup.execute();
        if (Tools.apiIsSuccessful(responseGroup))
            groups = responseGroup.body();

        if (searchOnLocation.length() == 0)
            return;

        setLoadingProgress(getString(R.string.info_loading_locations), 1, 2);

        Call<List<Locations>> c = app.getApiService().getLocationsUsers(AppSettings.getAppCampus(app), searchOnLocation.toString(), 100, 1);

        Response<List<Locations>> response = c.execute();
        if (Tools.apiIsSuccessful(response)) {
            locations = new HashMap<>(response.body().size());
            for (Locations l : response.body())
                locations.put(l.user.login, l);
        }

        setLoadingProgress(getString(R.string.info_api_finishing), 2, 2);

        list = new ArrayList<>();
        if (locations != null) {
            Collection<FriendsSmall> s = tmp.values();

            TreeSet<FriendsSmall> haveLocation = new TreeSet<>();
            TreeSet<FriendsSmall> noLocation = new TreeSet<>();
            for (FriendsSmall f : s) {
                if (locations.containsKey(f.login))
                    haveLocation.add(f);
                else
                    noLocation.add(f);
            }

            list.addAll(haveLocation);
            list.addAll(noLocation);
        } else
            list.addAll(tmp.values());
    }

    @Override
    public String getToolbarName() {
        return null;
    }

    @Override
    public void setViewContent() {
        swipeRefreshLayout.setRefreshing(false);
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

            if (recyclerView.getItemDecorationCount() == 0 || recyclerView.getItemDecorationAt(0) == null)
                recyclerView.addItemDecoration(new ItemDecoration(getResources().getDimensionPixelSize(R.dimen.list_spacing), noOfColumns), 0);

            adapter = new RecyclerViewAdapterFriends(this, list, locations);
            adapter.setClickListener(this);
            adapter.setSelectionListener(this);
            if (spinnerGroupSelected == 0)
                recyclerView.setAdapter(adapter);

            List<String> list = new ArrayList<>();
            list.add(getString(R.string.friends_groups_all));
            if (groups != null)
                for (Group g : groups) {
                    list.add(g.getName(this));
                }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item_large, list);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setSelection(spinnerGroupSelected);

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
            swipeRefreshLayout.setOnRefreshListener(null);
        } else {
            linearLayoutHeaderSelection.setVisibility(View.GONE);
            linearLayoutHeader.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setOnRefreshListener(this);
        }

        selection = selected;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        List<FriendsSmall> data = list;
        Group group;
        spinnerGroupSelected = position;

        if (position > 0 && groups != null && position - 1 < groups.size() && position - 1 >= 0) {
            TreeSet<FriendsSmall> listHaveLocation = new TreeSet<>();
            TreeSet<FriendsSmall> listNoLocation = new TreeSet<>();

            group = groups.get(position - 1);
            for (Integer pos : group.users) {
                FriendsSmall friend = listFriends.get(pos);

                if (locations != null && locations.containsKey(friend.login))
                    listHaveLocation.add(friend);
                else
                    listNoLocation.add(friend);
            }

            data = new ArrayList<>();
            data.addAll(listHaveLocation);
            data.addAll(listNoLocation);
        }

        adapter = new RecyclerViewAdapterFriends(this, data, locations);
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
                Toast.makeText(FriendsActivity.this, R.string.friends_adding_to_group, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(FriendsActivity.this, R.string.friends_removing_from_group, Toast.LENGTH_SHORT).show();
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
        if (selection != null)
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
            group[i] = groups.get(i).getName(this);
        return group;
    }

    @Override
    public final Object onRetainCustomNonConfigurationInstance() {
        DataWrapper d = new DataWrapper();
        d.listFriends = listFriends;
        d.groups = groups;
        d.locations = locations;
        return d;
    }

    @Override
    public void onRefresh() {
        listFriends = null;
        list = null;
        groups = null;
        locations = null;
        refresh();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SAVED_STATE_SPINNER_GROUP_SELECTED, spinnerGroupSelected);
        super.onSaveInstanceState(outState);
    }

    private class DataWrapper {
        SparseArray<FriendsSmall> listFriends;
        List<Group> groups;
        HashMap<String, Locations> locations;
    }
}
