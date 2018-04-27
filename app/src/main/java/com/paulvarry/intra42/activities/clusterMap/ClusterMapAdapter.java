package com.paulvarry.intra42.activities.clusterMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.cluster_map_contribute.Cluster;
import com.paulvarry.intra42.api.cluster_map_contribute.Location;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.ThemeHelper;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.UserImage;
import com.paulvarry.intra42.utils.clusterMap.ClusterStatus;

public class ClusterMapAdapter extends RecyclerView.Adapter<ClusterMapAdapter.ViewHolderComputer> {

    private Cluster cluster;
    private ClusterStatus clusterStatus;
    private LayoutInflater li;
    private Context context;

    private float itemPadding2dp;
    private int itemPadding3dp;
    private float baseItemWidth;
    private float baseItemHeight;

    public ClusterMapAdapter(Context context, Cluster cluster, ClusterStatus clusterStatus) {
        this.cluster = cluster;
        this.context = context;
        this.clusterStatus = clusterStatus;

        li = LayoutInflater.from(context);
        itemPadding2dp = Tools.dpToPx(context, 2);
        itemPadding3dp = (int) Tools.dpToPx(context, 3);
        baseItemHeight = Tools.dpToPx(context, 42);
        baseItemWidth = Tools.dpToPx(context, 35);
    }

    @NonNull
    @Override
    public ViewHolderComputer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolderComputer(li, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderComputer holder, int position) {
        int x = position % cluster.sizeX;
        int y = position / cluster.sizeX;
        holder.bind(cluster.map[x][y], position);
    }

    @Override
    public int getItemCount() {
        return cluster.sizeX * cluster.sizeY;
    }

    class ViewHolderComputer extends RecyclerView.ViewHolder {

        private ImageView imageViewContent;

        ViewHolderComputer(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.grid_layout_cluster_map, parent, false));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            imageViewContent = itemView.findViewById(R.id.imageView);
        }

        void bind(Location location, int position) {

            UsersLTE user;
            user = clusterStatus.getUserInLocation(location);

            if (location.kind == Location.Kind.USER) {


                if (location.host == null) {
                    imageViewContent.setImageResource(R.drawable.ic_missing_black_25dp);
                    imageViewContent.setColorFilter(ContextCompat.getColor(context, R.color.colorClusterMapComputerColor), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
                    if (user != null)
                        UserImage.setImageSmall(context, user, imageViewContent);
                    else {
                        imageViewContent.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_desktop_mac_black_custom));
                        imageViewContent.setColorFilter(ContextCompat.getColor(context, R.color.colorClusterMapComputerColor), android.graphics.PorterDuff.Mode.SRC_IN);
                    }
                }

            } else if (location.kind == Location.Kind.WALL)
                imageViewContent.setImageResource(R.color.colorClusterMapWall);
            else {
                imageViewContent.setImageResource(R.drawable.ic_add_black_24dp);
                imageViewContent.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.CLEAR);
            }

            GridLayoutManager.LayoutParams paramsGridLayout = (GridLayoutManager.LayoutParams) imageViewContent.getLayoutParams();
//            paramsGridLayout.columnSpec = GridLayout.spec(x);
//            paramsGridLayout.rowSpec = GridLayout.spec(y);
//            paramsGridLayout.setGravity(Gravity.FILL);
            paramsGridLayout.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            paramsGridLayout.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            paramsGridLayout.height = (int) (baseItemHeight * location.sizeY);
            paramsGridLayout.width = (int) (baseItemWidth * location.sizeX);

//            if (location.kind == Location.Kind.WALL) {
//                int paddingLeft = itemPadding2dp;
//                int paddingTop = itemPadding2dp;
//                int paddingRight = itemPadding2dp;
//                int paddingEnd = itemPadding2dp;
//
//                Location left = getPosition(x - 1, y);
//                if (left != null && left.kind == Location.Kind.WALL)
//                    paddingLeft = 0;
//                Location top = getPosition(x, y - 1);
//                if (top != null && top.kind == Location.Kind.WALL)
//                    paddingTop = 0;
//                Location right = getPosition(x + 1, y);
//                if (right != null && right.kind == Location.Kind.WALL)
//                    paddingRight = 0;
//                Location end = getPosition(x, y + 1);
//                if (end != null && end.kind == Location.Kind.WALL)
//                    paddingEnd = 0;

