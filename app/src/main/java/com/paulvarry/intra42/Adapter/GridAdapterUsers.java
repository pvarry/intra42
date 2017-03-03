package com.paulvarry.intra42.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.UserImage;
import com.paulvarry.intra42.api.model.UsersLTE;

import java.util.List;

public class GridAdapterUsers extends BaseAdapter {

    Context context;
    private List<UsersLTE> users;

    public GridAdapterUsers(Context context, List<UsersLTE> users) {

        this.context = context;
        this.users = users;
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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UsersLTE user = getItem(position);
        holder.textViewUserLogin.setText(user.login);
        UserImage.setImage(context, user, holder.imageViewUsers);

        return convertView;
    }

    static class ViewHolder {

        private ImageView imageViewUsers;
        private TextView textViewUserLogin;

    }
}
