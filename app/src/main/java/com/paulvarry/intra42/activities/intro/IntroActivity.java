package com.paulvarry.intra42.activities.intro;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.home.HomeActivity;

public class IntroActivity
        extends AppIntro
        implements IntroCalendarFragment.OnFragmentInteractionListener, IntroTranslationFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(IntroCalendarFragment.newInstance());
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
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        startActivity(HomeActivity.getIntent(this));
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