//                imageViewContent.setPadding(paddingLeft, paddingTop, paddingRight, paddingEnd);
//            } else
//                imageViewContent.setPadding(itemPadding2dp, itemPadding2dp, itemPadding2dp, itemPadding2dp);

            imageViewContent.setLayoutParams(paramsGridLayout);

            if (location.highlight) {
                GridLayoutManager.LayoutParams paramsFrameLayout = (GridLayoutManager.LayoutParams) imageViewContent.getLayoutParams();
                paramsFrameLayout.height = (int) (baseItemHeight * location.sizeY);
                paramsFrameLayout.width = (int) (baseItemWidth * location.sizeX);
                imageViewContent.setLayoutParams(paramsFrameLayout);

                imageViewContent.setPadding(itemPadding3dp, itemPadding3dp, itemPadding3dp, itemPadding3dp);

                TypedValue a = new TypedValue();
                context.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
                imageViewContent.setBackgroundResource(a.resourceId);

                imageViewContent.setBackgroundColor(ThemeHelper.getColorAccent(context));
            }
        }
    }

    class ViewHolderCorridor extends RecyclerView.ViewHolder {

        private ImageView imageViewContent;

        ViewHolderCorridor(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.grid_layout_cluster_map, parent, false));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            imageViewContent = itemView.findViewById(R.id.imageView);
        }

        void bind(Location location, int position) {

            if (location.kind == Location.Kind.USER) {


                if (location.host == null) {
                    imageViewContent.setImageResource(R.drawable.ic_missing_black_25dp);
                    imageViewContent.setColorFilter(ContextCompat.getColor(context, R.color.colorClusterMapComputerColor), android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
//                    if (user != null)
//                        UserImage.setImageSmall(context, user, imageViewContent);
//                    else {
                    imageViewContent.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_desktop_mac_black_custom));
                    imageViewContent.setColorFilter(ContextCompat.getColor(context, R.color.colorClusterMapComputerColor), android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
                }

            } else if (location.kind == Location.Kind.WALL)
                imageViewContent.setImageResource(R.color.colorClusterMapWall);
            else {
                imageViewContent.setImageResource(R.drawable.ic_add_black_24dp);
                imageViewContent.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.CLEAR);
            }

//            paramsGridLayout = (GridLayout.LayoutParams) view.getLayoutParams();
//            paramsGridLayout.columnSpec = GridLayout.spec(x);
//            paramsGridLayout.rowSpec = GridLayout.spec(y);
//            paramsGridLayout.setGravity(Gravity.FILL);
//            paramsGridLayout.height = GridLayout.LayoutParams.WRAP_CONTENT;
//            paramsGridLayout.width = GridLayout.LayoutParams.WRAP_CONTENT;
//            paramsGridLayout.height = (int) (baseItemHeight * location.sizeY);
//            paramsGridLayout.width = (int) (baseItemWidth * location.sizeX);

//            if (location.kind == Location.Kind.WALL) {
//                int paddingLeft = itemPadding2dp;
//                int paddingTop = itemPadding2dp;
//                int paddingRight = itemPadding2dp;
//                int paddingEnd = itemPadding2dp;
//
//                Location left = getPosition(x - 1, y);
//                if (left != null && left.kind == Location.Kind.WALL)
//                    paddingLeft = 0;
//                Location top = getPosition(x, y - 1);
//                if (top != null && top.kind == Location.Kind.WALL)
//                    paddingTop = 0;
//                Location right = getPosition(x + 1, y);
//                if (right != null && right.kind == Location.Kind.WALL)
//                    paddingRight = 0;
//                Location end = getPosition(x, y + 1);
//                if (end != null && end.kind == Location.Kind.WALL)
//                    paddingEnd = 0;
//
//                imageViewContent.setPadding(paddingLeft, paddingTop, paddingRight, paddingEnd);
//            } else
//                imageViewContent.setPadding(itemPadding2dp, itemPadding2dp, itemPadding2dp, itemPadding2dp);
//
//            view.setLayoutParams(paramsGridLayout);
//
//            if (location.highlight) {
//                FrameLayout.LayoutParams paramsFrameLayout = (FrameLayout.LayoutParams) imageViewContent.getLayoutParams();
//                paramsFrameLayout.height = (int) (baseItemHeight * location.sizeY);
//                paramsFrameLayout.width = (int) (baseItemWidth * location.sizeX);
//                imageViewContent.setLayoutParams(paramsFrameLayout);
//
//                imageViewContent.setPadding(0, 0, 0, 0);
//                view.setPadding(itemPadding3dp, itemPadding3dp, itemPadding3dp, itemPadding3dp);
//
//                TypedValue a = new TypedValue();
//                activity.getTheme().resolveAttribute(android.R.attr.windowBackground, a, true);
//                imageViewContent.setBackgroundResource(a.resourceId);
//
//                view.setBackgroundColor(ThemeHelper.getColorAccent(activity));
//            }
        }
    }
}
