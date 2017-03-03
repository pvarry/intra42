package com.paulvarry.intra42.activity.users;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.paulvarry.intra42.Adapter.GridAdapterUsers;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activity.user.UserActivity;
import com.paulvarry.intra42.api.model.UsersLTE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class UsersFriendsFragment
        extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    AppClass app;
    TextView textViewError;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ConstraintLayout constraintLayoutLoading;
    private ConstraintLayout constraintLayoutError;
    private GridView listView;
    private List<UsersLTE> list;

    ValueEventListener friendsEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            GenericTypeIndicator<HashMap<String, String>> t = new GenericTypeIndicator<HashMap<String, String>>() {
            };
            HashMap<String, String> messages = snapshot.getValue(t);
            if (messages == null) {
                setViewHide();
                constraintLayoutError.setVisibility(View.VISIBLE);
                textViewError.setText(R.string.nothing_to_show);
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
            Log.w("fire", "Failed to read value.", error.toException());

            setViewHide();
            constraintLayoutError.setVisibility(View.VISIBLE);
            textViewError.setText(R.string.network_error);
        }
    };

    public UsersFriendsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UsersSearchFragment.
     */
    public static UsersFriendsFragment newInstance() {
        return new UsersFriendsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (AppClass) getActivity().getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__basic_grid_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (GridView) view.findViewById(R.id.gridView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        constraintLayoutLoading = (ConstraintLayout) view.findViewById(R.id.constraintLayoutLoading);
        constraintLayoutError = (ConstraintLayout) view.findViewById(R.id.constraintOnError);

        textViewError = (TextView) view.findViewById(R.id.textViewError);

        swipeRefreshLayout.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        setViewHide();
        constraintLayoutLoading.setVisibility(View.VISIBLE);

        onRefresh();
    }

    public void setView() {
        setViewHide();
        if (list == null || list.isEmpty()) {
            constraintLayoutError.setVisibility(View.VISIBLE);
            listView.setAdapter(null);
            textViewError.setText(R.string.nothing_to_show);
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            GridAdapterUsers adapter = new GridAdapterUsers(getContext(), list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setViewHide() {
        swipeRefreshLayout.setVisibility(View.GONE);
        constraintLayoutLoading.setVisibility(View.GONE);
        constraintLayoutError.setVisibility(View.GONE);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (list.size() > position)
            UserActivity.openIt(getContext(), list.get(position), app);
    }


    @Override
    public void onRefresh() {
//        swipeRefreshLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeRefreshLayout.setRefreshing(true);
//            }
//        });


        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (app.firebaseRefFriends != null)
            app.firebaseRefFriends.addValueEventListener(friendsEventListener);
        else {
            setViewHide();
            constraintLayoutError.setVisibility(View.VISIBLE);
            textViewError.setText(R.string.auth_error);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (app.firebaseRefFriends != null)
            app.firebaseRefFriends.removeEventListener(friendsEventListener);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final UsersLTE user = list.get(position);
        String[] items = {getString(R.string.remove_from_friends), getString(R.string.view_profile)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    UserActivity.openIt(getContext(), user);

            }
        });
        builder.show();
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}