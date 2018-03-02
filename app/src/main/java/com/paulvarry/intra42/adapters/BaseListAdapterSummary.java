package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.IBaseItem;

import java.util.List;

public class BaseListAdapterSummary<T extends IBaseItem> extends BaseAdapter {

    private final Context context;
    private List<T> itemList;

    public BaseListAdapterSummary(Context context, List<T> items) {

        this.context = context;
        this.itemList = items;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        if (itemList == null)
            return 0;
        return itemList.size();
    }

    /**
     * Get the data BaseItem associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public IBaseItem getItem(int position) {
        return itemList.get(position);
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

            if (vi == null)
                return null;
            convertView = vi.inflate(R.layout.list_view__summary, parent, false);

            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.textViewSummary = convertView.findViewById(R.id.textViewSummary);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final IBaseItem item = getItem(position);

        String name = item.getName(context);
        if (name != null && !name.isEmpty()) {
            holder.textViewTitle.setVisibility(View.VISIBLE);
            holder.textViewTitle.setText(name);
        } else
            holder.textViewTitle.setVisibility(View.GONE);

        String summary = item.getName(context);
        if (name != null && !name.isEmpty()) {
            holder.textViewSummary.setVisibility(View.VISIBLE);
            holder.textViewSummary.setText(summary);
        } else
            holder.textViewSummary.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder {

        private TextView textViewTitle;
        private TextView textViewSummary;
    }
}