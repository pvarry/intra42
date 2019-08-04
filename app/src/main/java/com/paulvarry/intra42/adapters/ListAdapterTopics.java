package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.chip.ChipGroup;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.TopicActivity;
import com.paulvarry.intra42.api.model.Topics;
import com.paulvarry.intra42.utils.Tag;
import com.paulvarry.intra42.utils.UserImage;

import java.util.List;

public class ListAdapterTopics extends BaseAdapter {

    private final Context context;
    private List<Topics> topicsList;

    public ListAdapterTopics(Context context, List<Topics> topicsList) {

        this.context = context;
        this.topicsList = topicsList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return topicsList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Topics getItem(int position) {
        return topicsList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return topicsList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_view_topics, parent, false);
            holder.layoutParent = convertView.findViewById(R.id.list_topic_layoutParent);
            holder.imageViewUser = convertView.findViewById(R.id.imageViewUser);
            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.textViewSummary = convertView.findViewById(R.id.textViewSummary);
            holder.chipGroup = convertView.findViewById(R.id.chipGroup);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Topics topic = getItem(position);

        UserImage.setImage(context, topic.author, holder.imageViewUser);
        holder.textViewTitle.setText(topic.name);
        Tag.setTagForum(context, topic.tags, holder.chipGroup);
        holder.textViewSummary.setText(topic.getSub(context));

        holder.layoutParent.setOnClickListener(view -> TopicActivity.openIt(context, topic));
        return convertView;
    }

    private static class ViewHolder {

        private ViewGroup layoutParent;
        private ImageView imageViewUser;
        private TextView textViewTitle;
        private ChipGroup chipGroup;
        private TextView textViewSummary;
    }
}