package com.paulvarry.intra42.adapters;

import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.paulvarry.intra42.R;

public class ViewHolderScaleTeam extends RecyclerView.ViewHolder {

    public ImageView imageViewUser;
    public TextView textViewCorrector;
    public TextView textViewScale;
    public TextView textViewComment;
    public ImageView imageViewIconStatus;
    public TextView textViewUserFeedback;
    public Group groupFeedback;
    public TextView textViewFeedbackInterested;
    public TextView textViewFeedbackNice;
    public TextView textViewFeedbackPunctuality;
    public TextView textViewFeedbackRigorous;
    public TextView textViewFeedback;
    public RatingBar ratingBarFeedback;
    public View viewSeparatorFeedback;

    public ViewHolderScaleTeam(View itemView) {
        super(itemView);

        imageViewUser = itemView.findViewById(R.id.imageView);
        textViewCorrector = itemView.findViewById(R.id.textViewCorrector);
        textViewScale = itemView.findViewById(R.id.textViewScale);
        textViewComment = itemView.findViewById(R.id.textViewComment);
        imageViewIconStatus = itemView.findViewById(R.id.imageViewIconStatus);
        textViewFeedbackInterested = itemView.findViewById(R.id.textViewFeedbackInterested);
        textViewFeedbackNice = itemView.findViewById(R.id.textViewFeedbackNice);
        textViewFeedbackPunctuality = itemView.findViewById(R.id.textViewFeedbackPunctuality);
        textViewFeedbackRigorous = itemView.findViewById(R.id.textViewFeedbackRigorous);
        textViewFeedback = itemView.findViewById(R.id.textViewFeedback);
        ratingBarFeedback = itemView.findViewById(R.id.ratingBarFeedback);
        groupFeedback = itemView.findViewById(R.id.groupFeedback);
        textViewUserFeedback = itemView.findViewById(R.id.textViewUserFeedback);
        viewSeparatorFeedback = itemView.findViewById(R.id.viewSeparatorFeedback);
    }
}
