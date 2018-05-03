package com.paulvarry.intra42.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.TeamsUploads;
import com.paulvarry.intra42.utils.UserImage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

public class RecyclerAdapterScaleTeamsAutomatic extends RecyclerView.Adapter<RecyclerAdapterScaleTeamsAutomatic.ViewHolder> {

    private List<TeamsUploads> list;

    RecyclerAdapterScaleTeamsAutomatic(List<TeamsUploads> list) {
        this.list = list;
    }

    public TeamsUploads getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_scale_teams, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        TeamsUploads item = getItem(position);

        Picasso picasso = Picasso.with(holder.itemView.getContext());
        RequestCreator requestCreator;
        String url = UserImage.BASE_URL + "moulinette.jpg";
        requestCreator = picasso.load(url).resize(200, 240);
        requestCreator.into(holder.imageViewUser);

        holder.textViewCorrector.setText(R.string.project_moulinette);
        holder.textViewScale.setText(String.valueOf(item.finalMark));
        holder.textViewComment.setText(item.comment);
        holder.groupFeedback.setVisibility(View.GONE);
        holder.textViewUserFeedback.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

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

        public ViewHolder(View itemView) {
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
}