package com.paulvarry.intra42.Tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.caverock.androidsvg.SVG;

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
}
