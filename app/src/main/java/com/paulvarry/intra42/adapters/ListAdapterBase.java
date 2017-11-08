package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.BaseItem;

import java.util.List;

public class ListAdapterBase<T extends BaseItem> extends BaseAdapter {

    private final Context context;
    private List<T> itemList;

    public ListAdapterBase(Context context, List<T> items) {

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
    public BaseItem getItem(int position) {
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

            convertView = vi.inflate(R.layout.list_view_, parent, false);

            holder.textViewName = convertView.findViewById(R.id.textViewName);
            holder.textViewSub = convertView.findViewById(R.id.textViewSub);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BaseItem item = getItem(position);

        String name = item.getName();
        if (name != null && !name.isEmpty()) {
            holder.textViewName.setVisibility(View.VISIBLE);
            holder.textViewName.setText(name);
        } else
            holder.textViewName.setVisibility(View.GONE);

        String description = item.getSub();
        if (description != null && !description.isEmpty()) {
            holder.textViewSub.setVisibility(View.VISIBLE);
            holder.textViewSub.setText(description);
        } else
            holder.textViewSub.setVisibility(View.GONE);


        return convertView;
    }

    private static class ViewHolder {

        private TextView textViewName;
        private TextView textViewSub;
    }
}