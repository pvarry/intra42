package com.paulvarry.intra42.adapters;


import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.ui.TagSpanGenerator;

import java.util.List;

import in.uncod.android.bypass.Bypass;

// usage of Deprecated but is work in progress
@Deprecated
public class BaseRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_LOADING = 2;

    private final Context context;
    private List<RecyclerItemSmall<Events>> eventsList;
    private LayoutInflater inflater;
    private InfiniteScrollListener infiniteScrollListener;
    private Integer maxSize;

    public BaseRecyclerAdapter(Context context, List<RecyclerItemSmall<Events>> projectsList) {

        inflater = LayoutInflater.from(context);
        this.context = context;
        this.eventsList = projectsList;
    }

    static void newItemViewHolder() {
    }

    public void setInfiniteScrollListener(InfiniteScrollListener infiniteScrollListener) {
        this.infiniteScrollListener = infiniteScrollListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (eventsList.size() == position)
            return TYPE_LOADING;
        else if (eventsList.get(position).item != null)
            return TYPE_ITEM;
        else
            return TYPE_HEADER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM)
            return new ViewHolderItem(parent, inflater);
        else if (viewType == TYPE_HEADER)
            return new ViewHolderHeader(parent, inflater);
        else if (viewType == TYPE_LOADING)
            return new ViewHolderLoading(parent, inflater);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderItem)
            ((ViewHolderItem) holder).onBindView(eventsList.get(position).item);
        else if (holder instanceof ViewHolderHeader) {
            ((ViewHolderHeader) holder).onBindView(eventsList.get(position).getName(context));
        } else if (holder instanceof ViewHolderLoading) {
            ViewHolderLoading h = (ViewHolderLoading) holder;
            h.setProgressBarVisibility(infiniteScrollListener.requestMoreItem());
        }
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
        boolean showLoader = infiniteScrollListener == null;
        if (maxSize != null)
            showLoader |= maxSize > eventsList.size();
        return eventsList.size() + ((showLoader) ? 0 : 1);
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public interface InfiniteScrollListener {

        /**
         * @return True when new items will be added
         */
        boolean requestMoreItem();
    }

    public interface CustomViewHolder {
        void onBindView();
    }

    static class ViewHolderLoading extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        ViewHolderLoading(ViewGroup parent, LayoutInflater inflater) {
            super(inflater.inflate(R.layout.list_view_section_loading, parent, false));

            progressBar = itemView.findViewById(R.id.progressBar);
        }

        void setProgressBarVisibility(boolean visibility) {
            if (visibility)
                progressBar.setVisibility(View.VISIBLE);
            else
                progressBar.setVisibility(View.GONE);
        }
    }

    static class ViewHolderItem extends RecyclerView.ViewHolder {

        private TextView textViewName;
        private TextView textViewDescription;
        private TextView textViewTime;
        private TextView textViewPlace;
        private TextView textViewFull;

        public ViewHolderItem(ViewGroup parent, LayoutInflater inflater) {
            super(inflater.inflate(R.layout.list_view_section_item, parent, false));

            ViewGroup group = (ViewGroup) itemView;
            group.removeViewAt(0);
            group.addView(inflater.inflate(R.layout.list_view_event_, parent, false), 0);

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewPlace = itemView.findViewById(R.id.textViewPlace);
            textViewFull = itemView.findViewById(R.id.textViewFull);
        }

        void onBindView(Events item) {

            Context context = itemView.getContext();

            textViewName.setText(item.name);
            TagSpanGenerator span = new TagSpanGenerator(context);
            if (item.kind != null)
                span.addTag(item.kind.getString(context), item.kind.getColorInt(context));
            span.addText(item.name);
            textViewName.setText(span.getString());

            String content = item.description;
            content = content.replace("\r\n\r\n", " ");
            content = content.replace("\n\n", " ");
            content = content.replace("\r\n", " ");
            content = content.replace('\n', ' ');
            textViewDescription.setText(content);

            Bypass b = new Bypass(context);
            String content_tmp = b.markdownToSpannable(item.description).toString().replace('\n', ' ');
            textViewDescription.setText(content_tmp);

            String time;
            time = DateUtils.formatDateRange(context, item.beginAt.getTime(), item.endAt.getTime(), DateUtils.FORMAT_SHOW_TIME);
            if (time.length() > 30)
                time = time.replace(" – ", "\n");
            textViewTime.setText(time);
            textViewPlace.setText(item.location);

            if (item.nbrSubscribers >= item.maxPeople && item.maxPeople > 0) {
                textViewFull.setVisibility(View.VISIBLE);
            } else
                textViewFull.setVisibility(View.GONE);
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {

        TextView textViewHeader;

        public ViewHolderHeader(ViewGroup parent, LayoutInflater inflater) {
            super(inflater.inflate(R.layout.list_view_section_header, parent, false));

            textViewHeader = itemView.findViewById(R.id.textViewName);
        }

        public void onBindView(String title) {
            textViewHeader.setText(title);
        }
    }
}