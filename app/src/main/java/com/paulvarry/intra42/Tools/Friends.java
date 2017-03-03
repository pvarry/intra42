package com.paulvarry.intra42.Tools;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.paulvarry.intra42.api.model.Users;

import java.util.HashMap;
import java.util.Set;

public class Friends {

    public static void actionAddRemove(final DatabaseReference reference, final Users user) {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {
                };
                HashMap<String, String> messages = dataSnapshot.getValue(t);

                if (user == null) {
                    Log.d("fire", "no");
                } else if (messages == null) {
                    reference.child(String.valueOf(user.id)).setValue(user.login);
                } else {
                    Set<String> s = messages.keySet();
                    for (String k : s) {
                        if (user.id == Integer.decode(k)) {
                            reference.child(String.valueOf(user.id)).removeValue();
                            return;
                        }
                    }
                    reference.child(String.valueOf(user.id)).setValue(user.login);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
