package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.tools42.FriendsSmall;
import com.paulvarry.intra42.utils.ThemeHelper;
import com.paulvarry.intra42.utils.Tools;
import com.paulvarry.intra42.utils.UserImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class RecyclerViewAdapterFriends extends RecyclerView.Adapter<RecyclerViewAdapterFriends.RecyclerViewHolder> {

    private OnItemClickListener clickListener;
    private SelectionListener selectionListener;

    private Context context;
    private List<FriendsSmall> friends;
    private HashMap<String, Locations> locations;

    private HashSet<Integer> selectedPosition;

    private boolean stateSelection = false;

    public RecyclerViewAdapterFriends(Context context, List<FriendsSmall> friends, HashMap<String, Locations> locations) {
        this.context = context;
        this.friends = friends;
        this.locations = locations;
    }

    public void setClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setSelectionListener(SelectionListener selectionListener) {
        this.selectionListener = selectionListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        view = inflater.inflate(R.layout.grid_view_users_selection, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {

        UsersLTE user = friends.get(position);
        holder.textViewUserLogin.setText(user.login);
        UserImage.setImage(context, user, holder.imageViewUsers);

        if (locations != null && locations.containsKey(user.login)) {
            holder.linearLayoutLocation.setVisibility(View.VISIBLE);
            holder.textViewLocation.setText(locations.get(user.login).host);
        } else
            holder.linearLayoutLocation.setVisibility(View.GONE);

        if (stateSelection && selectedPosition != null) {
            int px = Tools.dpToPxInt(context, 4);
            holder.layoutChild.setPadding(px, px, px, px);
            if (selectedPosition.contains(position))
                holder.layoutParent.setBackgroundColor(ThemeHelper.getColorAccent(context));
            else
                holder.layoutParent.setBackgroundColor(Color.rgb(255, 255, 255));
        } else
            holder.layoutChild.setPadding(0, 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    private void selectionChanged() {

        if (selectedPosition.size() == 0) {
            stateSelection = false;
            selectionListener.onSelectionChanged(null);
            notifyDataSetChanged();
            return;
        }

        List<Integer> list = new ArrayList<>();

        list.addAll(selectedPosition);
        selectionListener.onSelectionChanged(list);
    }

    public interface OnItemClickListener {

        void onItemClick(int position, FriendsSmall clicked);
    }

    public interface SelectionListener {
        void onSelectionChanged(List<Integer> selected);
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ViewGroup layoutParent;
        private ImageView imageViewUsers;
        private TextView textViewUserLogin;
        private LinearLayout linearLayoutLocation;
        private TextView textViewLocation;
        private ViewGroup layoutChild;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            imageViewUsers = itemView.findViewById(R.id.imageViewUsers);
            textViewUserLogin = itemView.findViewById(R.id.textViewUserLogin);
            linearLayoutLocation = itemView.findViewById(R.id.linearLayoutLocation);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            layoutParent = itemView.findViewById(R.id.layoutParent);
            layoutChild = itemView.findViewById(R.id.layoutChild);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (stateSelection) {
                int position = getAdapterPosition();
                if (selectedPosition.contains(position))
                    selectedPosition.remove(position);
                else
                    selectedPosition.add(position);
                selectionChanged();
                notifyItemChanged(position);
            } else if (clickListener != null)
                clickListener.onItemClick(getAdapterPosition(), friends.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            if (stateSelection)
                return false;
            else {
                stateSelection = true;
                selectedPosition = new HashSet<>();
                selectedPosition.add(getAdapterPosition());
                selectionChanged();
                notifyDataSetChanged();
            }
            return true;
        }
    }
}