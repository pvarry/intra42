package com.paulvarry.intra42.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.UserImage;
import com.squareup.picasso.RequestCreator;

public class ImageViewerActivity extends AppCompatActivity {

    private static final String INTENT_LOGIN = "user_login";

    public static void openIt(Context context, String login) {

        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(INTENT_LOGIN, login);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        String login;

        if (getIntent().hasExtra(INTENT_LOGIN))
            login = getIntent().getStringExtra(INTENT_LOGIN).toLowerCase();
        else
            return;

        PhotoView photoView = findViewById(R.id.photeView);

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
