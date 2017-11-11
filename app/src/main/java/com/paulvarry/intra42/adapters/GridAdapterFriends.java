package com.paulvarry.intra42.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.tools42.FriendsSmall;
import com.paulvarry.intra42.utils.UserImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GridAdapterFriends extends BaseAdapter {


    private HashMap<String, Locations> locations;
    private Context context;
    private List<FriendsSmall> friends;

    public GridAdapterFriends(Context context, List<FriendsSmall> friends, HashMap<String, Locations> locations) {
        this.context = context;
        this.friends = friends;
        this.locations = locations;
        sortUsers();
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public FriendsSmall getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return friends.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.grid_view_users, parent, false);
            holder.imageViewUsers = convertView.findViewById(R.id.imageViewUsers);
            holder.textViewUserLogin = convertView.findViewById(R.id.textViewUserLogin);
            holder.linearLayoutLocation = convertView.findViewById(R.id.linearLayoutLocation);
            holder.textViewLocation = convertView.findViewById(R.id.textViewLocation);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UsersLTE user = getItem(position);
        holder.textViewUserLogin.setText(user.login);
        UserImage.setImage(context, user, holder.imageViewUsers);

        if (locations != null && locations.containsKey(user.login)) {
            holder.linearLayoutLocation.setVisibility(View.VISIBLE);
            holder.textViewLocation.setText(locations.get(user.login).host);
        } else
            holder.linearLayoutLocation.setVisibility(View.GONE);

        return convertView;
    }

    private void sortUsers() {

        if (locations == null || friends == null)
            return;

        int capacity = friends.size() - locations.size();
        if (capacity < 0)
            capacity = 0;

        List<FriendsSmall> usersLogged = new ArrayList<>(locations.size());
        List<FriendsSmall> usersNonLogged = new ArrayList<>(capacity);

        for (FriendsSmall u : friends) {
            if (locations.containsKey(u.login))
                usersLogged.add(u);
            else
                usersNonLogged.add(u);
        }

        friends = new ArrayList<>(friends.size());
        friends.addAll(usersLogged);
        friends.addAll(usersNonLogged);
    }

    static class ViewHolder {

        private ImageView imageViewUsers;
        private TextView textViewUserLogin;

        private LinearLayout linearLayoutLocation;
        private TextView textViewLocation;

    }
}
