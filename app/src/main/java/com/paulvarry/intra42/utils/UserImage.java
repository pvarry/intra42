package com.paulvarry.intra42.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.AppCompatDrawableManager;
import android.widget.ImageView;
import com.paulvarry.intra42.BuildConfig;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class UserImage {

    private static final String BASE_URL = "http://cdn.intra.42.fr/users/";

    private static RequestCreator getImageLarge(Picasso picasso, String login) {

        String url = BASE_URL + "large_" + login + ".jpg";

        return picasso.load(url)
                .resize(583, 700);
    }

    private static RequestCreator getImageMedium(Picasso picasso, String login) {

        String url = BASE_URL + "medium_" + login + ".jpg";

        return picasso.load(url)
                .resize(292, 350);
    }

    private static RequestCreator getImageDefault(Picasso picasso, String login) {

        String url = BASE_URL + login + ".jpg";

        return picasso.load(url)
                .resize(200, 240);
    }

    public static RequestCreator getImageSmall(Picasso picasso, String login) {

        String url = BASE_URL + "small_" + login + ".jpg";

        return picasso.load(url)
                .resize(146, 175);
    }

    private static RequestCreator getRequestCreator(Context context, UsersLTE user) {

        SharedPreferences sharedPreferences = AppSettings.getSharedPreferences(context);

        String type;

        if (Connectivity.isConnectedWifi(context))
            type = sharedPreferences.getString("list_preference_network_wifi", "large");
        else if (Connectivity.isConnectedFast(context))
            type = sharedPreferences.getString("list_preference_network_mobile_fast", "medium");
        else
            type = sharedPreferences.getString("list_preference_network_mobile_slow", "small");

        return getRequestCreator(context, user, type);
    }

    private static RequestCreator getRequestCreator(Context context, UsersLTE user, String type) {
        Picasso picasso = Picasso.with(context);
        RequestCreator requestCreator;

        if (BuildConfig.DEBUG)
            picasso.setLoggingEnabled(true);

        switch (type) {
            case "large":
                requestCreator = getImageLarge(picasso, user.login);
                break;
            case "medium":
                requestCreator = getImageMedium(picasso, user.login);
                break;
            case "default":
                requestCreator = getImageDefault(picasso, user.login);
                break;
            case "small":
                requestCreator = getImageSmall(picasso, user.login);
                break;
            default:
                requestCreator = getImageDefault(picasso, user.login);
        }

        if (requestCreator == null)
            return null;

        requestCreator.centerCrop();
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP)
            requestCreator.placeholder(R.drawable.ic_person_black_custom);
        else {
            Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_person_black_custom);
            requestCreator.placeholder(drawable);
        }

        return requestCreator;
    }

    public static RequestCreator getPicassoCorned(RequestCreator request) {
        final int radius = 5;
        final int margin = 5;
        final Transformation transformation = new RoundedCornersTransformation(radius, margin);
        request.transform(transformation);
        return request;
    }

    public static RequestCreator getPicassoCorned(Context context, UsersLTE user) {
        RequestCreator p = getRequestCreator(context, user);
        if (p == null)
            return null;
        return getPicassoCorned(p);
    }

    public static RequestCreator getPicassoRounded(RequestCreator request) {
        final Transformation transformation = new CropCircleTransformation();
        request.transform(transformation);
        return request;
    }

    public static RequestCreator getPicassoRounded(Context context, UsersLTE user) {
        RequestCreator p = getRequestCreator(context, user);
        if (p == null)
            return null;
        return getPicassoRounded(p);
    }

    static public void setImage(Context context, UsersLTE user, ImageView imageView) {
        RequestCreator picasso = getRequestCreator(context, user);

        if (picasso == null)
            return;

        picasso.into(imageView);
    }

    static public void setImageSmall(Context context, UsersLTE user, ImageView imageView) {
        RequestCreator picasso = getRequestCreator(context, user, "small");

        if (picasso == null)
            return;

        picasso.into(imageView);
    }
}
