package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.SlotsTools;

import java.util.List;

public class ListAdapterSlotsItem extends BaseAdapter {

    private final Context context;
    private List<SlotsTools.SlotsGroup> slots;
    @ColorInt
    private int defaultTextColor;

    ListAdapterSlotsItem(Context context, List<SlotsTools.SlotsGroup> slots) {

        this.context = context;
        this.slots = slots;

        // Get the primary text color of the theme
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr = context.obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.textColorPrimary});
        defaultTextColor = arr.getColor(0, -1);
        arr.recycle();
    }

    @Override
    public int getCount() {
        return slots.size();
    }

    @Override
    public SlotsTools.SlotsGroup getItem(int position) {
        return slots.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = LayoutInflater.from(context);

            convertView = vi.inflate(R.layout.list_view_slots_item, parent, false);
            holder.textViewDate = convertView.findViewById(R.id.textViewDate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SlotsTools.SlotsGroup item = getItem(position);

        String date = DateTool.getTimeShort(item.beginAt) + " - " + DateTool.getTimeShort(item.endAt);
        holder.textViewDate.setText(date);
        if (item.scaleTeam != null || item.isBooked)
            holder.textViewDate.setTextColor(ContextCompat.getColor(context, R.color.textColorError));
        else
            holder.textViewDate.setTextColor(defaultTextColor);

        return convertView;
    }

    static class ViewHolder {

        private TextView textViewDate;

    }
}
