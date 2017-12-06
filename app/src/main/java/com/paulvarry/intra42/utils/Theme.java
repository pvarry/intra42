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
import com.paulvarry.intra42.api.model.Coalitions;
import com.paulvarry.intra42.api.model.Users;

import java.util.List;

public class Theme {

    public static AppSettings.Theme.EnumTheme getThemeFromCoalition(Coalitions coalitions) {
        AppSettings.Theme.EnumTheme theme = null;
        switch (coalitions.id) {
            case 1:
                theme = AppSettings.Theme.EnumTheme.INTRA_FEDERATION;
                break;
            case 2:
                theme = AppSettings.Theme.EnumTheme.INTRA_ALLIANCE;
                break;
            case 3:
                theme = AppSettings.Theme.EnumTheme.INTRA_ASSEMBLY;
                break;
            case 4:
                theme = AppSettings.Theme.EnumTheme.INTRA_ORDER;
                break;
        }
        return theme;
    }

    public static AppSettings.Theme.EnumTheme getThemeFromCoalition(List<Coalitions> coalitions) {
        if (coalitions != null && coalitions.size() > 0)
            return getThemeFromCoalition(coalitions.get(0));
        return null;
    }

    private static int getTheme(Context context, Users users) {
        AppSettings.Theme.EnumTheme theme = AppSettings.Theme.getEnumTheme(context);

        if (theme == AppSettings.Theme.EnumTheme.INTRA && users != null) {
            AppSettings.Theme.EnumTheme tmp = getThemeFromCoalition(users.coalitions);
            if (tmp != null)
                theme = tmp;
        }
        return getThemeResource(theme);

    }

    public static void setTheme(AppClass app) {
        app.setTheme(getTheme(app, app.me));
    }

    public static void setTheme(Activity activity, AppClass app) {
        if (app != null)
            setTheme(activity, app.me);
    }

    public static void setTheme(Activity activity, Users user) {
        if (activity == null)
            return;

        activity.setTheme(getTheme(activity, user));
    }

    public static void setActionBar(AppBarLayout appBarLayout, AppClass app) {

        AppSettings.Theme.EnumTheme theme = AppSettings.Theme.getEnumTheme(app);
        if (theme == AppSettings.Theme.EnumTheme.INTRA && app != null && app.me != null) {
            AppSettings.Theme.EnumTheme tmp = Theme.getThemeFromCoalition(app.me.coalitions);
            if (tmp != null)
                theme = tmp;
        }
        setActionBar(appBarLayout, theme);
    }

    public static void setActionBar(AppBarLayout appBarLayout, AppSettings.Theme.EnumTheme enumTheme) {
        if (appBarLayout == null)
            return;

        AppSettings.Theme.EnumTheme themeSettings = AppSettings.Theme.getEnumTheme(appBarLayout.getContext());
        if (themeSettings == AppSettings.Theme.EnumTheme.INTRA) {
            themeSettings = AppSettings.Theme.getEnumTheme(appBarLayout.getContext());
            if (themeSettings != AppSettings.Theme.EnumTheme.INTRA &&
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
    public static int getThemeResource(Context context) {
        return getThemeResource(AppSettings.Theme.getEnumTheme(context));
    }

    @StyleRes
    private static int getThemeResource(AppSettings.Theme.EnumTheme theme) {
        int themeRes;

        switch (theme) {
            case DEFAULT:
                themeRes = R.style.ThemeIntra;
                break;
            case INTRA:
                themeRes = R.style.ThemeIntra;
                break;
            case INTRA_ORDER:
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
            case STUDIOS_42:
                themeRes = R.style.ThemeStudios;
                break;
            case STUDIOS_42_DARK:
                themeRes = R.style.ThemeStudiosDark;
                break;
            case ANDROID:
                themeRes = R.style.ThemeDarkAndroid;
                break;
            case OLD:
                themeRes = R.style.ThemeOld;
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

