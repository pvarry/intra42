package com.paulvarry.intra42.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.ui.TagSpanGenerator;
import com.paulvarry.intra42.utils.DateTool;

import java.util.List;

import in.uncod.android.bypass.Bypass;

public class ListAdapterEvents extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private final Context context;
    private List<Events> eventsList;
    private static final int TYPE_LOADING = 1;
    private LayoutInflater inflater;
    private InfiniteScrollListener infiniteScrollListener;

    public ListAdapterEvents(Context context, List<Events> projectsList) {

        inflater = LayoutInflater.from(context);
        this.context = context;
        this.eventsList = projectsList;
    }

    public void setInfiniteScrollListener(InfiniteScrollListener infiniteScrollListener) {
        this.infiniteScrollListener = infiniteScrollListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (eventsList.size() == position)
            return TYPE_LOADING;
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM)
            return new ViewHolderItem(parent, inflater);
        else if (viewType == TYPE_LOADING)
            return new ViewHolderLoading(parent, inflater);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolderItem)
            ((ViewHolderItem) holder).onBindView(eventsList.get(position));
        else if (holder instanceof ViewHolderLoading) {
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
        return eventsList.size() + ((infiniteScrollListener == null) ? 0 : 1);
    }

    public interface InfiniteScrollListener {

        /**
         * @return True when new items will be added
         */
        boolean requestMoreItem();
    }

    static class ViewHolderItem extends RecyclerView.ViewHolder {

        private TextView textViewDateDay;
        private TextView textViewDateMonth;
        private TextView textViewName;
        private TextView textViewDescription;
        private TextView textViewTime;
        private TextView textViewPlace;
        private TextView textViewFull;

        public ViewHolderItem(ViewGroup parent, LayoutInflater inflater) {
            super(inflater.inflate(R.layout.list_view_event, parent, false));

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            textViewDateDay = itemView.findViewById(R.id.textViewDateDay);
            textViewDateMonth = itemView.findViewById(R.id.textViewDateMonth);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewPlace = itemView.findViewById(R.id.textViewPlace);
            textViewFull = itemView.findViewById(R.id.textViewFull);
        }

        void onBindView(Events item) {

            Context context = itemView.getContext();

            textViewDateDay.setText(DateTool.getDay(item.beginAt));
            textViewDateMonth.setText(DateTool.getMonthMedium(item.beginAt));
            textViewName.setText(item.name);
            TagSpanGenerator span = new TagSpanGenerator.Builder(context).setTextSize(textViewName.getTextSize()).build();
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

    public class ViewHolderHeader extends RecyclerView.ViewHolder {

        private TextView textViewHeader;

        ViewHolderHeader(View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.textViewName);
        }

    }
}