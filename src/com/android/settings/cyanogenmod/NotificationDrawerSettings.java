/*
 * Copyright (C) 2015 The CyanogenMod Project
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
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.cyanogenmod.qs.QSTiles;
import com.android.internal.util.crdroid.DeviceUtils;
import android.provider.Settings;
import java.util.Locale;

public class NotificationDrawerSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
	private static final String PREF_SMART_PULLDOWN = "smart_pulldown";
	private static final String QUICK_PULLDOWN = "quick_pulldown";
	
    private Preference mQSTiles;
	private ListPreference mSmartPulldown;
	private ListPreference mQuickPulldown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.notification_drawer_settings);
		
		PreferenceScreen prefs = getPreferenceScreen();

        mQSTiles = findPreference("qs_order");
		
		// KDP's Addition
		mSmartPulldown = (ListPreference) findPreference(PREF_SMART_PULLDOWN);
		if (!DeviceUtils.isPhone(getActivity())) {
			prefs.removePreference(mSmartPulldown);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PreferenceScreen prefSet = getPreferenceScreen();
        ContentResolver resolver = getActivity().getContentResolver();
        mQuickPulldown = (ListPreference) prefSet.findPreference(QUICK_PULLDOWN);

        mQuickPulldown.setOnPreferenceChangeListener(this);
        int quickPulldownValue = Settings.System.getIntForUser(resolver,
                Settings.System.QS_QUICK_PULLDOWN, 0, UserHandle.USER_CURRENT);
        mQuickPulldown.setValue(String.valueOf(quickPulldownValue));
        updatePulldownSummary(quickPulldownValue);
    }

    @Override
    public void onResume() {
        super.onResume();

        int qsTileCount = QSTiles.determineTileCount(getActivity());
        mQSTiles.setSummary(getResources().getQuantityString(R.plurals.qs_tiles_summary,
                    qsTileCount, qsTileCount));
    }
	
    public boolean onPreferenceChange(Preference preference, Object newValue) {
		ContentResolver resolver = getContentResolver();
		if (preference == mSmartPulldown) {
		             int smartPulldown = Integer.valueOf((String) newValue);
		             Settings.System.putInt(getContentResolver(),
		                     Settings.System.QS_SMART_PULLDOWN,
		                     smartPulldown);
		             updateSmartPulldownSummary(smartPulldown);
		             return true;
 		} else if (preference == mQuickPulldown) {
           int quickPulldownValue = Integer.valueOf((String) newValue);
           Settings.System.putIntForUser(resolver, Settings.System.QS_QUICK_PULLDOWN,
                   quickPulldownValue, UserHandle.USER_CURRENT);
           updatePulldownSummary(quickPulldownValue);
           return true;
		}
        return false;
    }
		
    private void updatePulldownSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            // quick pulldown deactivated
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_off));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.quick_pulldown_summary_left
                    : R.string.quick_pulldown_summary_right);
            mQuickPulldown.setSummary(res.getString(R.string.quick_pulldown_summary, direction));
        }
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