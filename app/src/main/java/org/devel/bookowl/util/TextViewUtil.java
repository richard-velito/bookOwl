package org.devel.bookowl.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.devel.bookowl.R;


public class TextViewUtil {

    private static String TAG = "TextViewUtil";

    public static TextView getTextView(Context mContext,
                                       int width, int height) {

        // get font SIZE

        //Typeface robotoThin = Typeface.createFromAsset(
        //        mContext.getResources().getAssets(), "Roboto-Thin.ttf");

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(mContext);

        float dimenOriginal = prefs.getInt(
                mContext.getResources().getString(R.string.fontsize_preference_key), 25);


        boolean active = prefs.getBoolean(
                mContext.getResources().getString(R.string.darkmode_preference_key), false);

        TextView tv = new TextView(mContext);
        tv.layout(0, 0, width, height);
        tv.setWidth(width);
        tv.setHeight(height);

        // use preference dark mode
        if (active) {

            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.BLACK);
        } else {

            tv.setTextColor(Color.BLACK);
            tv.setBackgroundColor(Color.WHITE);
        }

        //tv.setTypeface(robotoThin,
        //        Typeface.BOLD);

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                dimenOriginal);
        tv.setPadding(15, 15, 15, 15);

        return tv;
    }

}
