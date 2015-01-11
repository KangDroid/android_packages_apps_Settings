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
import com.android.internal.util.crdroid.DeviceUtils;
import android.preference.SwitchPreference;
import java.util.Locale;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class KangDroidSettings extends SettingsPreferenceFragment {

	private static final String KEY_CLEAR_ALL_RECENTS_NAVBAR_ENABLED = "clear_all_recents_navbar_enabled";
	private SwitchPreference mClearAllRecentsNavbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.kangdroid_settings);

	PreferenceScreen prefSet = getPreferenceScreen();
	ContentResolver resolver = getActivity().getContentResolver();
		
		// Recent Navigation Bar button
		mClearAllRecentsNavbar = (SwitchPreference) prefSet.findPreference(KEY_CLEAR_ALL_RECENTS_NAVBAR_ENABLED);
        	mClearAllRecentsNavbar.setChecked(Settings.System.getInt(resolver,
            			Settings.System.CLEAR_ALL_RECENTS_NAVBAR_ENABLED, 1) == 1);	//End of Recent navigation bar button
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    
   @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mClearAllRecentsNavbar) {
            Settings.System.putInt(resolver, Settings.System.CLEAR_ALL_RECENTS_NAVBAR_ENABLED,
                    mClearAllRecentsNavbar.isChecked() ? 1 : 0);
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return true;
    }
}
