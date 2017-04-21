package com.paulvarry.intra42.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.GridView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.paulvarry.intra42.Adapter.GridAdapterUsers;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.ui.BasicActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FriendsActivity extends BasicActivity {

    List<UsersLTE> list;
    GridView gridView;

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

                setView();
            }

        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
            Log.e("Firebase", "Failed to read value.", error.toException());

            setViewError();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friends);
        activeHamburger();

        super.onCreate(savedInstanceState);

        navigationView.getMenu().getItem(5).getSubMenu().getItem(0).setChecked(true);
        gridView = (GridView) findViewById(R.id.gridView);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
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
    public boolean getDataOnOtherThread() {
        return false;
    }

    @Override
    public boolean getDataOnMainThread() {
        if (app.firebaseRefFriends != null)
            app.firebaseRefFriends.addValueEventListener(friendsEventListener);
        else {
            setViewError();
        }
        return true;
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

            GridAdapterUsers adapter = new GridAdapterUsers(this, list);
            gridView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
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
}
