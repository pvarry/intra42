package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.SlotsTools;

import java.util.List;

public class RecyclerAdapterSlotsItem extends RecyclerView.Adapter<RecyclerAdapterSlotsItem.ViewHolder> {

    private final Context context;
    private List<SlotsTools.SlotsGroup> slots;
    @ColorInt
    private int textColorDefault;
    @ColorInt
    private int textColorError;
    private OnItemClickListener listener;

    RecyclerAdapterSlotsItem(Context context, List<SlotsTools.SlotsGroup> slots) {

        this.context = context;
        this.slots = slots;

        Resources.Theme theme = context.getTheme();

        // Get the primary text color of the theme
        TypedValue typedValueDefault = new TypedValue();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValueDefault, true);
        TypedArray arrDefault = context.obtainStyledAttributes(typedValueDefault.data, new int[]{android.R.attr.textColorPrimary});
        textColorDefault = arrDefault.getColor(0, -1);
        arrDefault.recycle();

        TypedValue typedValueError = new TypedValue();
        theme.resolveAttribute(R.attr.colorError, typedValueError, true);
        TypedArray arrError = context.obtainStyledAttributes(typedValueError.data, new int[]{R.attr.colorError});
        textColorError = arrError.getColor(0, -1);
        arrError.recycle();
    }

    public SlotsTools.SlotsGroup getItem(int position) {
        return slots.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_slots_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final SlotsTools.SlotsGroup item = getItem(position);

        String date = DateTool.getTimeShort(item.beginAt) + " - " + DateTool.getTimeShort(item.endAt);
        holder.textViewDate.setText(date);
        if (item.scaleTeam != null || item.isBooked)
            holder.textViewDate.setTextColor(textColorError);
        else
            holder.textViewDate.setTextColor(textColorDefault);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClicked(holder.getAdapterPosition(), item);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    interface OnItemClickListener {
        void onItemClicked(int position, SlotsTools.SlotsGroup slots);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewDate;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}
