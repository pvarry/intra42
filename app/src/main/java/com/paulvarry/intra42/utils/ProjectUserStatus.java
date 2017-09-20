package com.paulvarry.intra42.utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.Teams;

public class ProjectUserStatus {

    public static final String SEARCHING_A_GROUP = "searching_a_group";
    public static final String FINISHED = "finished";
    public static final String IN_PROGRESS = "in_progress";
    public static final String WAITING_FOR_CORRECTION = "waiting_for_correction";
    public static final String WAITING_TO_START = "waiting_to_start";
    public static final String CREATING_GROUP = "creating_group";
    public static final String PARENT = "parent";
    public static final String UNKNOWN = "unknown";

    static public String getProjectStatus(Context context, String status) {
        switch (status) {
            case SEARCHING_A_GROUP:
                return context.getString(R.string.project_user_status_searching_a_group);
            case FINISHED:
                return context.getString(R.string.project_user_status_finished);
            case IN_PROGRESS:
                return context.getString(R.string.project_user_status_in_progress);
            case WAITING_FOR_CORRECTION:
                return context.getString(R.string.project_user_status_waiting_for_correction);
            case WAITING_TO_START:
                return context.getString(R.string.project_user_status_waiting_to_start);
            case CREATING_GROUP:
                return context.getString(R.string.project_user_status_creating_group);
            case PARENT:
                return context.getString(R.string.project_user_status_parent);
            case UNKNOWN:
                return context.getString(R.string.project_user_status_unknown);
            default:
                return context.getString(R.string.project_user_status_unknown);
        }
    }

    static public void setMark(Context context, Teams teams, TextView textView) {
        if (teams.status.equals(ProjectUserStatus.FINISHED)) {
            setMarkText(textView, teams.finalMark, teams.validated, context);
        } else
            textView.setText(getProjectStatus(context, teams.status));
    }

    static public void setMark(Context context, ProjectsUsers projects, TextView textView) {

        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        if (projects.status.equals(FINISHED)) {
            setMarkText(textView, projects.finalMark, projects.validated, context);
        } else {
            textView.setText(getProjectStatus(context, projects.status));
            textView.setTextColor(ContextCompat.getColor(context, R.color.textColorBlackSecondary));
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
