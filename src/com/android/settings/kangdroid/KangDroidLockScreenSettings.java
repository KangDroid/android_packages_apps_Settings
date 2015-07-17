/*
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

package com.android.settings.kangdroid;

import android.app.admin.DevicePolicyManager;
import android.app.ActivityManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.content.Context;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.provider.SearchIndexableResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.util.Helpers;

public class KangDroidLockScreenSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
	
	private static final String CARRIERLABEL_ON_LOCKSCREEN="lock_screen_hide_carrier";
	private SwitchPreference mCarrierLabelOnLockScreen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.kangdroid_lockscreen_settings);
		PreferenceScreen prefSet = getPreferenceScreen();
		ContentResolver resolver = getActivity().getContentResolver();
		
        //CarrierLabel on LockScreen
        mCarrierLabelOnLockScreen = (SwitchPreference) findPreference(CARRIERLABEL_ON_LOCKSCREEN);
        if (!Utils.isWifiOnly(getActivity())) {
            mCarrierLabelOnLockScreen.setOnPreferenceChangeListener(this);

            boolean hideCarrierLabelOnLS = Settings.System.getInt(
                    getActivity().getContentResolver(),
                    Settings.System.LOCK_SCREEN_HIDE_CARRIER, 0) == 1;
            mCarrierLabelOnLockScreen.setChecked(hideCarrierLabelOnLS);
        } else {
            prefSet.removePreference(mCarrierLabelOnLockScreen);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        final String key = preference.getKey();
        if (preference == mCarrierLabelOnLockScreen) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.LOCK_SCREEN_HIDE_CARRIER,
                    (Boolean) objValue ? 1 : 0);
            Helpers.restartSystemUI();
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
 		return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
