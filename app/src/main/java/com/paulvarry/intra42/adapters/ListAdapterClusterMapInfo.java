package com.paulvarry.intra42.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.utils.clusterMap.ClusterStatus;

public class ListAdapterClusterMapInfo extends BaseAdapter {

    private final Context context;
    private ClusterStatus cluster;

    public ListAdapterClusterMapInfo(Context context, ClusterStatus clusterStatus) {

        this.context = context;
        this.cluster = clusterStatus;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return cluster.clusters.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Cluster getItem(int position) {
        return cluster.clusters.get(position);
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

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater == null)
                return null;
            convertView = inflater.inflate(R.layout.list_view_cluster_info, parent, false);

            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.textViewSummary = convertView.findViewById(R.id.textViewSummary);
            holder.progressBar = convertView.findViewById(R.id.progressBar);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Cluster info = getItem(position);

        holder.textViewTitle.setText(info.name);
        StringBuilder builder = new StringBuilder();
        if (info.highlightPosts == 0)
            builder.append(context.getString(R.string.cluster_map_info_highlight_nothing));
        else if (info.highlightPosts == 1)
            builder.append(context.getString(R.string.cluster_map_info_highlight_singular, info.highlightPosts));
        else
            builder.append(context.getString(R.string.cluster_map_info_highlight_plural, info.highlightPosts));
//        builder.append(" - ");
//        if (info.freePosts == 0)
//            builder.append(context.getString(R.string.cluster_map_info_vacant_posts_nothing));
//        else if (info.freePosts == 1)
//            builder.append(context.getString(R.string.cluster_map_info_vacant_posts_singular, info.freePosts));
//        else
//            builder.append(context.getString(R.string.cluster_map_info_vacant_posts_plural, info.freePosts));
        holder.textViewSummary.setText(builder);

        holder.progressBar.setIndeterminate(false);
        holder.progressBar.setMax(info.posts);
        holder.progressBar.setProgress(info.posts - info.freePosts - info.highlightPosts);
        holder.progressBar.setSecondaryProgress(info.posts - info.freePosts);

//        holder.progressBar.setSecondaryProgress(info.posts - info.freePosts);
//        if (cluster.layerStatus == ClusterMapActivity.LayerStatus.NONE)
//            holder.progressBar.setProgress(info.posts - info.freePosts);
//        else
//            holder.progressBar.setProgress(info.highlightPosts);

        return convertView;
    }

    private static class ViewHolder {

        TextView textViewTitle;
        TextView textViewSummary;
        ProgressBar progressBar;
    }
}