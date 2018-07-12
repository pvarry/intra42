package com.paulvarry.intra42.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.TeamsUploads;
import com.paulvarry.intra42.utils.UserImage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

public class RecyclerAdapterScaleTeamsAutomatic extends RecyclerView.Adapter<ViewHolderScaleTeam> {

    private List<TeamsUploads> list;

    public RecyclerAdapterScaleTeamsAutomatic(List<TeamsUploads> list) {
        this.list = list;
    }

    public TeamsUploads getItem(int position) {
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
}