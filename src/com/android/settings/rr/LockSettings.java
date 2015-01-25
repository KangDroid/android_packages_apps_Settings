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

package com.android.settings.rr;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.content.ContentResolver;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference.OnPreferenceChangeListener;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.SwitchPreference;
import com.android.settings.util.Helpers;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class LockSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {
	
	private static final String KEY_LOCKSCREEN_CAMERA_WIDGET_HIDE = "camera_widget_hide";
	private static final String KEY_LOCKSCREEN_DIALER_WIDGET_HIDE = "dialer_widget_hide";
	private static final String KEY_LOCKSCREEN_WEATHER = "lockscreen_weather";
	
	private SwitchPreference mCameraWidgetHide;
	private SwitchPreference mDialerHideWidget;
	private PreferenceScreen mLockScreen;
	private SwitchPreference mLockscreenWeather;
	
	private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.rr_lockscreen_settings);
		
        PreferenceScreen prefSet = getPreferenceScreen();
        PackageManager pm = getPackageManager();
        Resources res = getResources();
		mContext = getActivity();
		ContentResolver resolver = getActivity().getContentResolver();

		mLockScreen = (PreferenceScreen) findPreference("kangdroid_lockscreen_settings");
		
        // Camera widget hide
        mCameraWidgetHide = (SwitchPreference) findPreference("camera_widget_hide");
        boolean mCameraDisabled = false;
        DevicePolicyManager dpm =
            (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null) {
            mCameraDisabled = dpm.getCameraDisabled(null);
        }
        if (mCameraDisabled){
            mLockScreen.removePreference(mCameraWidgetHide);
        }
		
        // Lockscreen weather
        mLockscreenWeather = (SwitchPreference) findPreference(KEY_LOCKSCREEN_WEATHER);
        mLockscreenWeather.setChecked(Settings.System.getIntForUser(resolver,
                Settings.System.LOCKSCREEN_WEATHER, 1, UserHandle.USER_CURRENT) == 1);
        mLockscreenWeather.setOnPreferenceChangeListener(this);
		
		// Dialer Widget Hide (WIP)
		mDialerHideWidget = (SwitchPreference) findPreference(KEY_LOCKSCREEN_DIALER_WIDGET_HIDE);
		mDialerHideWidget.setChecked(Settings.System.getIntForUser(resolver,
                Settings.System.DIALER_WIDGET_HIDE, 0, UserHandle.USER_CURRENT) == 1);
		mDialerHideWidget.setOnPreferenceChangeListener(this);
		
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
    }


    public boolean onPreferenceChange(Preference preference, Object objValue) {
		if (preference == mLockscreenWeather) {
        boolean value = (Boolean) objValue;
        Settings.System.putIntForUser(getActivity().getContentResolver(),
                Settings.System.LOCKSCREEN_WEATHER, value ? 1 : 0, UserHandle.USER_CURRENT);
        Helpers.restartSystemUI();
		return true;
	} else if (preference == mDialerHideWidget) {
        boolean value = (Boolean) objValue;
        Settings.System.putIntForUser(getActivity().getContentResolver(),
                Settings.System.DIALER_WIDGET_HIDE, value ? 1 : 0, UserHandle.USER_CURRENT);
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