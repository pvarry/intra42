package com.paulvarry.intra42.activities.intro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowInsets;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.home.HomeActivity;
import com.paulvarry.intra42.utils.Calendar;

public class IntroActivity
        extends AppIntro
        implements IntroCalendarFragment.OnFragmentInteractionListener, IntroTranslationFragment.OnFragmentInteractionListener {

    private static final int PERMISSIONS_REQUEST_CALENDAR = 1;

    private IntroCalendarFragment introCalendarFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        introCalendarFragment = IntroCalendarFragment.newInstance();
        addSlide(introCalendarFragment);
        addSlide(IntroTranslationFragment.newInstance());

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Cluster Map");
        sliderPage.setDescription("Cluster Map");
        sliderPage.setImageDrawable(R.drawable.intro_sync_calendar);
        sliderPage.setBgColor(Color.parseColor("#1EBBD1"));
        addSlide(AppIntroFragment.newInstance(sliderPage));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#00000000"));
        setSeparatorColor(Color.parseColor("#00000000"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            pager.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    return insets;
                }
            });
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent intent = HomeActivity.getIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CALENDAR: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Calendar.setEnableCalendarWithAutoSelect(this, true);
                    introCalendarFragment.permissionFinished();
                }
            }
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void askCalendarPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                    PERMISSIONS_REQUEST_CALENDAR);
        } else {
            Calendar.setEnableCalendarWithAutoSelect(this, true);
            introCalendarFragment.permissionFinished();
        }
    }
}
