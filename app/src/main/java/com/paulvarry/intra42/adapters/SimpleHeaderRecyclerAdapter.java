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

    private Context context;
    private LayoutInflater inflater;
    private List<Item<T>> items;

    public SimpleHeaderRecyclerAdapter(Context context, List<Item<T>> items) {
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

    public List<Item<T>> getItems() {
        return items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == Item.HEADER) {
            view = inflater.inflate(R.layout.list_view_section_header, parent, false);
            return new ViewHolderHeader(view);
        } else {
            return new ViewHolderItem(inflater, parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        Item<T> i = items.get(position);
        switch (getItemViewType(position)) {
            case Item.HEADER:
                ViewHolderHeader header = (ViewHolderHeader) holder;
                header.bind(i, position);
                break;

            case Item.ITEM:
                ViewHolderItem item = (ViewHolderItem) holder;
                item.bind(i, position);
                break;
        }
    }

    static public class Item<U extends IBaseItem> implements IBaseItem {

        public static final int HEADER = 0;
        public static final int ITEM = 1;

        public final int type;
        public final U item;
        public final String title;

        public Item(U item) {
            this.type = ITEM;
            this.item = item;
            title = null;
        }

        public Item(String header) {
            this.type = HEADER;
            this.item = null;
            title = header;
        }

        @Override
        public String toString() {
            if (item != null)
                return item.getName(null);
            return title;
        }

        @Override
        public String getName(Context context) {
            if (item != null)
                return item.getName(context);
            return title;
        }

        @Override
        public String getSub(Context context) {
            if (item != null)
                return item.getSub(context);
            return title;
        }

        @Override
        public boolean openIt(Context context) {
            return false;
        }
    }

    // this should be static
    class ViewHolderHeader extends RecyclerView.ViewHolder {

        private TextView textViewHeader;

        ViewHolderHeader(View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.textViewName);
        }

        void bind(Item<T> data, int position) {
            textViewHeader.setText(data.getName(context));
        }
    }

    // this should be static
    class ViewHolderItem extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewSummary;
        private View divider;

        ViewHolderItem(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_view_section_item, parent, false));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewSummary = itemView.findViewById(R.id.textViewSummary);
            divider = itemView.findViewById(R.id.divider);
        }

        void bind(Item<T> data, int position) {
            textViewTitle.setText(data.getName(context));
            textViewSummary.setText(data.getSub(context));
            if (items.size() > position + 1) {
                Item<T> next = items.get(position + 1);
                if (next.type == Item.HEADER)
                    divider.setVisibility(View.GONE);
                else
                    divider.setVisibility(View.VISIBLE);
            }
        }
    }

}
