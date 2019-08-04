package com.paulvarry.intra42.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.ChipDrawable;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Achievements;
import com.paulvarry.intra42.api.model.Events;
import com.paulvarry.intra42.api.model.Tags;
import com.paulvarry.intra42.utils.Tag;

public class TagSpanGenerator {

    private SpannableStringBuilder stringBuilder;
    private Context context;

    public TagSpanGenerator(Context context) {
        this.context = context;
        stringBuilder = new SpannableStringBuilder();
    }

    public void addText(String text) {
        stringBuilder.append(text);
    }

    public void addTag(Tags tag) {
        addTag(tag.name, Tag.getUsersTagColor(tag));
    }

    public void addTag(Events.EventKind kind) {
        addTag(kind.getString(context), kind.getColorInt(context));
    }


    public void addTag(Achievements achievement) {
        String str = null;
        @ColorRes
        int colorBackground = 0;
        int colorText = 0;
        switch (achievement.tier) {
            case "easy":
                str = context.getString(R.string.user_achievement_bronze);
                colorBackground = R.color.user_achievements_bronze;
                colorText = android.R.color.white;
                break;
            case "medium":
                str = context.getString(R.string.user_achievement_silver);
                colorBackground = R.color.user_achievements_silver;
                colorText = android.R.color.black;
                break;
            case "hard":
                str = context.getString(R.string.user_achievement_gold);
                colorBackground = R.color.user_achievements_gold;
                colorText = android.R.color.black;
                break;
            case "challenge":
                str = context.getString(R.string.user_achievements_platinum);
                colorBackground = R.color.user_achievements_platinum;
                colorText = android.R.color.black;
                break;
        }

        if (str != null) {
            addTag(str, ContextCompat.getColor(context, colorBackground), ContextCompat.getColor(context, colorText));
        }
    }

    public void addTag(String text, @ColorInt int backgroundColor) {
        addTag(text, backgroundColor, 0);
    }

    public void addTag(String text, @ColorInt int backgroundColor, @ColorInt int textColor) {

        ChipDrawable chip = ChipDrawable.createFromResource(context, R.xml.standalone_chip);
        // Use it as a Drawable however you want.
        chip.setText(text);
        chip.setChipBackgroundColor(ColorStateList.valueOf(backgroundColor));
        chip.setBounds(0, 0, chip.getIntrinsicWidth(), (int) (chip.getIntrinsicHeight() * 0.75f));
        ImageSpan imageSpan = new ImageSpan(chip);

        int length = stringBuilder.length();
        stringBuilder.append(text);
        stringBuilder.setSpan(imageSpan, length, length + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public SpannableStringBuilder getString() {
        return stringBuilder;
    }
}
