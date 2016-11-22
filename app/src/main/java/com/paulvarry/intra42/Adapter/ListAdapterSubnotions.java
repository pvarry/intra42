package com.paulvarry.intra42.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.Subnotions;

import java.util.List;

public class ListAdapterSubnotions extends BaseAdapter {

    private final Context context;
    private List<Subnotions> subnotionsList;

    public ListAdapterSubnotions(Context context, List<Subnotions> subnotionsList) {

        this.context = context;
        this.subnotionsList = subnotionsList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return subnotionsList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Subnotions getItem(int position) {
        return subnotionsList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return subnotionsList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_view_notions, parent, false);

            holder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
            holder.textViewSub = (TextView) convertView.findViewById(R.id.textViewSub);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Subnotions notions = getItem(position);

        holder.textViewTitle.setText(notions.name);
        holder.textViewSub.setText(notions.slug);

        return convertView;
    }

    private static class ViewHolder {
        private TextView textViewTitle;
        private TextView textViewSub;
    }
}
