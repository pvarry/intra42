package com.paulvarry.intra42.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.caverock.androidsvg.SVG;
import com.paulvarry.intra42.BuildConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class mImage {

    public static Bitmap loadImageSVG(String imageUrl) {
        URL url;
        URLConnection urlConnection;

        try {
            url = new URL(imageUrl);
            urlConnection = url.openConnection();
            InputStream i = urlConnection.getInputStream();

            SVG svg = SVG.getFromInputStream(i);

            Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);

            svg.renderToCanvas(c);
            return b;

        } catch (IOException e) {
            Log.w("image not found", imageUrl);
            e.printStackTrace();
        } catch (com.caverock.androidsvg.SVGParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setPicasso(Uri url, ImageView imageView, @DrawableRes int placeHolder) {

        Picasso picasso = Picasso.get();

        if (BuildConfig.DEBUG)
            picasso.setLoggingEnabled(true);

        RequestCreator requestCreator = picasso.load(url);

        if (placeHolder != 0) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                requestCreator.placeholder(placeHolder);
                requestCreator.error(placeHolder);
            } else {
                Drawable drawable = ContextCompat.getDrawable(imageView.getContext(), placeHolder);
                requestCreator.placeholder(drawable);
                requestCreator.error(drawable);
            }
        }

        requestCreator.into(imageView);
    }
}
