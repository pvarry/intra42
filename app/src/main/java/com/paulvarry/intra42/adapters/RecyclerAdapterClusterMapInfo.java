package com.paulvarry.intra42.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.utils.clusterMap.ClusterData;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerAdapterClusterMapInfo extends RecyclerView.Adapter<RecyclerAdapterClusterMapInfo.ViewHolder> {

    private final Context context;
    private ClusterData cluster;
    private OnItemClickListener listener;

    public RecyclerAdapterClusterMapInfo(Context context, ClusterData clusterStatus) {

        this.context = context;
        this.cluster = clusterStatus;
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    public Cluster getItem(int position) {
        return cluster.clusters.get(position);
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_cluster_info, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Cluster cluster = getItem(position);

        holder.textViewTitle.setText(cluster.name);
        StringBuilder builder = new StringBuilder();
        if (cluster.highlightPosts == 0)
            builder.append(context.getString(R.string.cluster_map_info_highlight_nothing));
        else if (cluster.highlightPosts == 1)
            builder.append(context.getString(R.string.cluster_map_info_highlight_singular, cluster.highlightPosts));
        else
            builder.append(context.getString(R.string.cluster_map_info_highlight_plural, cluster.highlightPosts));
        holder.textViewSummary.setText(builder);

        holder.progressBar.setIndeterminate(false);
        holder.progressBar.setMax(cluster.posts);
        holder.progressBar.setProgress(cluster.posts - cluster.freePosts - cluster.highlightPosts);
        holder.progressBar.setSecondaryProgress(cluster.posts - cluster.freePosts);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClicked(holder.getAdapterPosition(), cluster);
            }
        });
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

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return cluster.clusters.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, Cluster cluster);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewSummary;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewSummary = itemView.findViewById(R.id.textViewSummary);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}