package com.paulvarry.intra42.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.api.model.Tags;

import java.util.List;

public class Tag {

    public static void setTagEvent(Events event, Chip tagView) {
        String str;
        Context context = tagView.getContext();

        if (event.kind != null)
            str = event.kind.getString(context);
        else
            str = context.getString(R.string.event_kind_unknown);

        tagView.setText(str);
        tagView.setChipBackgroundColorResource(event.kind.getColorRes());
        tagView.setTextColor(ContextCompat.getColor(tagView.getContext(), R.color.tag_on_event));
    }

    @ColorInt
    public static int getUsersTagColor(Tags tag) {

        int color = 0;
        switch (tag.id) {
            case 1: //staff
                color = Color.parseColor("#E05757");
                break;
            case 2: //dev unit
                color = Color.parseColor("#02C4C7");
                break;
            case 3: //pixel
                color = Color.parseColor("#CC0000");
                break;
            case 4: //representative
                color = Color.parseColor("#719434");
                break;
            case 5: //SI
                color = Color.parseColor("#333333");
                break;
            case 6: //pedago
                color = Color.parseColor("#333333");
                break;
            case 7: //adm
                color = Color.parseColor("#333333");
                break;
            case 8: //direction
                color = Color.parseColor("#333333");
                break;
            case 9: //cleaning
                color = Color.parseColor("#333333");
                break;
            case 10: //BDE
                color = Color.parseColor("#333333");
                break;
            case 12: //Mentor
                color = Color.parseColor("#33516D");
                break;
            default:
                Log.i("setTagUser", tag.id + " - " + tag.name);
        }

        return color;
    }

    public static void setTagForum(Context context, List<Tags> tags, ChipGroup chipViewTags) {
        chipViewTags.removeAllViews();
        if (tags == null || tags.size() == 0) {
            chipViewTags.setVisibility(View.GONE);
            return;
        } else
            chipViewTags.setVisibility(View.VISIBLE);

        chipViewTags.setChipSpacingHorizontalResource(R.dimen.chip_group_spacing_horizontal);
        chipViewTags.setChipSpacingVerticalResource(R.dimen.chip_group_spacing_vertical);

        for (Tags tag : tags) {
            Chip chip = new Chip(context);
            chip.setText(tag.name);
            chip.setChipBackgroundColorResource(getTagColor(context, tag));
            chipViewTags.addView(chip);
        }
    }

    public static void setTagUsers(Context context, List<Tags> tags, ChipGroup chipViewTags) {
        if (tags == null || tags.size() == 0) {
            chipViewTags.setVisibility(View.GONE);
            return;
        } else
            chipViewTags.setVisibility(View.VISIBLE);

        for (Tags tag : tags) {
            Chip chip = new Chip(context);
            chip.setText(tag.name);
            chip.setChipBackgroundColor(ColorStateList.valueOf(getUsersTagColor(tag)));
            chipViewTags.addView(chip);
        }
    }

    @ColorRes
    public static int getTagColor(Context context, Tags tag) {
        return getTagColor(context, tag.name);
    }

    @ColorRes
    public static int getTagColor(Context context, String tagName) {
        String colorName = "tag__" + tagName.toLowerCase().subSequence(0, 1);
        int res = context.getResources().getIdentifier(colorName, "color", context.getPackageName());
        if (res == 0)
            return R.color.tag__;
        else
            return res;
    }
}
