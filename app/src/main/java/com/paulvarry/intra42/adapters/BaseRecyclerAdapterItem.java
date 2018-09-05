package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.IBaseItemSmall;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseRecyclerAdapterItem<T extends IBaseItemSmall> extends RecyclerView.Adapter<BaseRecyclerAdapterItem.ViewHolderItem> {

    private List<T> items;

    private OnItemClickListener listener;

    public BaseRecyclerAdapterItem(List<T> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view__summary, parent, false);
        return new ViewHolderItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem holder, final int position) {
        final T item = items.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClicked(position, item);
            }
        });

        Context context = holder.itemView.getContext();

        String name = item.getName(context);
        if (name != null && !name.isEmpty()) {
            holder.textViewTitle.setVisibility(View.VISIBLE);
            holder.textViewTitle.setText(name);
        } else
            holder.textViewTitle.setVisibility(View.GONE);

        String summary = item.getSub(context);
        if (summary != null && !summary.isEmpty()) {
            holder.textViewSummary.setVisibility(View.VISIBLE);
            holder.textViewSummary.setText(summary);
        } else
            holder.textViewSummary.setVisibility(View.GONE);
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
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener<T> {
        /**
         * Callback method to be invoked when an item in this Recycler has been clicked.
         *
         * @param position Position of clicked item.
         * @param item     Item clicked.
         */
        void onItemClicked(int position, T item);
    }

    static class ViewHolderItem extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewSummary;

        ViewHolderItem(View view) {
            super(view);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewSummary = itemView.findViewById(R.id.textViewSummary);
        }
    }
}