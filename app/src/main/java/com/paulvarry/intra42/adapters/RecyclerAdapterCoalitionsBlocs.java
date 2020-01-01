package com.paulvarry.intra42.adapters;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Coalitions;
import com.paulvarry.intra42.utils.mImage;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapterCoalitionsBlocs extends RecyclerView.Adapter<RecyclerAdapterCoalitionsBlocs.ViewHolder> {

    private final Activity activity;
    private List<Coalitions> itemList;

    public RecyclerAdapterCoalitionsBlocs(Activity activity, List<Coalitions> items) {
        this.activity = activity;
        this.itemList = items;
    }

    public Coalitions getItem(int position) {
        return itemList.get(position);
    }

    @NonNull
    @Override
    public RecyclerAdapterCoalitionsBlocs.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_coalitions_blocs, parent, false);
        return new RecyclerAdapterCoalitionsBlocs.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterCoalitionsBlocs.ViewHolder holder, int position) {
        final Coalitions item = getItem(position);

        holder.textViewCoalitions.setText(item.name);

        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        holder.textViewScore.setText(numberFormat.format(item.score));

        mImage.setPicasso(Uri.parse(item.coverUrl), holder.imageViewBackground, 0);

        holder.frameLayoutBanner.setBackgroundResource(R.drawable.banner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.frameLayoutBanner.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(item.color)));
        }

        new Thread(() -> {
            final Bitmap bitmap = mImage.loadImageSVG(item.imageUrl);
            activity.runOnUiThread(() -> holder.imageViewBanner.setImageBitmap(bitmap));
        }).start();
    }

    @Override
    public int getItemCount() {
        if (itemList == null)
            return 0;
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout frameLayoutBanner;
        private ImageView imageViewBanner;
        private ImageView imageViewBackground;
        private TextView textViewCoalitions;
        private TextView textViewScore;

        public ViewHolder(View itemView) {
            super(itemView);

            frameLayoutBanner = itemView.findViewById(R.id.frameLayoutBanner);
            imageViewBanner = itemView.findViewById(R.id.imageViewBanner);
            imageViewBackground = itemView.findViewById(R.id.imageViewBackground);
            textViewCoalitions = itemView.findViewById(R.id.textViewCoalitions);
            textViewScore = itemView.findViewById(R.id.textViewScore);
        }

    }


}