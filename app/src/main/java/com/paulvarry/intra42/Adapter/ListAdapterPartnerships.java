package com.paulvarry.intra42.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activity.user.UserActivity;
import com.paulvarry.intra42.api.model.Partnerships;

import java.util.List;

public class ListAdapterPartnerships extends BaseAdapter {

    private final UserActivity activity;
    private List<Partnerships> partnerships;

    public ListAdapterPartnerships(UserActivity activity, List<Partnerships> partnerships) {

        this.activity = activity;
        this.partnerships = partnerships;
    }

    @Override
    public int getCount() {
        return partnerships.size();
    }

    @Override
    public Partnerships getItem(int position) {
        return partnerships.get(position);
    }

    @Override
    public long getItemId(int position) {
        return partnerships.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_view_partnerships, parent, false);
            holder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
            holder.textViewTier = (TextView) convertView.findViewById(R.id.textViewTier);
            holder.textViewDescription = (TextView) convertView.findViewById(R.id.textViewDescription);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Partnerships partnerships = getItem(position);

        holder.textViewTitle.setText(partnerships.name);
        holder.textViewDescription.setText(partnerships.slug);

        if (partnerships.tier != 0) {
            String string = "T" + String.valueOf(partnerships.tier);
            holder.textViewTier.setText(string);
        } else
            holder.textViewTier.setVisibility(View.GONE);

        return convertView;
    }

    static class ViewHolder {

        private TextView textViewTitle;
        private TextView textViewTier;
        private TextView textViewDescription;

    }
}
