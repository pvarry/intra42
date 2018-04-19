package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.IBaseItem;

import java.util.List;

public class BaseHeaderRecyclerAdapter<T extends IBaseItem> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Item<T>> items;

    public BaseHeaderRecyclerAdapter(Context context, List<Item<T>> items) {
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
            view = inflater.inflate(R.layout.spinner_basic_simple_text, parent, false);
            return new ViewHolderHeader(view);
        } else {
            view = inflater.inflate(R.layout.list_view__summary, parent, false);
            return new ViewHolderItem(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        Item<T> i = items.get(position);
        switch (getItemViewType(position)) {
            case Item.HEADER:
                ViewHolderHeader header = (ViewHolderHeader) holder;
                header.textViewHeader.setText(i.getName(context));
                header.textViewHeader.setTextColor(Color.RED);
                break;

            case Item.ITEM:
                ViewHolderItem item = (ViewHolderItem) holder;
                item.textViewTitle.setText(i.getName(context));
                item.textViewSummary.setText(i.getSub(context));
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
            return item.getName(null);
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

    public class ViewHolderHeader extends RecyclerView.ViewHolder {

        private TextView textViewHeader;

        ViewHolderHeader(View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.textViewName);
        }

    }

    class ViewHolderItem extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        TextView textViewSummary;

        ViewHolderItem(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewSummary = itemView.findViewById(R.id.textViewSummary);
        }
    }

}
