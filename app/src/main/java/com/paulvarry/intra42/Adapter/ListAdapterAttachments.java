package com.paulvarry.intra42.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Attachments;

import java.util.List;

public class ListAdapterAttachments extends BaseAdapter {

    private final Context context;
    List<Attachments> attachmentsList;

    public ListAdapterAttachments(Context context, List<Attachments> attachmentsList) {

        this.context = context;
        this.attachmentsList = attachmentsList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return attachmentsList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Attachments getItem(int position) {
        return attachmentsList.get(position);
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

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_view_attachments, parent, false);
            holder.imageViewIcon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
            holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.textViewSub = (TextView) convertView.findViewById(R.id.textViewSub);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final Attachments attachments = getItem(position);

        holder.textViewName.setText(attachments.name);
        holder.textViewSub.setText(attachments.url);

        return convertView;
    }

    private static class ViewHolder {

        private ImageView imageViewIcon;
        private TextView textViewName;
        private TextView textViewSub;

    }
}