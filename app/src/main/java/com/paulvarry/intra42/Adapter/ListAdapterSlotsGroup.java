package com.paulvarry.intra42.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.paulvarry.intra42.BottomSheet.BottomSheetSlotsDialogFragment;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.DateTool;
import com.paulvarry.intra42.Tools.SlotsTools;
import com.paulvarry.intra42.activity.home.HomeActivity;
import com.paulvarry.intra42.api.model.Slots;

import java.util.List;

public class ListAdapterSlotsGroup extends BaseAdapter {

    private final HomeActivity activity;
    private SlotsTools slotsTools;
    private List<Slots> slots;

    public ListAdapterSlotsGroup(HomeActivity activity, List<Slots> slots) {

        this.activity = activity;
        this.slots = slots;
        this.slotsTools = new SlotsTools(slots);

    }

    @Override
    public int getCount() {
        return slotsTools.slots.size();
    }

    @Override
    public SlotsTools.SlotsDay getItem(int position) {
        return slotsTools.slots.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_view_slots_group, parent, false);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
            holder.listViewSlots = (ExpandableHeightListView) convertView.findViewById(R.id.listViewSlots);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SlotsTools.SlotsDay item = getItem(position);

        holder.textViewDate.setText(DateTool.getDateLong(item.day));
        ListAdapterSlotsItem adapter = new ListAdapterSlotsItem(activity, item.slots);
        holder.listViewSlots.setAdapter(adapter);
        holder.listViewSlots.setExpanded(true);
        holder.listViewSlots.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int subPosition, long id) {
                BottomSheetSlotsDialogFragment bottomSheetDialogFragment = BottomSheetSlotsDialogFragment.newInstance(item.slots.get(subPosition));
                bottomSheetDialogFragment.show(activity.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                //on dismiss: adapter.notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        slotsTools = new SlotsTools(slots);
        super.notifyDataSetChanged();
    }

    static class ViewHolder {

        private TextView textViewDate;
        private ExpandableHeightListView listViewSlots;

    }
}
