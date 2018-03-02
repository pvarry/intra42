package com.paulvarry.intra42.adapters;

import android.app.Activity;
import android.support.constraint.Group;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.TeamsUploads;
import com.paulvarry.intra42.utils.UserImage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

public class ListAdapterScaleTeamsAutomatic extends BaseAdapter {

    private final Activity context;
    private List<TeamsUploads> list;

    ListAdapterScaleTeamsAutomatic(Activity context, List<TeamsUploads> list) {

        this.context = context;
        this.list = list;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public TeamsUploads getItem(int position) {
        return list.get(position);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = LayoutInflater.from(context);
            convertView = vi.inflate(R.layout.list_view_scale_teams, parent, false);

            holder.imageViewUser = convertView.findViewById(R.id.imageView);
            holder.textViewCorrector = convertView.findViewById(R.id.textViewCorrector);
            holder.textViewScale = convertView.findViewById(R.id.textViewScale);
            holder.textViewComment = convertView.findViewById(R.id.textViewComment);
            holder.imageViewIconStatus = convertView.findViewById(R.id.imageViewIconStatus);
            holder.textViewFeedbackInterested = convertView.findViewById(R.id.textViewFeedbackInterested);
            holder.textViewFeedbackNice = convertView.findViewById(R.id.textViewFeedbackNice);
            holder.textViewFeedbackPunctuality = convertView.findViewById(R.id.textViewFeedbackPunctuality);
            holder.textViewFeedbackRigorous = convertView.findViewById(R.id.textViewFeedbackRigorous);
            holder.textViewFeedback = convertView.findViewById(R.id.textViewFeedback);
            holder.ratingBarFeedback = convertView.findViewById(R.id.ratingBarFeedback);
            holder.groupFeedback = convertView.findViewById(R.id.groupFeedback);
            holder.textViewUserFeedback = convertView.findViewById(R.id.textViewUserFeedback);
            holder.viewSeparatorFeedback = convertView.findViewById(R.id.viewSeparatorFeedback);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TeamsUploads item = getItem(position);

        Picasso picasso = Picasso.with(context);
        RequestCreator requestCreator;
        String url = UserImage.BASE_URL + "moulinette.jpg";
        requestCreator = picasso.load(url).resize(200, 240);
        requestCreator.into(holder.imageViewUser);

        holder.textViewCorrector.setText(R.string.project_moulinette);
        holder.textViewScale.setText(String.valueOf(item.finalMark));
        holder.textViewComment.setText(item.comment);
        holder.groupFeedback.setVisibility(View.GONE);
        holder.textViewUserFeedback.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder {

        private ImageView imageViewUser;
        private TextView textViewCorrector;
        private TextView textViewScale;
        private TextView textViewComment;
        private ImageView imageViewIconStatus;
        private TextView textViewUserFeedback;
        private Group groupFeedback;
        private TextView textViewFeedbackInterested;
        private TextView textViewFeedbackNice;
        private TextView textViewFeedbackPunctuality;
        private TextView textViewFeedbackRigorous;
        private TextView textViewFeedback;
        private RatingBar ratingBarFeedback;
        private View viewSeparatorFeedback;
    }
}
