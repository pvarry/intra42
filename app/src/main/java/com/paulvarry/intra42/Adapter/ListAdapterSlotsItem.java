package com.paulvarry.intra42.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.DateTool;
import com.paulvarry.intra42.Tools.SlotsTools;

import java.util.List;

public class ListAdapterSlotsItem extends BaseAdapter {

    private final Context activity;
    private List<SlotsTools.SlotsGroup> slots;

    public ListAdapterSlotsItem(Context context, List<SlotsTools.SlotsGroup> slots) {

        this.activity = context;
        this.slots = slots;
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

            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_view_slots_item, parent, false);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SlotsTools.SlotsGroup item = getItem(position);

        String date = DateTool.getTimeShort(item.beginAt) + " - " + DateTool.getTimeShort(item.endAt);
        holder.textViewDate.setText(date);
        if (item.scaleTeam == null && item.isBooked)
            holder.textViewDate.setTextColor(ContextCompat.getColor(activity, R.color.colorFail));
        else
            holder.textViewDate.setTextColor(ContextCompat.getColor(activity, R.color.colorGrayDark));

        return convertView;
    }

    static class ViewHolder {

        private TextView textViewDate;

    }
}
