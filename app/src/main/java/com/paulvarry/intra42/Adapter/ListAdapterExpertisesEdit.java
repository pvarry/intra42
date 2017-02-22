package com.paulvarry.intra42.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ExpertisesUsers;

import java.util.List;

public class ListAdapterExpertisesEdit extends BaseAdapter {

    Context context;
    private List<ExpertisesUsers> expertisesUsersList;

    public ListAdapterExpertisesEdit(Context context, List<ExpertisesUsers> expertisesUsersList) {
        this.expertisesUsersList = expertisesUsersList;
        this.context = context;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return expertisesUsersList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public ExpertisesUsers getItem(int position) {
        return expertisesUsersList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return expertisesUsersList.get(position).id;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.list_view_expertises_edit, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            holder.checkboxInterested = (CheckBox) convertView.findViewById(R.id.checkboxInterested);
            holder.imageButtonEdit = (ImageButton) convertView.findViewById(R.id.imageButtonEdit);
            holder.imageButtonRemove = (ImageButton) convertView.findViewById(R.id.imageButtonRemove);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ExpertisesUsers item = getItem(position);

        if (item.expertise != null)
            holder.textView.setText(item.expertise.name);
        holder.checkboxInterested.setChecked(item.interested);
        holder.ratingBar.setRating(item.value);

        holder.imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    static class ViewHolder {
        private TextView textView;
        private RatingBar ratingBar;
        private CheckBox checkboxInterested;
        private ImageButton imageButtonEdit;
        private ImageButton imageButtonRemove;
    }
}
