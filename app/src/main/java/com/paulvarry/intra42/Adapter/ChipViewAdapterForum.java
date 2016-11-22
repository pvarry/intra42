package com.paulvarry.intra42.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.Tag;
import com.paulvarry.intra42.api.Tags;
import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChipViewAdapterForum extends ChipViewAdapter {

    public ChipViewAdapterForum(Context context) {
        super(context);
    }

    @Override
    public int getLayoutRes(int position) {
        return R.layout.include_chip;
    }

    @Override
    public int getBackgroundColorSelected(int position) {
        return 0;
    }

    @Override
    public int getBackgroundRes(int position) {
        return 0;
    }

    @Override
    public int getBackgroundColor(int position) {
        Tags tag = (Tags) getChip(position);

        int id = Tag.getTagColor(getContext(), tag.name);
        return getColor(id);
    }

    @Override
    public void onLayout(View view, int position) {
        ((TextView) view.findViewById(android.R.id.text1)).setTextColor(getColor(R.color.colorGrayLight));
    }

    public void setTagList(List<Tags> list) {

        List<Chip> chip = new ArrayList<>();
        for (Tags t : list) {
            chip.add(t);
        }
        super.setChipList(chip);
    }
}