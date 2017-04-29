package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.TopicActivity;
import com.paulvarry.intra42.api.model.Tags;
import com.paulvarry.intra42.api.model.Topics;
import com.paulvarry.intra42.utils.UserImage;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;
import java.util.Locale;

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
            holder.imageViewUser = (ImageView) convertView.findViewById(R.id.imageViewUser);
            holder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
            holder.textViewSummary = (TextView) convertView.findViewById(R.id.textViewSummary);
            holder.completionViewTags = (CompletionViewTags) convertView.findViewById(R.id.completion_view_tags);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Topics topic = getItem(position);

        UserImage.setImage(context, topic.author, holder.imageViewUser);
        holder.textViewTitle.setText(topic.name);

        if (topic.tags == null || topic.tags.size() == 0) {
            holder.completionViewTags.setVisibility(View.GONE);
        } else {
            ArrayAdapter<Tags> adapterTags = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, topic.tags);
            holder.completionViewTags.setAdapter(adapterTags);
            holder.completionViewTags.allowDuplicates(false);
            holder.completionViewTags.allowCollapse(false);

            holder.completionViewTags.clear();
            Editable text = holder.completionViewTags.getText();
            if (text != null) text.clear();
            for (Tags t : topic.tags)
                holder.completionViewTags.addObject(t);

//        holder.completionViewTags.setPrefix(topic.name + " ");
            holder.completionViewTags.setFocusable(false);
            holder.completionViewTags.setCursorVisible(false);
            holder.completionViewTags.setClickable(true);

            holder.completionViewTags.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    parent.callOnClick();
                    TopicActivity.openIt(context, topic);
                }
            });
        }

        String summary = topic.author.login;
        PrettyTime p = new PrettyTime(Locale.getDefault());
        if (topic.updatedAt != null)
            summary += " • " + p.format(topic.updatedAt);
        String flag = topic.language.getFlag();
        if (flag != null)
            summary += " " + flag;
        holder.textViewSummary.setText(summary);

        return convertView;
    }

    private static class ViewHolder {

        private ImageView imageViewUser;
        private TextView textViewTitle;
        private CompletionViewTags completionViewTags;
        private TextView textViewSummary;
    }
}