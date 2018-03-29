package com.paulvarry.intra42.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StyleRes;
import android.support.design.widget.AppBarLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Users;

public class ThemeHelper {

    public static void setTheme(Activity activity, AppClass app) {
        activity.setTheme(app.themeRes);
    }

    public static void setTheme(Activity activity, Users user) {
        if (activity == null)
            return;

        activity.setTheme(getThemeResource(AppSettings.Theme.getEnumTheme(activity, user)));
    }

    public static void setActionBar(AppBarLayout appBarLayout, AppClass app) {
        setActionBar(appBarLayout, app.themeSettings);
    }

    public static void setActionBar(AppBarLayout appBarLayout, Users user) {
        setActionBar(appBarLayout, AppSettings.Theme.getEnumTheme(appBarLayout.getContext(), user));
    }

    public static void setActionBar(AppBarLayout appBarLayout, AppSettings.Theme.EnumTheme enumTheme) {
        if (appBarLayout == null)
            return;

        AppSettings.Theme.EnumTheme themeSettings = AppSettings.Theme.getEnumTheme(appBarLayout.getContext());
        if (themeSettings == AppSettings.Theme.EnumTheme.DEFAULT) {
            themeSettings = AppSettings.Theme.getEnumTheme(appBarLayout.getContext());
            if (themeSettings != AppSettings.Theme.EnumTheme.DEFAULT &&
                    themeSettings != AppSettings.Theme.EnumTheme.INTRA_FEDERATION &&
                    themeSettings != AppSettings.Theme.EnumTheme.INTRA_ASSEMBLY &&
                    themeSettings != AppSettings.Theme.EnumTheme.INTRA_ALLIANCE &&
                    themeSettings != AppSettings.Theme.EnumTheme.INTRA_ORDER)
                return;
        }

        ImageView imageView = appBarLayout.findViewById(R.id.imageViewActionBar);
        if (imageView == null)
            return;

        boolean enable = AppSettings.Theme.getActionBarBackgroundEnable(appBarLayout.getContext());

        if (enumTheme == null || !enable) {
            imageView.setVisibility(View.GONE);
            return;
        }

        imageView.setVisibility(View.VISIBLE);
        switch (enumTheme) {
            case INTRA_ORDER:
                imageView.setImageResource(R.drawable.order_background);
                break;
            case INTRA_ASSEMBLY:
                imageView.setImageResource(R.drawable.assembly_background);
                break;
            case INTRA_ALLIANCE:
                imageView.setImageResource(R.drawable.alliance_background);
                break;
            case INTRA_FEDERATION:
                imageView.setImageResource(R.drawable.federation_background);
                break;
            default:
                imageView.setVisibility(View.GONE);
        }
    }

    @StyleRes
    public static int getThemeResource(AppSettings.Theme.EnumTheme theme) {
        int themeRes;

        switch (theme) {
            case DEFAULT:
                if (theme.isDark())
                    themeRes = R.style.ThemeIntra_Dark;
                else
                    themeRes = R.style.ThemeIntra;
                break;
            case INTRA_ORDER:
                if (theme.isDark())
                    themeRes = R.style.ThemeIntraOrder_Dark;
                else
                    themeRes = R.style.ThemeIntraOrder;
                break;
            case INTRA_ASSEMBLY:
                themeRes = R.style.ThemeIntraAssembly;
                break;
            case INTRA_FEDERATION:
                themeRes = R.style.ThemeIntraFederation;
                break;
            case INTRA_ALLIANCE:
                themeRes = R.style.ThemeIntraAlliance;
                break;
            case ANDROID:
                themeRes = R.style.ThemeDarkAndroid;
                break;
            default:
                themeRes = R.style.ThemeIntra;
                break;
        }

        return themeRes;
    }

    public static int getColorPrimary(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    public static int getColorAccent(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }
}

