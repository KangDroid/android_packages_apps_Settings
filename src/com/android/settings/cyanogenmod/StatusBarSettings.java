/*
* Copyright (C) 2014 The CyanogenMod Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
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

public class StatusBarSettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

	private static final String TAG = "CustomSettings";

    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";
    private static final String STATUS_BAR_BATTERY_STYLE_HIDDEN = "4";
    private static final String STATUS_BAR_BATTERY_STYLE_TEXT = "6";
	
	private static final String KEY_STATUS_BAR_TICKER = "status_bar_ticker_enabled";
	private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
	
	private ListPreference mSmartPulldown;
	private SwitchPreference mTicker;

    private ListPreference mStatusBarBattery;
    private ListPreference mStatusBarBatteryShowPercent;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.status_bar_settings);
		
        PackageManager pm = getPackageManager();
        Resources systemUiResources;
		PreferenceScreen prefSet = getPreferenceScreen();
        try {
            systemUiResources = pm.getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            Log.e(TAG, "can't access systemui resources",e);
            return;
        }

        ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarBattery = (ListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        mStatusBarBatteryShowPercent =
                (ListPreference) findPreference(STATUS_BAR_SHOW_BATTERY_PERCENT);

        int batteryStyle = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, 0);
        mStatusBarBattery.setValue(String.valueOf(batteryStyle));
        mStatusBarBattery.setSummary(mStatusBarBattery.getEntry());
        mStatusBarBattery.setOnPreferenceChangeListener(this);

        int batteryShowPercent = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0);
        mStatusBarBatteryShowPercent.setValue(String.valueOf(batteryShowPercent));
        mStatusBarBatteryShowPercent.setSummary(mStatusBarBatteryShowPercent.getEntry());
        mStatusBarBatteryShowPercent.setOnPreferenceChangeListener(this);

        mTicker = (SwitchPreference) prefSet.findPreference(KEY_STATUS_BAR_TICKER);
        final boolean tickerEnabled = systemUiResources.getBoolean(systemUiResources.getIdentifier(
                    "com.android.systemui:bool/enable_ticker", null, null));
        mTicker.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_TICKER_ENABLED, tickerEnabled ? 1 : 0) == 1);
        mTicker.setOnPreferenceChangeListener(this);
		
        mHideLabels = (ListPreference) findPreference(PREF_NOTIFICATION_HIDE_LABELS);
        int hideCarrier = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATION_HIDE_LABELS, 0);
        mHideLabels.setValue(String.valueOf(hideCarrier));
        mHideLabels.setOnPreferenceChangeListener(this);
        updateHideNotificationLabelsSummary(hideCarrier);

        if (!DeviceUtils.isPhone(getActivity())) {
            // Nothing for tablets and large screen devices which doesn't show
            // information in notification drawer.....remove option
            prefs.removePreference(mHideLabels);
        }
		
		mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
		if (!DeviceUtils.isPhone(getActivity())) {
			prefSet.removePreference(mSmartPulldown);
		} else {
            // Smart Pulldown
            mSmartPulldown.setOnPreferenceChangeListener(this);
            int smartPulldown = Settings.System.getInt(getContentResolver(),
                    Settings.System.QS_SMART_PULLDOWN, 0);
            mSmartPulldown.setValue(String.valueOf(smartPulldown));
            updateSmartPulldownSummary(smartPulldown);
		}
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mStatusBarBattery) {
            int batteryStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarBattery.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.STATUS_BAR_BATTERY_STYLE, batteryStyle);
            mStatusBarBattery.setSummary(mStatusBarBattery.getEntries()[index]);

            enableStatusBarBatteryDependents((String) newValue);
            return true;
        } else if (preference == mStatusBarBatteryShowPercent) {
            int batteryShowPercent = Integer.valueOf((String) newValue);
            int index = mStatusBarBatteryShowPercent.findIndexOfValue((String) newValue);
            Settings.System.putInt(
                    resolver, Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, batteryShowPercent);
            mStatusBarBatteryShowPercent.setSummary(
                    mStatusBarBatteryShowPercent.getEntries()[index]);
            return true;
        } else if (preference == mTicker) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.STATUS_BAR_TICKER_ENABLED,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mSmartPulldown) {
            int smartPulldown = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QS_SMART_PULLDOWN,
                    smartPulldown);
            updateSmartPulldownSummary(smartPulldown);
            return true;
	     } else if (preference == mHideLabels) {
	            int hideLabels = Integer.valueOf((String) newValue);
	            Settings.System.putInt(getContentResolver(), Settings.System.NOTIFICATION_HIDE_LABELS,
	                    hideLabels);
	            updateHideNotificationLabelsSummary(hideLabels);
	            return true;
      }
        return false;
    }
	
    private void updateHideNotificationLabelsSummary(int value) {
        Resources res = getResources();
        StringBuilder text = new StringBuilder();
        switch (value) {
            case 1:
                text.append(res.getString(R.string.notification_hide_labels_carrier));
                break;
            case 2:
                text.append(res.getString(R.string.notification_hide_labels_wifi));
                break;
            case 3:
                text.append(res.getString(R.string.notification_hide_labels_all));
                break;
            default:
                text.append(res.getString(R.string.notification_hide_labels_disable));
                break;
        }
        text.append(" " + res.getString(R.string.notification_hide_labels_text));
        mHideLabels.setSummary(text.toString());
    }

    private void enableStatusBarBatteryDependents(String value) {
        boolean enabled = !(value.equals(STATUS_BAR_BATTERY_STYLE_TEXT)
                || value.equals(STATUS_BAR_BATTERY_STYLE_HIDDEN));
        mStatusBarBatteryShowPercent.setEnabled(enabled);
    }
	
    private void updateSmartPulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // Smart pulldown deactivated
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_off));
        } else {
            String type = null;
            switch (value) {
                case 1:
                    type = res.getString(R.string.smart_pulldown_dismissable);
                    break;
                case 2:
                    type = res.getString(R.string.smart_pulldown_persistent);
                    break;
                default:
                    type = res.getString(R.string.smart_pulldown_all);
                    break;
            }
            // Remove title capitalized formatting
            type = type.toLowerCase();
            mSmartPulldown.setSummary(res.getString(R.string.smart_pulldown_summary, type));
        }
	}
}
