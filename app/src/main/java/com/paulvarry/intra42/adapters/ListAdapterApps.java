package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Apps;
import com.paulvarry.intra42.utils.mImage;
import com.veinhorn.tagview.TagView;

import java.util.List;

public class ListAdapterApps extends BaseAdapter {

    private final Context context;
    private List<Apps> appsList;

    public ListAdapterApps(Context context, List<Apps> apps) {

        this.context = context;
        this.appsList = apps;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return appsList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Apps getItem(int position) {
        return appsList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_view_apps, parent, false);
            holder.imageViewIcon = convertView.findViewById(R.id.imageViewIcon);
            holder.textViewName = convertView.findViewById(R.id.textViewName);
            holder.textViewSub = convertView.findViewById(R.id.textViewSub);
            holder.textVieWebSite = convertView.findViewById(R.id.textViewWebSite);
            holder.tagViewOfficialApp = convertView.findViewById(R.id.tagViewOfficialApp);
            holder.textViewBack = convertView.findViewById(R.id.textViewBack);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final Apps app = getItem(position);

        if (app.name != null && !app.name.isEmpty()) {
            holder.textViewName.setVisibility(View.VISIBLE);
            holder.textViewName.setText(app.name);
        } else
            holder.textViewName.setVisibility(View.GONE);

        if (app.description != null && !app.description.isEmpty()) {
            holder.textViewSub.setVisibility(View.VISIBLE);
            holder.textViewSub.setText(app.description);
        } else
            holder.textViewSub.setVisibility(View.GONE);

        if (app.website != null && !app.website.isEmpty()) {
            holder.textVieWebSite.setVisibility(View.VISIBLE);
            holder.textVieWebSite.setText(app.website);
        } else
            holder.textVieWebSite.setVisibility(View.GONE);

        if (app.roles != null && !app.roles.isEmpty()) {
            boolean official = false;
            for (Apps.Role r : app.roles) {
                if (r.id == 10)
                    official = true;
            }
            if (official)
                holder.tagViewOfficialApp.setVisibility(View.VISIBLE);
            else
                holder.tagViewOfficialApp.setVisibility(View.GONE);
        } else
            holder.tagViewOfficialApp.setVisibility(View.GONE);

        if (app._public)
            holder.textViewBack.setVisibility(View.GONE);
        else
            holder.textViewBack.setVisibility(View.VISIBLE);

        Uri uri = null;
        if (app.image != null && !app.image.isEmpty()) {
            String url = "https://cdn.intra.42.fr" + app.image;

            uri = Uri.parse(url.replace("/uploads", ""));
        }
        mImage.setPicasso(context, uri, holder.imageViewIcon, R.drawable.ic_app_no_image);

        return convertView;
    }

    private static class ViewHolder {

        private ImageView imageViewIcon;
        private TextView textViewName;
        private TextView textViewSub;
        private TextView textVieWebSite;
        private TagView tagViewOfficialApp;
        private TextView textViewBack;
    }
}