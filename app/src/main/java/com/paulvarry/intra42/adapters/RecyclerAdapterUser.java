package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.UserImage;

import java.util.List;

public class RecyclerAdapterUser extends RecyclerView.Adapter<RecyclerAdapterUser.ViewHolder> {

    Context activity;
    private List<UsersLTE> users;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public RecyclerAdapterUser(Context context, List<UsersLTE> users) {

        this.activity = context;
        this.users = users;
    }

    public UsersLTE getItem(int position) {
        return users.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final UsersLTE user = getItem(position);

        holder.linearLayoutLocation.setVisibility(View.GONE);
        UserImage.setImage(activity, user, holder.imageViewUsers);
        holder.textViewUserLogin.setText(user.login);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemUserClick(holder.getAdapterPosition(), user);
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

    interface OnItemLongClickListener {

        /**
         * Called when a view has been clicked and held.
         *
         * @return true if the callback consumed the long click, false otherwise.
         */
        boolean onItemTeamUserLongClick(int position, UsersLTE users);
    }

    public interface OnItemClickListener {

        void onItemUserClick(int position, UsersLTE users);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewUsers;
        private ViewGroup linearLayoutLocation;
        private TextView textViewUserLogin;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewUsers = itemView.findViewById(R.id.imageViewUsers);
            linearLayoutLocation = itemView.findViewById(R.id.linearLayoutLocation);
            textViewUserLogin = itemView.findViewById(R.id.textViewUserLogin);
        }
    }
}
