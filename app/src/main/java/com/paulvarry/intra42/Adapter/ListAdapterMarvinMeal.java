package com.paulvarry.intra42.Adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.DateTool;
import com.paulvarry.intra42.api.cantina.MarvinMeals;

import java.util.List;

public class ListAdapterMarvinMeal extends BaseAdapter {

    private final Context context;
    private List<MarvinMeals> mealList;

    public ListAdapterMarvinMeal(Context context, List<MarvinMeals> mealList) {

        this.context = context;
        this.mealList = mealList;
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
        return mealList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return mealList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_view_marvin_meal, parent, false);

            holder.textViewDateDay = (TextView) convertView.findViewById(R.id.textViewDateDay);
            holder.textViewDateMonth = (TextView) convertView.findViewById(R.id.textViewDateMonth);
            holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.textViewDescription = (TextView) convertView.findViewById(R.id.textViewDescription);
            holder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
        }

        MarvinMeals item = getItem(position);
        String mealKind;
        String menu;

        holder.textViewDateDay.setText(DateTool.getDay(item.beginAt));
        holder.textViewDateMonth.setText(DateTool.getMonthMedium(item.beginAt));

        menu = item.menu.replace("\r", "");
        boolean removeFirstWord = true;
        if (menu.startsWith("Breakfast"))
            mealKind = "Breakfast";
        else if (menu.startsWith("Brunch"))
            mealKind = "Brunch";
        else if (menu.startsWith("Lunch"))
            mealKind = "Lunch";
        else if (menu.startsWith("Dinner"))
            mealKind = "Dinner";
        else {
            mealKind = "Meal";
            removeFirstWord = false;
        }
        if (removeFirstWord) {
            menu = menu.replace(mealKind, "");
            if (menu.startsWith(" "))
                menu = menu.replaceFirst(" ", "");
            if (menu.startsWith("\n"))
                menu = menu.replaceFirst("\n", "");
        }

        holder.textViewName.setText(mealKind);
        holder.textViewDescription.setText(menu);

        String summary;
        summary = DateUtils.formatDateRange(context, item.beginAt.getTime(), item.endAt.getTime(), DateUtils.FORMAT_SHOW_TIME);
        summary += " • $" + String.valueOf(item.price);
        holder.textViewTime.setText(summary);

        return convertView;
    }

    private static class ViewHolder {

        TextView textViewDateDay;
        TextView textViewDateMonth;
        TextView textViewName;
        TextView textViewDescription;
        TextView textViewTime;
    }

}
