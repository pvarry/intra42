package com.paulvarry.intra42.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ReplacementSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import com.paulvarry.intra42.utils.Tag;
import com.paulvarry.intra42.utils.Tools;

public class TagSpanGenerator {

    private SpannableStringBuilder stringBuilder;
    private Context context;

    private float topMargin;
    private float bottomMargin;
    private float startMargin;
    private float endMargin;

    private float topPadding;
    private float bottomPadding;
    private float startPadding;
    private float endPadding;

    private int cornerRadius;
    private float textSize;

    private TagSpanGenerator(Context context) {
        this.context = context;
        stringBuilder = new SpannableStringBuilder();
    }

    public void addText(String text) {
        stringBuilder.append(text);
    }

    public void addTag(String text, int backgroundColor) {
        int tagStart = stringBuilder.length();
        RoundedBackgroundSpan tagSpan = new RoundedBackgroundSpan(context, backgroundColor, Color.WHITE);
        stringBuilder.append(text);
        stringBuilder.setSpan(tagSpan, tagStart, tagStart + text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        stringBuilder.append(" ");
    }

    public void addTagWithRes(String text, @ColorRes int backgroundColor) {
        addTag(text, ResourcesCompat.getColor(context.getResources(), backgroundColor, null));
    }

    public void addTag(String text) {
        addTagWithRes(text, Tag.getTagColor(context, text));
    }

    public SpannableStringBuilder getString() {
        return stringBuilder;
    }

    public static class Builder {

        private Context context;
        private TagSpanGenerator generator;

        private int topMargin = 1;
        private int bottomMargin = 1;
        private int startMargin = 0;
        private int endMargin = 0;
        private int topPadding = 2;
        private int bottomPadding = 2;
        private int startPadding = 6;
        private int endPadding = 6;
        private int corner = 8;
        private float textSize;

        public Builder(Context context) {
            this.context = context;
            generator = new TagSpanGenerator(context);
        }

        public Builder setMargin(int top, int end, int bottom, int start) {
            topMargin = top;
            endMargin = end;
            bottomMargin = bottom;
            startMargin = start;
            return this;
        }

        public Builder setMarginHorizontal(int margin) {
            topMargin = margin;
            bottomMargin = margin;
            return this;
        }

        public Builder setPadding(int top, int end, int bottom, int start) {
            topPadding = top;
            endPadding = end;
            bottomPadding = bottom;
            startPadding = start;
            return this;
        }

        public Builder setCorner(int corner) {
            this.corner = corner;
            return this;
        }

        public Builder setTextSize(float textSize) {
            this.textSize = textSize;
            return this;
        }

        public TagSpanGenerator build() {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            generator.topMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topMargin, displayMetrics);
            generator.endMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, endMargin, displayMetrics);
            generator.bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomMargin, displayMetrics);
            generator.startMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, startMargin, displayMetrics);
            generator.topPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topPadding, displayMetrics);
            generator.endPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, endPadding, displayMetrics);
            generator.bottomPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomPadding, displayMetrics);
            generator.startPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, startPadding, displayMetrics);
            generator.cornerRadius = corner;
            generator.textSize = textSize;
            return generator;
        }
    }

    public class RoundedBackgroundSpan extends ReplacementSpan {

        Context context;
        private int mBackgroundColor;
        private int mTextColor;

        /**
         * @param backgroundColor color value, not res id
         */
        RoundedBackgroundSpan(Context context, int backgroundColor, int textColor) {
            mBackgroundColor = backgroundColor;
            mTextColor = textColor;

            this.context = context;
        }


        private int getTagWidth(CharSequence text, int start, int end, Paint paint) {
            return Math.round(startMargin + startPadding + paint.measureText(text.subSequence(start, end).toString()) + endPadding + endMargin);
        }

        private int getTagInsideWidth(CharSequence text, int start, int end, Paint paint) {
            return Math.round(startPadding + paint.measureText(text.subSequence(start, end).toString()) + endPadding);
        }

        private float getTextSize() {
            return textSize - bottomPadding - topPadding - bottomMargin - topMargin;
        }

        /**
         * Returns the width of the span. Extending classes can set the height of the span by updating
         * attributes of {@link Paint.FontMetricsInt}. If the span covers the whole
         * text, and the height is not set,
         * {@link #draw(Canvas, CharSequence, int, int, float, int, int, int, Paint)} will not be
         * called for the span.
         *
         * @param paint Paint instance.
         * @param text  Current text.
         * @param start Start character index for span.
         * @param end   End character index for span.
         * @param fm    Font metrics, can be null.
         * @return Width of the span.
         */
        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            paint = new Paint(paint); // make a copy for not editing the referenced paint
            paint.setTextSize(getTextSize());
            return getTagWidth(text, start, end, paint);
        }

        /**
         * Draws the span into the canvas.
         *
         * @param canvas Canvas into which the span should be rendered.
         * @param text   Current text.
         * @param start  Start character index for span.
         * @param end    End character index for span.
         * @param x      Edge of the replacement closest to the leading margin.
         * @param top    Top of the line.
         * @param y      Baseline.
         * @param bottom Bottom of the line.
         * @param paint  Paint instance.
         */
        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            Paint newPaint = new Paint(paint); // make a copy for not editing the referenced paint

            final float MAGIC_NUMBER = Tools.dpToPxInt(context, 2);

            float mTextSize = getTextSize();

            newPaint.setTextSize(mTextSize);

            // Draw the rounded background
            newPaint.setColor(mBackgroundColor);
            float tagRight = x + getTagInsideWidth(text, start, end, newPaint);
            RectF rect = new RectF(x, top + topMargin, tagRight, bottom - bottomMargin);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, newPaint);

            // Draw the text
            newPaint.setColor(mTextColor);
            canvas.drawText(text, start, end, x + startPadding, top + topMargin + topPadding + mTextSize + MAGIC_NUMBER, newPaint);
            newPaint.setTextSize(textSize);
        }
    }

}
