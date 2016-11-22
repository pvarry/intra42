package com.paulvarry.intra42.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.Tag;
import com.paulvarry.intra42.api.Tags;
import com.tokenautocomplete.TokenCompleteTextView;

public class CompletionViewTags extends TokenCompleteTextView<Tags> {

    public CompletionViewTags(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Tags tag) {

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = l.inflate(R.layout.auto_complete_tag, (ViewGroup) getParent(), false);
        TextView textView = (TextView) view.findViewById(R.id.name);
        textView.setText(tag.name);

        textView.setBackgroundResource(R.drawable.shape_completion_view_tag);
        GradientDrawable drawable = (GradientDrawable) textView.getBackground();
        drawable.setColor(ResourcesCompat.getColor(getResources(), Tag.getTagColor(getContext(), tag), null)); //without theme

        return view;
    }

    @Override
    protected Tags defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        return null;
    }
}