package com.paulvarry.intra42.adapters;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.constraint.Group;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.ScaleTeams;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.UserImage;

import java.util.List;

public class ListAdapterScaleTeams extends BaseAdapter {

    private final Activity context;
    private List<ScaleTeams> list;

    ListAdapterScaleTeams(Activity context, List<ScaleTeams> list) {

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
        if (list != null)
            return list.size();
        return 0;
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public ScaleTeams getItem(int position) {
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

        ScaleTeams item = getItem(position);
        if (item.corrector != null) {
            UserImage.setImage(context, item.corrector, holder.imageViewUser);
            String s = item.corrector.login + " • ";
            if (item.filledAt != null)
                s += DateTool.getDurationAgo(item.filledAt);
            else
                s += DateTool.getDurationAgo(item.beginAt);
            holder.textViewCorrector.setText(s);
        }

        holder.textViewScale.setText(String.valueOf(item.finalMark));
        holder.textViewComment.setText(item.comment);

        if (item.flag.positive) {
            holder.imageViewIconStatus.setImageResource(R.drawable.ic_check_black_24dp);
            holder.imageViewIconStatus.setColorFilter(ContextCompat.getColor(context, R.color.colorTintCheck));
        } else {
            holder.imageViewIconStatus.setImageResource(R.drawable.ic_close_black_24dp);
            holder.imageViewIconStatus.setColorFilter(ContextCompat.getColor(context, R.color.colorTintCross));
        }

        holder.groupFeedback.setVisibility(View.VISIBLE);
        holder.textViewUserFeedback.setVisibility(View.VISIBLE);

        if (true /*(item.feedback == null || item.feedback.isEmpty()) && (item.feedbacks == null || item.feedbacks.size() == 0)*/) {
            holder.groupFeedback.setVisibility(View.GONE);
            holder.textViewUserFeedback.setVisibility(View.GONE);
        } else {

            if (item.feedback != null) {
                holder.textViewFeedback.setText(item.feedback);
            }

            holder.ratingBarFeedback.setRating(item.feedback_rating);

            // re initialise feedback
            holder.textViewFeedbackPunctuality.setVisibility(View.GONE);
            holder.textViewFeedbackRigorous.setVisibility(View.GONE);
            holder.textViewFeedbackInterested.setVisibility(View.GONE);
            holder.textViewFeedbackNice.setVisibility(View.GONE);
            holder.textViewFeedbackNice.setText(null);
            holder.textViewFeedbackRigorous.setText(null);
            holder.textViewFeedbackInterested.setText(null);
            holder.textViewFeedbackPunctuality.setText(null);
            holder.textViewFeedbackNice.setCompoundDrawables(null, null, null, null);
            holder.textViewFeedbackRigorous.setCompoundDrawables(null, null, null, null);
            holder.textViewFeedbackInterested.setCompoundDrawables(null, null, null, null);
            holder.textViewFeedbackPunctuality.setCompoundDrawables(null, null, null, null);
            if (item.feedbacks != null && item.feedbacks.size() != 0) {
                holder.textViewFeedbackPunctuality.setVisibility(View.VISIBLE);
                holder.textViewFeedbackRigorous.setVisibility(View.VISIBLE);
                holder.textViewFeedbackInterested.setVisibility(View.VISIBLE);
                holder.textViewFeedbackNice.setVisibility(View.VISIBLE);
                holder.viewSeparatorFeedback.setVisibility(View.VISIBLE);

                Drawable drawableInterested = context.getResources().getDrawable(R.drawable.ic_chat_bubble_outline_black_24dp);
                Drawable drawableNice = context.getResources().getDrawable(R.drawable.ic_nice_black_24px);
                Drawable drawablePunctuality = context.getResources().getDrawable(R.drawable.ic_timer_black_24px);
                Drawable drawableRigorous = context.getResources().getDrawable(R.drawable.ic_build_black_24dp);
                drawableInterested.setBounds(0, 0, 42, 42);
                drawableNice.setBounds(0, 0, 42, 42);
                drawablePunctuality.setBounds(0, 0, 42, 42);
                drawableRigorous.setBounds(0, 0, 42, 42);
                holder.textViewFeedbackInterested.setCompoundDrawables(drawableInterested, null, null, null);
                holder.textViewFeedbackNice.setCompoundDrawables(drawableNice, null, null, null);
                holder.textViewFeedbackPunctuality.setCompoundDrawables(drawablePunctuality, null, null, null);
                holder.textViewFeedbackRigorous.setCompoundDrawables(drawableRigorous, null, null, null);

                String str;
                for (ScaleTeams.Feedback f : item.feedbacks) {
                    str = String.valueOf(f.rate) + "/4";
                    switch (f.kind) {
                        case NICE:
                            holder.textViewFeedbackNice.setText(str);
                            break;
                        case RIGOROUS:
                            holder.textViewFeedbackRigorous.setText(str);
                            break;
                        case INTERESTED:
                            holder.textViewFeedbackInterested.setText(str);
                            break;
                        case PUNCTUALITY:
                            holder.textViewFeedbackPunctuality.setText(str);
                            break;
                        default:
                            break;
                    }
                }
            }
        }

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