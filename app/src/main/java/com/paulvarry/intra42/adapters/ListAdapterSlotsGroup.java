package com.paulvarry.intra42.adapters;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.home.HomeSlotsFragment;
import com.paulvarry.intra42.api.model.Slots;
import com.paulvarry.intra42.bottomSheet.BottomSheetSlotsDialogFragment;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.SlotsTools;

import java.util.List;

public class ListAdapterSlotsGroup extends BaseAdapter {

    private final HomeSlotsFragment fragment;
    private SlotsTools slotsTools;
    private List<Slots> slots;

    public ListAdapterSlotsGroup(HomeSlotsFragment fragment, List<Slots> slots) {

        this.fragment = fragment;
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

            LayoutInflater vi = LayoutInflater.from(parent.getContext());

            convertView = vi.inflate(R.layout.list_view_slots_group, parent, false);
            holder.textViewDate = convertView.findViewById(R.id.textViewDate);
            holder.recyclerViewSlots = convertView.findViewById(R.id.recyclerViewSlots);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final SlotsTools.SlotsDay item = getItem(position);

        holder.textViewDate.setText(DateTool.getDateLong(item.day));
        RecyclerAdapterSlotsItem adapter = new RecyclerAdapterSlotsItem(parent.getContext(), item.slots);
        holder.recyclerViewSlots.setAdapter(adapter);
        holder.recyclerViewSlots.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        holder.recyclerViewSlots.addItemDecoration(new DividerItemDecoration(parent.getContext(), DividerItemDecoration.VERTICAL));
        holder.recyclerViewSlots.setNestedScrollingEnabled(false);
        adapter.setOnItemClickListener(new RecyclerAdapterSlotsItem.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, SlotsTools.SlotsGroup slots) {
                FragmentActivity activity = fragment.getActivity();
                if (activity == null)
                    return;
                BottomSheetSlotsDialogFragment bottomSheetDialogFragment = BottomSheetSlotsDialogFragment.newInstance(item.slots.get(position));
                bottomSheetDialogFragment.show(activity.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                bottomSheetDialogFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        fragment.onRefresh();
                    }
                });
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
        private RecyclerView recyclerViewSlots;

    }
}
