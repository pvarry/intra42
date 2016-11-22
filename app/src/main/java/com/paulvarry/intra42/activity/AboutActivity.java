package com.paulvarry.intra42.activity;

import android.content.Context;
import android.content.Intent;
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

import com.paulvarry.intra42.BuildConfig;
import com.paulvarry.intra42.R;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ListView listView = (ListView) findViewById(R.id.listView);

        List<Item> list = new ArrayList<>();
        Item item;

        item = new Item(getString(R.string.activity_about_content), null);
        list.add(item);

        String str = BuildConfig.VERSION_NAME + " (" + String.valueOf(BuildConfig.VERSION_CODE) + ")";
        item = new Item(str, getString(R.string.version));
        list.add(item);

        item = new Item("Library list", null);
        list.add(item);

        Adapter adapter = new Adapter(list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 2) {
                    Intent intent = new Intent(AboutActivity.this, LibraryListActivity.class);
                    startActivity(intent);
                }
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
                holder.textViewName = (TextView) view.findViewById(R.id.textViewName);
                holder.textViewSub = (TextView) view.findViewById(R.id.textViewSub);

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
