package com.paulvarry.intra42.Interface;

import android.support.v4.view.ViewPager;

public interface InterfaceViewActivity {
    /**
     * This method is run on a Thread, so you can make API calls and other long stuff.
     */
    void getData();

    /**
     * This method is run after getData(), there tou can set view with data previously obtained  .
     */
    void setView();

    interface Tab {

        /**
         * This method is run on a Thread, so you can make API calls and other long stuff.
         */
        void getData();

        /**
         * This method is run after getData(), there tou can set view with data previously obtained  .
         */
        void setView();

        void setupViewPager(ViewPager viewPager);
    }
}