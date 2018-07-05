package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.TeamsUsers;
import com.paulvarry.intra42.utils.UserImage;

import java.util.List;

public class RecyclerAdapterUserTeam extends RecyclerView.Adapter<RecyclerAdapterUserTeam.ViewHolder> {

    Context activity;
    private List<TeamsUsers> users;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public RecyclerAdapterUserTeam(Context context, List<TeamsUsers> users) {

        this.activity = context;
        this.users = users;
    }

    public TeamsUsers getItem(int position) {
        return users.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_users_little, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final TeamsUsers user = getItem(position);

        if (user.leader)
            holder.imageViewStar.setVisibility(View.VISIBLE);
        else
            holder.imageViewStar.setVisibility(View.GONE);
        UserImage.setImage(activity, user, holder.imageViewUsers);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemTeamUserClick(holder.getAdapterPosition(), user);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return onItemLongClickListener != null && onItemLongClickListener.onItemTeamUserLongClick(holder.getAdapterPosition(), user);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).id;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemLongClickListener {

        /**
         * Called when a view has been clicked and held.
         *
         * @return true if the callback consumed the long click, false otherwise.
         */
        boolean onItemTeamUserLongClick(int position, TeamsUsers users);
    }

    public interface OnItemClickListener {

        void onItemTeamUserClick(int position, TeamsUsers users);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewUsers;
        private ImageView imageViewStar;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewUsers = itemView.findViewById(R.id.imageViewUsers);
            imageViewStar = itemView.findViewById(R.id.imageViewStar);
        }
    }
}
