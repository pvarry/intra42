package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.paulvarry.intra42.BuildConfig;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.ui.BasicThreadActivity;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends BasicThreadActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setContentView(R.layout.activity_about);
        super.activeHamburger();
        super.onCreate(savedInstanceState);
        navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setChecked(true);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public String getToolbarName() {
        return null;
    }

    @Override
    public void setViewContent() {
        ListView listView = findViewById(R.id.listView);

        List<Item> list = new ArrayList<>();
        Item item;

        item = new Item(getString(R.string.about_content), null);
        list.add(item);

        String str = BuildConfig.VERSION_NAME + " (" + String.valueOf(BuildConfig.VERSION_CODE) + ")";
        item = new Item(str, getString(R.string.about_version));
        list.add(item);

        item = new Item(getString(R.string.about_view_source), getString(R.string.about_on_github));
        list.add(item);

        item = new Item(getString(R.string.about_report_bug), getString(R.string.about_on_github));
        list.add(item);

        item = new Item(getString(R.string.about_library_list), null);
        list.add(item);

        item = new Item(getString(R.string.about_poeditor), getString(R.string.home_help_POEditor));
        list.add(item);

        Adapter adapter = new Adapter(list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 2) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.Github_link)));
                    startActivity(browserIntent);
                } else if (i == 3) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.Github_link_issues)));
                    startActivity(browserIntent);
                } else if (i == 4) {
                    Intent intent = new Intent(AboutActivity.this, LibraryListActivity.class);
                    startActivity(intent);
                } else if (i == 5) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.POEditor_link)));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    private class Item {

        public String name;
        public String content;

        Item(String name, String content) {
            this.name = name;
            this.content = content;
        }
    }

    class Adapter extends BaseAdapter {

        List<Item> list;

        Adapter(List<Item> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Item getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;

            if (view == null) {
                holder = new ViewHolder();

                LayoutInflater vi = (LayoutInflater) AboutActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = vi.inflate(R.layout.list_view_about, viewGroup, false);
                holder.textViewName = view.findViewById(R.id.textViewName);
                holder.textViewSub = view.findViewById(R.id.textViewSub);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Item item = getItem(i);

            holder.textViewName.setText(item.name);

            if (item.content != null) {
                holder.textViewSub.setText(item.content);
                holder.textViewSub.setVisibility(View.VISIBLE);
            } else
                holder.textViewSub.setVisibility(View.GONE);
            return view;
        }

        private class ViewHolder {

            private TextView textViewName;
            private TextView textViewSub;

        }
    }
}
