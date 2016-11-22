package com.paulvarry.intra42.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.paulvarry.intra42.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LibraryListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ListView listView = (ListView) findViewById(R.id.listView);

        final HashMap<String, Item> list = new HashMap<>();
        Item item;

        item = new Item("gson", getString(R.string.license_gson), "https://github.com/google/gson");
        list.put(item.name, item);

        item = new Item("ExpandableHeightListView", getString(R.string.license_ExpandableHeightListView), "https://github.com/PaoloRotolo/ExpandableHeightListView");
        list.put(item.name, item);

        item = new Item("prettytime", getString(R.string.license_ExpandableHeightListView), "https://github.com/ocpsoft/prettytime");
        list.put(item.name, item);

        item = new Item("svg-android", "LGPL-3.0", "https://libraries.io/maven/org.mapsforge:svg-android/0.4.3");
        list.put(item.name, item);

        item = new Item("tagview", getString(R.string.license_tagview), "https://github.com/VEINHORN/android-tagview");
        list.put(item.name, item);

        item = new Item("bypasses", getString(R.string.license_bypasses), "https://github.com/Commit451/bypasses");
        list.put(item.name, item);

        item = new Item("Picasso", getString(R.string.license_picasso), "https://github.com/square/picasso");
        list.put(item.name, item);

        item = new Item("androidsvg", "Apache License 2.0", "https://code.google.com/archive/p/androidsvg/");
        list.put(item.name, item);

        item = new Item("multiline-collapsingtoolbar", "Apache License 2.0", "https://github.com/opacapp/multiline-collapsingtoolbar");
        list.put(item.name, item);

        item = new Item("ChipView", getString(R.string.license_chipView), "https://github.com/Plumillon/ChipView");
        list.put(item.name, item);

        item = new Item("pinned-section-listview", getString(R.string.license_pinned_section_listview), "https://github.com/beworker/pinned-section-listview");
        list.put(item.name, item);

        item = new Item("materialish-progress", getString(R.string.license_materialish_progress), "https://github.com/pnikosis/materialish-progress");
        list.put(item.name, item);

        item = new Item("Picasso Transformations", getString(R.string.license_picasso_transformations), "https://github.com/wasabeef/picasso-transformations");
        list.put(item.name, item);

        final Adapter adapter = new Adapter(list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url = adapter.getItem(i).url;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class Item {

        public String name;
        public String content;
        public String url;

        Item(String name, String content, String url) {
            this.name = name;
            this.content = content;
            this.url = url;
        }
    }

    class Adapter extends BaseAdapter {

        List<Item> items;

        Adapter(HashMap<String, Item> map) {
            items = new ArrayList<>();

            Set<String> set = map.keySet();
            for (String i : set) {
                items.add(map.get(i));
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Item getItem(int i) {
            return items.get(i);
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

                LayoutInflater vi = (LayoutInflater) LibraryListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = vi.inflate(R.layout.list_view_library, viewGroup, false);
                holder.textViewName = (TextView) view.findViewById(R.id.textViewName);
                holder.textViewLicense = (TextView) view.findViewById(R.id.textViewLicense);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final Item item = getItem(i);

            holder.textViewName.setText(item.name);
            holder.textViewLicense.setText(item.content);

            return view;
        }

        private class ViewHolder {

            private TextView textViewName;
            private TextView textViewLicense;

        }
    }
}
