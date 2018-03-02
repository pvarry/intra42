package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.IBaseItemDetail;
import com.paulvarry.intra42.utils.AppSettings;

import java.util.List;

public class BaseListAdapterSlugDetail<T extends IBaseItemDetail> extends BaseAdapter {

    private final Context context;
    private List<T> itemList;

    public BaseListAdapterSlugDetail(Context context, List<T> items) {

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
    public T getItem(int position) {
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
            convertView = vi.inflate(R.layout.list_view__detail_summary, parent, false);

            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.textViewSummary = convertView.findViewById(R.id.textViewSummary);
            holder.textViewDetail = convertView.findViewById(R.id.textViewDetail);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final T item = getItem(position);

        String name = item.getName(context);
        if (name != null && !name.isEmpty()) {
            holder.textViewTitle.setVisibility(View.VISIBLE);
            holder.textViewTitle.setText(name);
        } else
            holder.textViewTitle.setVisibility(View.GONE);

        String description = item.getSub(context);
        if (AppSettings.Advanced.getAllowAdvancedData(context) && description != null && !description.isEmpty()) {
            holder.textViewSummary.setVisibility(View.VISIBLE);
            holder.textViewSummary.setText(description);
        } else
            holder.textViewSummary.setVisibility(View.GONE);

        String detail = item.getDetail(context);
        if (description != null && !description.isEmpty()) {
            holder.textViewDetail.setVisibility(View.VISIBLE);
            holder.textViewDetail.setText(detail);
        } else
            holder.textViewDetail.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder {

        private TextView textViewTitle;
        private TextView textViewSummary;
        private TextView textViewDetail;

    }
}