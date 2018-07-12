package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.chrisbanes.photoview.PhotoView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Users;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.UserImage;
import com.squareup.picasso.RequestCreator;

public class ImageViewerActivity extends AppCompatActivity {

    private static final String INTENT_LOGIN = "user_login";
    private static final String INTENT_DISPLAY_NAME = "display_name";

    public static void openIt(Context context, Users user) {

        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(INTENT_LOGIN, user.login);
        intent.putExtra(INTENT_DISPLAY_NAME, user.displayName + " - " + user.login);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        String login;
        String displayName;

        if (getIntent().hasExtra(INTENT_LOGIN))
            login = getIntent().getStringExtra(INTENT_LOGIN).toLowerCase();
        else
            return;
        displayName = getIntent().getStringExtra(INTENT_DISPLAY_NAME);

        PhotoView photoView = findViewById(R.id.photoView);
        TextView textView = findViewById(R.id.textView);

        if (displayName != null && !displayName.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(displayName);
        } else {
            textView.setVisibility(View.GONE);
        }
        UsersLTE user = new UsersLTE();
        user.login = login;
        RequestCreator requestCreator = UserImage.getRequestCreator(this, user, "large");
        if (requestCreator != null)
            requestCreator.into(photoView);

//        photoView.setOnSingleFlingListener(new OnSingleFlingListener() {
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                finish();
//                return true;
//            }
//        });
    }
}
