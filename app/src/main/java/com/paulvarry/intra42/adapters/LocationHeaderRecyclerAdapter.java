package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.utils.UserImage;

import java.util.List;

public class LocationHeaderRecyclerAdapter extends SimpleHeaderRecyclerAdapter<Locations> {

    private boolean isUserHistory = false;

    public LocationHeaderRecyclerAdapter(Context context, List<RecyclerItemSmall<Locations>> items) {
        super(context, items);
    }

    public void setUserHistory(boolean userHistory) {
        isUserHistory = userHistory;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == RecyclerItemSmall.HEADER) {
            view = inflater.inflate(R.layout.list_view_section_header, parent, false);
            return new ViewHolderHeader(view);
        } else {
            return new ViewHolderItem(inflater, parent);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        RecyclerItemSmall<Locations> i = items.get(position);
        switch (getItemViewType(position)) {
            case RecyclerItemSmall.ITEM:
                ViewHolderItem item = (ViewHolderItem) holder;
                item.bind(i, position);
                break;
            default:
                super.onBindViewHolder(holder, position);
        }
    }

    // this should be static
    class ViewHolderItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerItemSmall<Locations> location;

        private ImageView imageView;
        private TextView textViewTitle;
        private TextView textViewSummary;
        private View divider;

        ViewHolderItem(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_view_section_item_photo, parent, false));

            itemView.findViewById(R.id.layoutContent).setOnClickListener(this);

            imageView = itemView.findViewById(R.id.imageView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewSummary = itemView.findViewById(R.id.textViewSummary);
            divider = itemView.findViewById(R.id.divider);
        }

        void bind(RecyclerItemSmall<Locations> data, int position) {
            location = data;

            if (isUserHistory) {
                imageView.setVisibility(View.GONE);
                textViewTitle.setText(data.item.host);
            } else {
                textViewTitle.setText(data.getName(context));
                UserImage.setImageSmall(context, data.item.user, imageView);
                imageView.setVisibility(View.VISIBLE);
            }
            textViewSummary.setText(data.getSub(context));

            if (items.size() > position + 1) {
                RecyclerItemSmall<Locations> next = items.get(position + 1);
                if (next.type == RecyclerItemSmall.HEADER)
                    divider.setVisibility(View.GONE);
                else
                    divider.setVisibility(View.VISIBLE);
            }
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (LocationHeaderRecyclerAdapter.super.listener != null)
                LocationHeaderRecyclerAdapter.super.listener.onItemClick(location);
        }
    }

}
