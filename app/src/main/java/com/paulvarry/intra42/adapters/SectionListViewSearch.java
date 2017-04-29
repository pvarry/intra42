package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.BaseItem;

import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;

public class SectionListViewSearch extends BaseAdapter
        implements PinnedSectionListView.PinnedSectionListAdapter {

    Context context;
    List<Item> list;


    public SectionListViewSearch(Context context, List<Item> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Item getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_view__, parent, false);

            holder.linearLayoutTitle = (LinearLayout) convertView.findViewById(R.id.linearLayoutTitle);
            holder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);

            holder.linearLayoutContent = (LinearLayout) convertView.findViewById(R.id.linearLayoutContent);
            holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.textViewSub = (TextView) convertView.findViewById(R.id.textViewSub);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item i = getItem(position);

        if (isItemViewTypePinned(getItemViewType(position))) {
            holder.linearLayoutContent.setVisibility(View.GONE);
            holder.linearLayoutTitle.setVisibility(View.VISIBLE);

            holder.textViewTitle.setText(i.title);
        } else {
            holder.linearLayoutContent.setVisibility(View.VISIBLE);
            holder.linearLayoutTitle.setVisibility(View.GONE);

            holder.textViewName.setText(i.item.getName());
            if (i.item.getSub() != null)
                holder.textViewSub.setText(i.item.getSub());
            else
                holder.textViewSub.setVisibility(View.GONE);
        }


        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    // We implement this method to return 'true' for all view types we want to pin
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Item.SECTION;
    }

    private static class ViewHolder {

        private LinearLayout linearLayoutTitle;
        private TextView textViewTitle;

        private LinearLayout linearLayoutContent;
        private TextView textViewName;
        private TextView textViewSub;

    }

    public static class Item<T extends BaseItem> {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final T item;
        public final String title;

        public Item(int type, T item, String defaultTitle) {
            this.type = type;
            this.item = item;
            this.title = defaultTitle;
        }

        @Override
        public String toString() {
            return item.getName();
        }

    }
}