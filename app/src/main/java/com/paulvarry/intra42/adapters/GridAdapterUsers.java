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
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.UserImage;

import java.util.List;

public class GridAdapterUsers extends BaseAdapter {

    private Context context;
    private List<UsersLTE> users;

    public GridAdapterUsers(Context context, List<UsersLTE> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        if (users == null)
            return 0;
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
