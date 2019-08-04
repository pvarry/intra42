package com.paulvarry.intra42.adapters;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Tools;

import java.util.List;

public class ListAdapterEvents extends BaseAdapter {

    private final Context context;
    private List<Events> eventsList;

    public ListAdapterEvents(Context context, List<Events> projectsList) {

        this.context = context;
        this.eventsList = projectsList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return eventsList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Events getItem(int position) {
        return eventsList.get(position);
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

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater == null)
                return null;
            convertView = inflater.inflate(R.layout.list_view_event, parent, false);

            holder.viewRegistered = convertView.findViewById(R.id.viewRegistered);
            holder.textViewDateDay = convertView.findViewById(R.id.textViewDateDay);
            holder.textViewDateMonth = convertView.findViewById(R.id.textViewDateMonth);
            holder.textViewName = convertView.findViewById(R.id.textViewName);
            holder.textViewTime = convertView.findViewById(R.id.textViewTime);
            holder.textViewPlace = convertView.findViewById(R.id.textViewPlace);
            holder.textViewFull = convertView.findViewById(R.id.textViewFull);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Events item = getItem(position);

        holder.textViewDateDay.setText(DateTool.getDay(item.beginAt));
        holder.textViewDateMonth.setText(DateTool.getMonthMedium(item.beginAt));
        holder.textViewName.setText(item.name);

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        if (item.kind != null) {
            String text = context.getString(item.kind.getName());
            stringBuilder.append(text);
            stringBuilder.append("  ");
            int color = ContextCompat.getColor(context, item.kind.getColorRes());
            int length = text.length();

            stringBuilder.setSpan(new ForegroundColorSpan(color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            stringBuilder.setSpan(new TypefaceSpan("monospace"), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(new TextAppearanceSpan("monospace", Typeface.BOLD, Math.round(Tools.dpToPx(context, 18)), ColorStateList.valueOf(color), ColorStateList.valueOf(color)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        stringBuilder.append(item.name);
        holder.textViewName.setText(stringBuilder);

        holder.viewRegistered.setVisibility(View.GONE);

        String time;
        time = DateUtils.formatDateRange(context, item.beginAt.getTime(), item.endAt.getTime(), DateUtils.FORMAT_SHOW_TIME);
        if (time.length() > 30)
            time = time.replace(" – ", "\n");
        holder.textViewTime.setText(time);
        holder.textViewPlace.setText(item.location);

        if (item.maxPeople != null && item.nbrSubscribers >= item.maxPeople && item.maxPeople > 0) {
            holder.textViewFull.setVisibility(View.VISIBLE);
        } else
            holder.textViewFull.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder {

        View viewRegistered;
        TextView textViewDateDay;
        TextView textViewDateMonth;
        TextView textViewName;
        TextView textViewTime;
        TextView textViewPlace;
        TextView textViewFull;
    }
}