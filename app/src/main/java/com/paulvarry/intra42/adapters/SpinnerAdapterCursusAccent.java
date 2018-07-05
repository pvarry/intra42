package com.paulvarry.intra42.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.CursusUsers;

import java.util.List;

public class SpinnerAdapterCursusAccent extends BaseAdapter {

    private final Activity context;
    private List<CursusUsers> cursus;

    public SpinnerAdapterCursusAccent(Activity context, List<CursusUsers> cursus) {

        this.context = context;
        this.cursus = cursus;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return cursus.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public CursusUsers getItem(int position) {
        return cursus.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return cursus.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (context == null)
            return null;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = LayoutInflater.from(context);
            convertView = vi.inflate(R.layout.spinner_cursus_accent, parent, false);

            holder.textViewName = convertView.findViewById(R.id.textViewName);
            holder.textViewLvl = convertView.findViewById(R.id.textViewLvl);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CursusUsers item = getItem(position);

        holder.textViewName.setText(item.cursus.name);
        holder.textViewLvl.setText(String.valueOf(item.level));

        return convertView;
    }

    private static class ViewHolder {
        private TextView textViewName;
        private TextView textViewLvl;
    }
}
