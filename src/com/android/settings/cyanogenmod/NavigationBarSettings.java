package com.android.settings.cyanogenmod;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NavigationBarSettings extends SettingsPreferenceFragment implements
OnPreferenceChangeListener {

    private static final String KEY_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
	private static final String KEY_NAVIGATION_BAR_WIDTH = "navigation_bar_width";

    private ListPreference mNavigationBarHeight;
	private ListPreference mNavigationBarWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navigation_bar_settings);

        mNavigationBarHeight = (ListPreference) findPreference(KEY_NAVIGATION_BAR_HEIGHT);
        mNavigationBarHeight.setOnPreferenceChangeListener(this);
        int statusNavigationBarHeight = Settings.System.getInt(getActivity().getApplicationContext()
                .getContentResolver(),
                Settings.System.NAVIGATION_BAR_HEIGHT, 48);
        mNavigationBarHeight.setValue(String.valueOf(statusNavigationBarHeight));
        mNavigationBarHeight.setSummary(mNavigationBarHeight.getEntry());
		
        mNavigationBarWidth = (ListPreference) findPreference(KEY_NAVIGATION_BAR_WIDTH);
        mNavigationBarWidth.setOnPreferenceChangeListener(this);
        int statusNavigationBarWidth = Settings.System.getInt(getActivity().getApplicationContext()
                .getContentResolver(),
                Settings.System.NAVIGATION_BAR_WIDTH, 48);
        mNavigationBarWidth.setValue(String.valueOf(statusNavigationBarWidth));
        mNavigationBarWidth.setSummary(mNavigationBarWidth.getEntry());
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mNavigationBarHeight) {
            int statusNavigationBarHeight = Integer.valueOf((String) objValue);
            int index = mNavigationBarHeight.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT, statusNavigationBarHeight);
            mNavigationBarHeight.setSummary(mNavigationBarHeight.getEntries()[index]);
        } else if (preference == mNavigationBarWidth) {
            int statusNavigationBarWidth = Integer.valueOf((String) objValue);
            int index = mNavigationBarWidth.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_WIDTH, statusNavigationBarWidth);
            mNavigationBarWidth.setSummary(mNavigationBarWidth.getEntries()[index]);
		}
        return true;
    }
}
