package com.paulvarry.intra42.Tools;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v4.app.ShareCompat;
import android.widget.Toast;

import com.paulvarry.intra42.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class Share {

    static public void shareString(Activity activity, String textToShare) {

        if (activity == null || textToShare == null)
            return;

        ShareCompat.IntentBuilder
                .from(activity)
                .setText(textToShare)
                .setType("text/plain") // most general text sharing MIME type
                .setChooserTitle(R.string.share_using)
                .startChooser();
    }

    static public void copyString(Context context, String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", textToCopy);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();
    }

}
