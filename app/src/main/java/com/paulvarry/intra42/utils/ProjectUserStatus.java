package com.paulvarry.intra42.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.Teams;

public class ProjectUserStatus {

    static public void setMark(Context context, Teams teams, TextView textView) {
        if (teams.status == ProjectsUsers.Status.FINISHED) {
            setMarkText(textView, teams.finalMark, teams.validated, context);
        } else
            textView.setText(teams.status.getRes());
    }

    static public void setMark(Context context, @Nullable ProjectsUsers projects, TextView textView) {

        if (projects == null) {
            textView.setVisibility(View.GONE);
            return;
        } else
            textView.setVisibility(View.VISIBLE);

        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        if (projects.status == ProjectsUsers.Status.FINISHED) {
            setMarkText(textView, projects.finalMark, projects.validated, context);
        } else {
            textView.setText(projects.status.getRes());
            textView.setTextColor(context.getResources().getColor(R.color.gray));
        }
    }

    static private void setMarkText(TextView textView, Integer finalMark, Boolean validated, Context context) {
        if (finalMark != null)
            textView.setText(String.valueOf(finalMark));
        else
            textView.setText(R.string.project_no_scale);
        if (validated == null) {
            textView.setText(R.string.project_no_scale);
        } else if (validated) {
//            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_check_black_24dp, context.getTheme());
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_check_black_24dp);
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.colorTintCheck));
                textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorTintCheck));
        } else {
//            Drawable drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_close_black_24dp, context.getTheme());
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_close_black_24dp);
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.colorTintCross));
                textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
            textView.setTextColor(ContextCompat.getColor(context, R.color.colorTintCross));
        }
    }
}
