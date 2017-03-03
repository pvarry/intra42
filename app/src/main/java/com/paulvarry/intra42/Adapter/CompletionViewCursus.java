package com.paulvarry.intra42.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Cursus;
import com.tokenautocomplete.TokenCompleteTextView;

public class CompletionViewCursus extends TokenCompleteTextView<Cursus> {

    public CompletionViewCursus(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Cursus cursus) {

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = l.inflate(R.layout.auto_complete_cursus, (ViewGroup) getParent(), false);
        TextView textView = (TextView) view.findViewById(R.id.name);
        textView.setText(cursus.name);

        return view;
    }

    @Override
    protected Cursus defaultObject(String completionText) {
        //Stupid simple example of guessing if we have an email or not
        return null;
    }
}