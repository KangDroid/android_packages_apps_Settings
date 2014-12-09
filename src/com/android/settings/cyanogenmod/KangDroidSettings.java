/*
 * Copyright (C) 2014 The Android Open KangDroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class KangDroidSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

	private static final String KEY_TOAST_ANIMATION = "toast_animation";
	private static final String TAG = "CustomSettings";
	private static final String KEY_STATUS_BAR_TICKER = "status_bar_ticker_enabled";
	private ListPreference mToastAnimation;
	private SwitchPreference mTicker;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.kangdroid_settings);

	PreferenceScreen prefSet = getPreferenceScreen();
        PackageManager pm = getPackageManager();
        Resources systemUiResources;
        try {
            systemUiResources = pm.getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            Log.e(TAG, "can't access systemui resources",e);
            return;
        }

        mToastAnimation = (ListPreference)findPreference(KEY_TOAST_ANIMATION);
        mToastAnimation.setSummary(mToastAnimation.getEntry());
        int CurrentToastAnimation = Settings.System.getInt(getContentResolver(), Settings.System.TOAST_ANIMATION, 1);
        mToastAnimation.setValueIndex(CurrentToastAnimation);
        mToastAnimation.setSummary(mToastAnimation.getEntries()[CurrentToastAnimation]);
        mToastAnimation.setOnPreferenceChangeListener(this);

        mTicker = (SwitchPreference) prefSet.findPreference(KEY_STATUS_BAR_TICKER);
        final boolean tickerEnabled = systemUiResources.getBoolean(systemUiResources.getIdentifier(
                    "com.android.systemui:bool/enable_ticker", null, null));
        mTicker.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_TICKER_ENABLED, tickerEnabled ? 1 : 0) == 1);
        mTicker.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mToastAnimation) {
	         int index = mToastAnimation.findIndexOfValue((String) newValue);
	         Settings.System.putString(getContentResolver(), Settings.System.TOAST_ANIMATION, (String) newValue);
	         mToastAnimation.setSummary(mToastAnimation.getEntries()[index]);
	         Toast.makeText(getActivity(), "Toast Test", Toast.LENGTH_SHORT).show();
	         return true;
        } else if (preference == mTicker) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_TICKER_ENABLED,
                    (Boolean) objValue ? 1 : 0);
            return true;
		}
        return false;
    }
}
