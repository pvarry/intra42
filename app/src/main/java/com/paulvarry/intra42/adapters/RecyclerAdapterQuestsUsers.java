package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.QuestsUsers;
import com.paulvarry.intra42.utils.DateTool;

import java.util.List;

public class RecyclerAdapterQuestsUsers extends RecyclerView.Adapter<RecyclerAdapterQuestsUsers.ViewHolder> {

    private List<QuestsUsers> questsUsersList;

    RecyclerAdapterQuestsUsers(List<QuestsUsers> questsUsersList) {
        this.questsUsersList = questsUsersList;
    }

    @Override
    public int getItemCount() {
        return questsUsersList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_quests_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final QuestsUsers questsUsers = questsUsersList.get(position);
        Context c = holder.itemView.getContext();

        holder.imageViewStatus.setVisibility(View.INVISIBLE);
        Boolean endAtIsFuture = DateTool.isInFuture(questsUsers.end_at);

        if (questsUsers.validatedAt != null) { // validated
            holder.imageViewStatus.setVisibility(View.VISIBLE);
            holder.textViewStatus.setText(R.string.quest_validated);
            holder.textViewStatus.setTextColor(ContextCompat.getColor(c, R.color.colorSuccess));
            holder.imageViewStatus.setImageResource(R.drawable.ic_check_black_24dp);
            holder.imageViewStatus.setColorFilter(ContextCompat.getColor(c, R.color.colorSuccess));
        } else if (endAtIsFuture != null) {
            if (endAtIsFuture) {
                holder.textViewStatus.setText(R.string.quest_in_progress);
            } else { // failed
                holder.imageViewStatus.setVisibility(View.VISIBLE);
                holder.textViewStatus.setText(R.string.quest_failed);
                holder.textViewStatus.setTextColor(ContextCompat.getColor(c, R.color.colorFail));
                holder.imageViewStatus.setImageResource(R.drawable.ic_close_black_24dp);
                holder.imageViewStatus.setColorFilter(ContextCompat.getColor(c, R.color.colorFail));
            }
        } else {
            holder.textViewStatus.setText(R.string.quest_status_unknown);//TODO: change color
        }

        holder.textViewDate.setText(null);
        if (questsUsers.end_at != null)
            holder.textViewDate.setText(DateTool.getDateLong(questsUsers.end_at));
        holder.textViewAdvancement.setVisibility(View.GONE);
        if (questsUsers.advancement != null) {
            holder.textViewAdvancement.setVisibility(View.VISIBLE);
            holder.textViewAdvancement.setText(c.getString(R.string.quest_progress, questsUsers.advancement));
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewStatus;
        private TextView textViewStatus;
        private TextView textViewDate;
        private TextView textViewAdvancement;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewStatus = itemView.findViewById(R.id.imageViewStatus);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewAdvancement = itemView.findViewById(R.id.textViewProgress);
        }
    }
}