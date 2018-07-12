package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.IBaseItemSmall;
import com.paulvarry.intra42.api.model.Locations;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.UserImage;
import de.halfbit.pinnedsection.PinnedSectionListView;

import java.util.List;

public class SectionListView extends BaseAdapter
        implements PinnedSectionListView.PinnedSectionListAdapter {

    private Context context;
    private List<Item> list;
    private boolean forceUserPicture;

    public SectionListView(Context context, List<Item> list) {
        this.context = context;
        this.list = list;
    }

    public void forceUserPicture(boolean force) {
        this.forceUserPicture = force;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Item getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null || true) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (vi == null)
                return null;
            convertView = vi.inflate(R.layout.list_view__photo_summary, parent, false);

            holder.linearLayoutTitle = convertView.findViewById(R.id.linearLayoutTitle);
            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);

            holder.layoutContent = convertView.findViewById(R.id.layoutContent);
            holder.imageViewUser = convertView.findViewById(R.id.imageViewUser);
            holder.textViewName = convertView.findViewById(R.id.textViewName);
            holder.textViewSub = convertView.findViewById(R.id.textViewSub);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item i = getItem(position);

        if (isItemViewTypePinned(getItemViewType(position))) {
            holder.layoutContent.setVisibility(View.GONE);
            holder.linearLayoutTitle.setVisibility(View.VISIBLE);

            holder.textViewTitle.setText(i.title);
        } else {
            holder.layoutContent.setVisibility(View.VISIBLE);
            holder.linearLayoutTitle.setVisibility(View.GONE);

            if (i.item instanceof UsersLTE) {
                holder.imageViewUser.setVisibility(View.VISIBLE);
                UserImage.setImage(context, (UsersLTE) i.item, holder.imageViewUser);
            } else if (i.item instanceof Locations && forceUserPicture) {
                holder.imageViewUser.setVisibility(View.VISIBLE);
                UserImage.setImage(context, ((Locations) i.item).user, holder.imageViewUser);
            } else
                holder.imageViewUser.setVisibility(View.GONE);

            if (i.title != null)
                holder.textViewName.setText(i.title);
            else
                holder.textViewName.setText(i.item.getName(context));
            String sub = i.item.getSub(context);
            if (sub != null) {
                holder.textViewSub.setText(sub);
                holder.textViewSub.setVisibility(View.VISIBLE);
            } else
                holder.textViewSub.setVisibility(View.GONE);

            convertView.requestLayout();
        }


        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    // We implement this method to return 'true' for all view types we want to pin
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Item.SECTION;
    }

    private static class ViewHolder {

        private LinearLayout linearLayoutTitle;
        private TextView textViewTitle;

        private ConstraintLayout layoutContent;
        private ImageView imageViewUser;
        private TextView textViewName;
        private TextView textViewSub;

    }

    public static class Item<T extends IBaseItemSmall> {

        public static final int ITEM = 0;
        public static final int SECTION = 1;

        public final int type;
        public final T item;
        public String title;

        public Item(int type, T item, String defaultTitle) {
            this.type = type;
            this.item = item;
            this.title = defaultTitle;
        }

        @Override
        public String toString() {
            return item.getName(null);
        }

    }
}