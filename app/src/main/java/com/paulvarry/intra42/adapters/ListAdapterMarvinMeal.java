package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.cantina.MarvinMeals;
import com.paulvarry.intra42.utils.DateTool;

import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;

public class ListAdapterMarvinMeal
        extends BaseAdapter
        implements PinnedSectionListView.PinnedSectionListAdapter {

    private final Context context;
    private List<Item> mealList;

    public ListAdapterMarvinMeal(Context context, List<Item> list) {

        this.context = context;
        this.mealList = list;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mealList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public MarvinMeals getItem(int position) {
        return mealList.get(position).item;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (vi == null)
                return null;

            holder = new ViewHolder();
            convertView = vi.inflate(R.layout.list_view_marvin_meal, parent, false);

            holder.textViewDateDay = convertView.findViewById(R.id.textViewDateDay);
            holder.textViewDateMonth = convertView.findViewById(R.id.textViewDateMonth);
            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.textViewSummary = convertView.findViewById(R.id.textViewSummary);

            convertView.setTag(holder);

        } else
            holder = (ViewHolder) convertView.getTag();

        MarvinMeals item = getItem(position);
        if (item == null)
            return null;
        String menu;

        holder.textViewDateDay.setText(DateTool.getDay(item.beginAt));
        holder.textViewDateMonth.setText(DateTool.getMonthMedium(item.beginAt));

        menu = item.menu.replace("\r", "").trim();

        holder.textViewTitle.setText(menu);

        String summary;
        summary = DateUtils.formatDateRange(context, item.beginAt.getTime(), item.endAt.getTime(), DateUtils.FORMAT_SHOW_TIME);
        summary += " • $" + String.valueOf(item.price);
        holder.textViewSummary.setText(summary);

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return mealList.get(position).type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Item.SECTION;
    }

    private static class ViewHolder {

        TextView textViewDateDay;
        TextView textViewDateMonth;
        TextView textViewTitle;
        TextView textViewSummary;
    }

    public static class Item {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final MarvinMeals item;
        public final String title;

        public Item(int type, MarvinMeals item, String defaultTitle) {
            this.type = type;
            this.item = item;
            this.title = defaultTitle;
        }

    }

}
