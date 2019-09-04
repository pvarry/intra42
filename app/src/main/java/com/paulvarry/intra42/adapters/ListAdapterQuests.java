package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Quests;
import com.paulvarry.intra42.api.model.QuestsUsers;
import com.paulvarry.intra42.utils.DateTool;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterQuests extends BaseAdapter {

    private final Context context;
    private List<List<QuestsUsers>> questsUsersList;

    public ListAdapterQuests(Context context, SparseArray<List<QuestsUsers>> questsUsersList) {

        this.context = context;
        this.questsUsersList = new ArrayList<>();

        for (int i = 0; i < questsUsersList.size(); i++)
            this.questsUsersList.add(questsUsersList.valueAt(i));
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return questsUsersList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public List<QuestsUsers> getItem(int position) {
        return questsUsersList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_view_quests, parent, false);
            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.textViewSummary = convertView.findViewById(R.id.textViewSummary);
            holder.textViewStatus = convertView.findViewById(R.id.textViewStatus);
            holder.textViewRetryLeft = convertView.findViewById(R.id.textViewRetryLeft);
            holder.recyclerView = convertView.findViewById(R.id.recyclerView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final List<QuestsUsers> item = getItem(position);
        final QuestsUsers questUser = item.get(0);
        final Quests quest = questUser.quest;

        holder.textViewTitle.setText(quest.name);
        holder.textViewSummary.setText(quest.description);

        holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
        Boolean endAtIsFuture = DateTool.isInFuture(questUser.end_at);
        if (questUser.validatedAt != null) { // validated
            holder.textViewStatus.setText(R.string.quest_validated);
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.colorSuccess));
        } else if (endAtIsFuture != null) {
            if (endAtIsFuture) {
                holder.textViewStatus.setText(R.string.quest_in_progress);
            } else { // failed
                holder.textViewStatus.setText(R.string.quest_failed);
                holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.colorFail));
            }
        } else {
            holder.textViewStatus.setText(R.string.quest_status_unknown);
            holder.textViewStatus.setTextColor(ContextCompat.getColor(context, R.color.colorGray)); //TODO: change color
        }

        holder.textViewRetryLeft.setVisibility(View.GONE);
        if (questUser.validatedAt == null && item.size() <= 3) {
            holder.textViewRetryLeft.setVisibility(View.VISIBLE);
            holder.textViewRetryLeft.setText(context.getString(R.string.quest_retry_left, 3 - item.size()));
        }

        if (holder.recyclerView.getItemDecorationCount() > 0)
            holder.recyclerView.removeItemDecorationAt(0);

        LinearLayoutManager lm = new LinearLayoutManager(context);
        holder.recyclerView.setLayoutManager(lm);
        holder.recyclerView.addItemDecoration(new ItemDecorationNotLast(context));
        holder.recyclerView.setAdapter(new RecyclerAdapterQuestsUsers(item));
        holder.recyclerView.setNestedScrollingEnabled(false);

        return convertView;
    }

    private static class ViewHolder {

        private TextView textViewTitle;
        private TextView textViewSummary;
        private TextView textViewStatus;
        private TextView textViewRetryLeft;
        private RecyclerView recyclerView;
    }
}