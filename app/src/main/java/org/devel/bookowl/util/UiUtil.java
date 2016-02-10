package org.devel.bookowl.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import org.devel.bookowl.R;


public class UiUtil {

    public static void loadSettings(Context mContext, Activity a) {

        UiUtil.setBrightness(mContext, a);
    }

    public static int getScreenOrientation(Activity a) {

        return a.getResources().getConfiguration().orientation;
    }

    public static int itemsInBookShelf(Activity a) {

        int orientation = UiUtil.getScreenOrientation(a);
        int numItems = 3;

            // 1 for Configuration.ORIENTATION_PORTRAIT
            // 2 for Configuration.ORIENTATION_LANDSCAPE
        if (orientation== Configuration.ORIENTATION_PORTRAIT)
        {
            // portrait
            numItems = 3;
        } else {
            // landspace
            numItems = 5;
        }
        return numItems;
    }

    public static void setBrightness(Context mContext, Activity a) {

        try {

            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(mContext);

            float v = prefs.getFloat(
                    mContext.getResources().getString(R.string.brightness_preference_key), 50);

            WindowManager.LayoutParams lp = a.getWindow().getAttributes();
            lp.screenBrightness = v / 100.0f;
            a.getWindow().setAttributes(lp);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public static void updateBrightness(Activity a, float v) {

        try {

            WindowManager.LayoutParams lp = a.getWindow().getAttributes();
            lp.screenBrightness = v / 100.0f;
            a.getWindow().setAttributes(lp);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
