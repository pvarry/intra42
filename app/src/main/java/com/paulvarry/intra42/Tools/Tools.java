package com.paulvarry.intra42.Tools;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.paulvarry.intra42.api.Attachments;

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
                url = "https://cdn.intra.42.fr" + url;

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
                Toast.makeText(activity, "There are no applications installed to open this", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
