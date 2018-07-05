package com.paulvarry.intra42.adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.ScaleTeams;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.UserImage;

import java.util.List;

public class RecyclerAdapterScaleTeams extends RecyclerView.Adapter<ViewHolderScaleTeam> {

    private List<ScaleTeams> list;
    private OnItemClickListener listener;

    RecyclerAdapterScaleTeams(List<ScaleTeams> list) {

        this.list = list;
    }

    public ScaleTeams getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public ViewHolderScaleTeam onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_scale_teams, parent, false);
        return new ViewHolderScaleTeam(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderScaleTeam holder, int position) {
        Context context = holder.itemView.getContext();
        final ScaleTeams item = getItem(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClicked(holder.getAdapterPosition(), item);
            }
        });

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
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {

        void onItemClicked(int position, ScaleTeams scaleTeams);
    }
}