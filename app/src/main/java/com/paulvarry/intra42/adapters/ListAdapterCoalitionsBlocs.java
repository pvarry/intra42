package com.paulvarry.intra42.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Coalitions;
import com.paulvarry.intra42.utils.mImage;

import java.util.List;

public class ListAdapterCoalitionsBlocs extends BaseAdapter {

    private final Activity activity;
    private List<Coalitions> itemList;

    public ListAdapterCoalitionsBlocs(Activity activity, List<Coalitions> items) {
        this.activity = activity;
        this.itemList = items;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        if (itemList == null)
            return 0;
        return itemList.size();
    }

    /**
     * Get the data BaseItem associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Coalitions getItem(int position) {
        return itemList.get(position);
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

            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (vi == null)
                return null;
            convertView = vi.inflate(R.layout.list_view_coalitions_blocs, parent, false);

            holder.frameLayoutBanner = convertView.findViewById(R.id.frameLayoutBanner);
            holder.imageViewBanner = convertView.findViewById(R.id.imageViewBanner);
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.textViewCoalitions = convertView.findViewById(R.id.textViewCoalitions);
            holder.textViewScore = convertView.findViewById(R.id.textViewScore);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Coalitions item = getItem(position);

        holder.textViewCoalitions.setText(item.name);
        holder.textViewScore.setText(String.valueOf(item.score));
        holder.imageView.setVisibility(View.VISIBLE);
        switch (item.slug) {
            case "the-federation":
                holder.imageView.setImageResource(R.drawable.federation_background);
                break;
            case "the-alliance":
                holder.imageView.setImageResource(R.drawable.alliance_background);
                break;
            case "the-assembly":
                holder.imageView.setImageResource(R.drawable.assembly_background);
                break;
            case "the-order":
                holder.imageView.setImageResource(R.drawable.order_background);
                break;
            default:
                holder.imageView.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.frameLayoutBanner.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(item.color)));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = mImage.loadImageSVG(item.imageUrl);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.imageViewBanner.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();

        return convertView;
    }

    private static class ViewHolder {

        private FrameLayout frameLayoutBanner;
        private ImageView imageViewBanner;
        private ImageView imageView;
        private TextView textViewCoalitions;
        private TextView textViewScore;

    }
}