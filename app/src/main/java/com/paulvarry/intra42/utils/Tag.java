package com.paulvarry.intra42.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.adapters.ChipViewAdapterForum;
import com.paulvarry.intra42.adapters.ChipViewAdapterUsers;
import com.paulvarry.intra42.api.model.Achievements;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.api.model.Tags;
import com.plumillonforge.android.chipview.ChipView;
import com.veinhorn.tagview.TagView;

import java.util.List;

public class Tag {

    public static void setTagEvent(Events event, TagView tagView) {

        String str;
        if (event.kind != null)
            switch (event.kind) {
                case CONFERENCE:
                    str = "conf";
                    break;
                case OTHER:
                    str = "other";
                    break;
                case EXTERN:
                    str = "extern";
                    break;
                case ATELIER:
                    str = "atelier";
                    break;
                case MEET_UP:
                    str = "meet up";
                    break;
                case HACKATHON:
                    str = "hackathon";
                    break;
                case WORKSHOP:
                    str = "workshop";
                    break;
                case ASSOCIATION:
                    str = "association";
                    break;
                case PARTNERSHIP:
                    str = "partnership";
                    break;
                default:
                    str = String.valueOf(event.kind);
            }
        else {
            str = "unknown";
        }

        tagView.setText(str);
        tagView.setTagColor(getTagColor(event));
    }

    public static int getTagColor(Events event) {
        if (event == null || event.kind == null)
            return -16729412;
        switch (event.kind) {
            case CONFERENCE:
                return Color.parseColor("#00babc");
            case OTHER:
                return Color.parseColor("#c0c0c0");
            case EXTERN:
                return Color.parseColor("#c0c0c0");
            case ATELIER:
                return -6432158;
            case MEET_UP:
                return Color.parseColor("#00babc");
            case HACKATHON:
                return Color.parseColor("#39D88F");
            case WORKSHOP:
                return Color.parseColor("#39D88F");
            case ASSOCIATION:
                return Color.parseColor("#a2b3e5");
            case PARTNERSHIP:
                return Color.parseColor("#39D88F");
            case CHALLENGE:
                return Color.parseColor("#39D88F");
            case EVENT:
                return Color.parseColor("#00babc");
            default:
                return Color.parseColor("#00babc");
        }
    }

    public static void setTagAchievement(Context context, Achievements achievement, TagView tagView) {

        String str = null;
        int color = 0;
        tagView.setVisibility(View.VISIBLE);
        switch (achievement.tier) {
            case "easy":
                str = "bronze";
                color = ContextCompat.getColor(context, R.color.user_achievements_bronze);
                break;
            case "medium":
                str = "silver";
                color = ContextCompat.getColor(context, R.color.user_achievements_solver);
                break;
            case "hard":
                str = "gold";
                color = ContextCompat.getColor(context, R.color.user_achievements_gold);
                break;
            case "challenge":
                str = "platinum";
                color = ContextCompat.getColor(context, R.color.user_achievements_platinum);
                break;
            case "none":
                tagView.setVisibility(View.GONE);
                break;
            default:
                Log.d("setTagAchievement", achievement.tier);
                str = achievement.tier;
        }

        if (str != null)
            tagView.setText(str);
        if (color != 0)
            tagView.setTagColor(color);
    }

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
                Log.i("setTagUser", String.valueOf(tag.id) + " - " + tag.name);
        }

        return color;
    }

    public static void setTagForum(Context context, List<Tags> tags, ChipView chipViewTags) {
        ChipViewAdapterForum adapter = new ChipViewAdapterForum(context);
        adapter.setTagList(tags);
        adapter.setChipCornerRadius(5);

        chipViewTags.setAdapter(adapter);
        chipViewTags.setChipCornerRadius(5);
    }

    public static void setTagUsers(Context context, List<Tags> tags, ChipView chipViewTags) {
        ChipViewAdapterUsers adapter = new ChipViewAdapterUsers(context);
        adapter.setTagList(tags);
        adapter.setChipCornerRadius(5);

        chipViewTags.setAdapter(adapter);
        chipViewTags.setChipCornerRadius(5);
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
