package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.IBaseItem;

import java.util.List;

/**
 * @param <T> Data type used to fill list
 */
public class SimpleHeaderRecyclerAdapter<T extends IBaseItem> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected Context context;
    protected LayoutInflater inflater;
    protected List<RecyclerItem<T>> items;

    protected OnItemClickListener<T> listener;

    public SimpleHeaderRecyclerAdapter(Context context, List<RecyclerItem<T>> items) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<RecyclerItem<T>> getItems() {
        return items;
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == RecyclerItem.HEADER) {
            view = inflater.inflate(R.layout.list_view_section_header, parent, false);
            return new ViewHolderHeader(view);
        } else {
            return new ViewHolderItem(inflater, parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        RecyclerItem<T> i = items.get(position);
        switch (getItemViewType(position)) {
            case RecyclerItem.HEADER:
                ViewHolderHeader header = (ViewHolderHeader) holder;
                header.bind(i, position);
                break;
            case RecyclerItem.ITEM:
                ViewHolderItem item = (ViewHolderItem) holder;
                item.bind(i, position);
                break;
        }
    }

    public interface OnItemClickListener<C extends IBaseItem> {

        void onItemClick(RecyclerItem<C> item);
    }

    // this should be static
    class ViewHolderHeader extends RecyclerView.ViewHolder {

        private TextView textViewHeader;

        ViewHolderHeader(View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.textViewName);
        }

        void bind(RecyclerItem<T> data, int position) {
            textViewHeader.setText(data.getName(context));
        }
    }

    // this should be static
    class ViewHolderItem extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewSummary;
        private View divider;
        private RecyclerItem<T> item;

        ViewHolderItem(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_view_section_item, parent, false));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(item);
                }
            });

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewSummary = itemView.findViewById(R.id.textViewSummary);
            divider = itemView.findViewById(R.id.divider);
        }

        void bind(RecyclerItem<T> data, int position) {
            item = data;

            textViewTitle.setText(data.getName(context));
            textViewSummary.setText(data.getSub(context));
            if (items.size() > position + 1) {
                RecyclerItem<T> next = items.get(position + 1);
                if (next.type == RecyclerItem.HEADER)
                    divider.setVisibility(View.GONE);
                else
                    divider.setVisibility(View.VISIBLE);
            }
        }
    }
}
