package com.paulvarry.intra42.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import in.uncod.android.bypass.Bypass;

/**
 * Original credits: http://stackoverflow.com/a/25530488/504611
 */
public class BypassPicassoImageGetter implements Bypass.ImageGetter {

    private final Picasso mPicasso;
    private final WeakReference<TextView> mTextView;
    private SourceModifier mSourceModifier;

    public BypassPicassoImageGetter(final TextView textView) {
        mTextView = new WeakReference<>(textView);
        mPicasso = Picasso.get();
    }

    @Override
    public Drawable getDrawable(String source) {

        final Handler handler = new Handler();

        final BitmapDrawablePlaceHolder result = new BitmapDrawablePlaceHolder();
        final String finalSource = mSourceModifier == null ? source : mSourceModifier.modify(source);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = mPicasso.load(finalSource).get();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = mTextView.get();
                            if (textView == null) {
                                return;
                            }
                            try {
                                int maxWidth;
                                int horizontalPadding = textView.getPaddingLeft() + textView.getPaddingRight();
                                maxWidth = textView.getMeasuredWidth() - horizontalPadding;
                                if (maxWidth == 0) {
                                    maxWidth = Integer.MAX_VALUE;
                                }

                                final BitmapDrawable drawable = new BitmapDrawable(textView.getResources(), bitmap);
                                final double aspectRatio = 1.0 * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                                final int width = Math.min(maxWidth, drawable.getIntrinsicWidth());
                                final int height = (int) (width / aspectRatio);

                                drawable.setBounds(0, 0, width, height);

                                result.setDrawable(drawable);
                                result.setBounds(0, 0, width, height);

                                textView.setText(textView.getText()); // invalidate() doesn't work correctly...
                            } catch (Exception e) {
                                //do something with this?
                            }
                        }

                    });
                } catch (Exception | OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return result;
    }

    /**
     * Set the {@link SourceModifier}
     *
     * @param sourceModifier the new source modifier
     */
    public void setSourceModifier(SourceModifier sourceModifier) {
        mSourceModifier = sourceModifier;
    }

    /**
     * Allows hooking into the source so that you can do things like modify relative urls
     */
    public interface SourceModifier {
        /**
         * Modify the source url, adding to it in any way you need to
         *
         * @param source the source url from the markdown
         * @return the modified url which will be loaded by Picasso
         */
        String modify(String source);
    }

    private static class BitmapDrawablePlaceHolder extends BitmapDrawable {

        protected Drawable drawable;

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }

    }
}