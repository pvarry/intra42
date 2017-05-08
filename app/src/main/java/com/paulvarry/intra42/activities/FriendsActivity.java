package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.GridAdapterUsers;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.utils.AppSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class FriendsActivity extends BasicActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, BasicActivity.GetDataOnThread {

    List<UsersLTE> list;
    HashMap<String, Locations> locations;

    GridView gridView;
    GridAdapterUsers adapter;

    ValueEventListener friendsEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {
            };
            HashMap<String, String> messages = snapshot.getValue(t);
            if (messages == null) {
                Log.e("Firebase", "Message null");
                setViewError();
            } else {
                list = new ArrayList<>();
                Set<String> s = messages.keySet();
                for (String k : s) {
                    UsersLTE tmp = new UsersLTE();
                    tmp.id = Integer.decode(k);
                    tmp.login = messages.get(k);
                    list.add(tmp);
                }

                Collections.sort(list, new Comparator<UsersLTE>() {
                    @Override
                    public int compare(UsersLTE o1, UsersLTE o2) {
                        return o1.login.compareTo(o2.login);
                    }
                });

                refresh();
            }

        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.e("Firebase", "Failed to read value.", error.toException());

            setViewError();
        }
    };

    public static void openIt(Context context) {
        Intent intent = new Intent(context, FriendsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friends);
        activeHamburger();

        registerGetDataOnOtherThread(this);

        super.onCreate(savedInstanceState);

/*        if (app.firebaseRefFriends != null)
            app.firebaseRefFriends.addValueEventListener(friendsEventListener);
        else {
            setViewError();
        }*/

        navigationView.getMenu().getItem(5).getSubMenu().getItem(0).setChecked(true);
        gridView = (GridView) findViewById(R.id.gridView);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public StatusCode getDataOnOtherThread() {

        if (list == null)
            return StatusCode.EMPTY;

        String searchOn = "";
        String separator = "";

        for (UsersLTE u : list) {
            searchOn += separator + u.id;
            separator = ",";
        }

        Call<List<Locations>> c = app.getApiService().getLocationsUsers(AppSettings.getAppCampus(app), searchOn, 100, 1);
        try {
            Response<List<Locations>> response = c.execute();
            if (response != null && response.isSuccessful()) {
                locations = new HashMap<>(response.body().size());
                for (Locations l : response.body())
                    locations.put(l.user.login, l);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StatusCode.FINISH;
    }

    @Override
    public String getToolbarName() {
        return null;
    }

    @Override
    public void setViewContent() {
        if (list == null || list.isEmpty()) {
            setViewError();
        } else {
            adapter = new GridAdapterUsers(this, list, locations);
            gridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            gridView.setOnItemClickListener(this);
            gridView.setOnItemLongClickListener(this);
        }
    }

    @Override
    public String getEmptyText() {
        return "You can add friends from their profile";
    }

    @Override
    public void onPause() {
        super.onPause();
        if (app.firebaseRefFriends != null)
            app.firebaseRefFriends.removeEventListener(friendsEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (app.firebaseRefFriends != null)
            app.firebaseRefFriends.addValueEventListener(friendsEventListener);
        else {
            setViewError();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        UserActivity.openIt(app, adapter.getItem(position));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final UsersLTE user = adapter.getItem(position);
        String[] items = {getString(R.string.remove_from_friends), getString(R.string.view_profile)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_action_colon) + user.login);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    DatabaseReference ref = user.getFriendsFirebaseRef(app);
                    if (ref != null)
                        ref.removeValue();
                    else
                        Toast.makeText(app, "Can't perform this action", Toast.LENGTH_SHORT).show();
                } else if (which == 1)
                    UserActivity.openIt(app, user);

            }
        });
        builder.show();
        return true;
    }
}
