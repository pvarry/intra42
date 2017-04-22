package com.paulvarry.intra42.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.UserImage;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.UsersLTE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GridAdapterUsers extends BaseAdapter {

    private Context context;
    private List<UsersLTE> users;
    private HashMap<String, Locations> locations;

    public GridAdapterUsers(Context context, List<UsersLTE> users, HashMap<String, Locations> locations) {
        this.context = context;
        this.users = users;
        this.locations = locations;
        sortUsers();
    }

    public GridAdapterUsers(Context context, List<UsersLTE> users, List<Locations> locations) {
        this.context = context;
        this.users = users;

        if (locations != null) {
            this.locations = new HashMap<>(locations.size());
            for (Locations l : locations)
                this.locations.put(l.user.login, l);
        }
        sortUsers();
    }

    public GridAdapterUsers(Context context, List<UsersLTE> users) {
        this.context = context;
        this.users = users;
    }

    private void sortUsers() {

        if (locations == null || users == null)
            return;

        List<UsersLTE> usersLogged = new ArrayList<>(locations.size());
        List<UsersLTE> usersNonLogged = new ArrayList<>(users.size() - locations.size());

        for (UsersLTE u : users) {
            if (locations.containsKey(u.login))
                usersLogged.add(u);
            else
                usersNonLogged.add(u);
        }

        users = new ArrayList<>(users.size());
        users.addAll(usersLogged);
        users.addAll(usersNonLogged);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public UsersLTE getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.grid_view_users, parent, false);
            holder.imageViewUsers = (ImageView) convertView.findViewById(R.id.imageViewUsers);
            holder.textViewUserLogin = (TextView) convertView.findViewById(R.id.textViewUserLogin);
            holder.linearLayoutLocation = (LinearLayout) convertView.findViewById(R.id.linearLayoutLocation);
            holder.textViewLocation = (TextView) convertView.findViewById(R.id.textViewLocation);

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

    static class ViewHolder {

        private ImageView imageViewUsers;
        private TextView textViewUserLogin;

        private LinearLayout linearLayoutLocation;
        private TextView textViewLocation;

    }
}
