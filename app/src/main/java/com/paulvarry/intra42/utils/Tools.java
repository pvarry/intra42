package com.paulvarry.intra42.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ItemWithId;
import com.paulvarry.intra42.api.model.Attachments;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.StringJoiner;

import in.uncod.android.bypass.Bypass;
import retrofit2.Response;

public class Tools {

    public static void openAttachment(Activity activity, @Nullable Attachments attachment) {
        if (attachment != null && attachment.url != null) {
            openAttachment(activity, attachment.url);
        }
    }

    /**
     * Open url like "/stuff/file.txt"
     *
     * @param activity A Activity
     * @param url      the url
     */
    public static void openAttachment(Activity activity, String url) {
        if (url != null) {

            if (!url.startsWith("http"))
                url = "http://cdn.intra.42.fr" + url;

            Uri uri = Uri.parse(url.replace("/uploads", ""));

            Intent intent = new Intent(Intent.ACTION_VIEW);
            // Check what kind of file you are trying to open, by comparing the url with extensions.
            // When the if condition is matched, plugin sets the correct intent (mime) type,
            // so Android knew what application to use to open the file
            if (url.contains(".doc") || url.contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.contains(".ppt") || url.contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.contains(".xls") || url.contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.contains(".zip") || url.contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (url.contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.contains(".wav") || url.contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                //if you want you can also define the intent type for any other file

                //additionally use else clause below, to manage other unknown extensions
                //in this case, Android will show all applications installed on the device
                //so you can choose which application to use intent.setDataAndType(uri, "*/*");
                intent.setDataAndType(uri, "text/plain");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                activity.startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {

                if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi"))
                    Toast.makeText(activity, R.string.info_attachment_no_app_video, Toast.LENGTH_SHORT).show();
                else if (url.contains(".pdf"))
                    Toast.makeText(activity, R.string.info_attachment_no_app_pdf, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(activity, R.string.info_attachment_no_app, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void setMarkdown(Context context, TextView textView, String content) {
        if (AppSettings.Advanced.getAllowMarkdownRenderer(context)) {
            Bypass bypass = new Bypass(context);

            content = content.replace(":\r\n-", ":\r\n\r\n-");

            CharSequence string = bypass.markdownToSpannable(content, new BypassPicassoImageGetter(textView, Picasso.with(context)));

            textView.setText(string);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        } else
            textView.setText(content);
    }

    public static boolean setLayoutOnError(View view, int image, int text, final SwipeRefreshLayout.OnRefreshListener refreshListener) {
        if (view == null)
            return false;
        ImageView imageView = view.findViewById(R.id.imageViewStatus);
        TextView textViewError = view.findViewById(R.id.textViewError);
        Button buttonRefresh = view.findViewById(R.id.buttonRefresh);

        if (imageView == null || textViewError == null || buttonRefresh == null)
            return false;

        view.setVisibility(View.VISIBLE);

        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(image);
        textViewError.setText(text);
        if (refreshListener != null)
            buttonRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshListener.onRefresh();
                }
            });
        else
            buttonRefresh.setVisibility(View.GONE);
        return true;
    }

    public static boolean apiIsSuccessful(Response<?> response) throws BasicThreadActivity.UnauthorizedException, BasicThreadActivity.ErrorServerException {
        if (response == null)
            throw new BasicThreadActivity.ErrorServerException();
        else if (response.isSuccessful())
            return true;
        else if (response.code() / 100 == 4)
            throw new BasicThreadActivity.UnauthorizedException();
        else if (response.code() / 100 == 5)
            throw new BasicThreadActivity.ErrorServerException();
        else return false;
    }

    public static boolean apiIsSuccessfulNoThrow(Response<?> response) {
        try {
            return apiIsSuccessful(response);
        } catch (BasicThreadActivity.UnauthorizedException | BasicThreadActivity.ErrorServerException e) {
            return false;
        }
    }


    public static <T extends ItemWithId> String concatListIds(List<T> list) {
        String eventsId;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            StringJoiner join = new StringJoiner(",");
            for (T item : list) {
                join.add(String.valueOf(item.id));
            }
            eventsId = join.toString();
        } else {
            StringBuilder builder = new StringBuilder();
            String join = "";
            for (T item : list) {
                builder.append(join).append(String.valueOf(item.id));
                join = ",";
            }
            eventsId = builder.toString();
        }

        return eventsId;
    }

    @Deprecated
    public static String concatIds(List<Integer> integerList) {
        String eventsId;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            StringJoiner join = new StringJoiner(",");
            for (Integer integer : integerList) {
                join.add(String.valueOf(integer));
            }
            eventsId = join.toString();
        } else {
            StringBuilder builder = new StringBuilder();
            String join = "";
            for (Integer e : integerList) {
                builder.append(join).append(String.valueOf(e));
                join = ",";
            }
            eventsId = builder.toString();
        }

        return eventsId;
    }

    public static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toString();
    }
}
