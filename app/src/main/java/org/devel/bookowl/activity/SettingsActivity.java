package org.devel.bookowl.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import org.devel.bookowl.R;
import org.devel.bookowl.util.UiUtil;

import de.mrapp.android.preference.SeekBarPreference;


public class SettingsActivity extends PreferenceActivity {

    private static String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UiUtil.loadSettings(getApplicationContext(),
                this);

        FragmentManager mFragmentManager = getFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        SettingsFragment mSettingsFragment = new SettingsFragment();
        mSettingsFragment.setMActivity(this);
        mFragmentTransaction.replace(android.R.id.content, mSettingsFragment);
        mFragmentTransaction.commit();
    }

    @Override
    public void onStop() {
        super.onStop();

        finish();
    }

    @Override
    protected void onResume() {

        // load settings
        UiUtil.loadSettings(getApplicationContext(),
                this);
        super.onResume();
    }

    public static class SettingsFragment extends PreferenceFragment {

        private static String TAG = "SettingsFragment";

        private Activity mActivity = null;

        private SeekBarPreference seekBarPreference;

        public void setMActivity(Activity mActivity) {

            this.mActivity = mActivity;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            seekBarPreference =
                    (SeekBarPreference) findPreference(getString(R.string.brightness_preference_key));
            seekBarPreference.showValueAsSummary(true);

            seekBarPreference.setOnPreferenceChangeListener(createShowValueAsSummaryListener(
                    mActivity));

        }

        private Preference.OnPreferenceChangeListener createShowValueAsSummaryListener(
                Activity mActivity) {

            final Activity innerActivity = mActivity;
            return new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(final Preference preference,
                                                  final Object newValue) {

                    boolean r = true;
                    try {

                        UiUtil.updateBrightness(innerActivity,
                                Float.parseFloat(newValue.toString()));

                    } catch (Exception e) {

                        r = false;
                    }
                    return r;
                }

            };
        }
    }
}