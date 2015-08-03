/*
 * Copyright (C) 2014 The KangDroid Project
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

package com.android.settings.kangdroid;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.provider.SearchIndexableResource;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.util.Helpers;
import com.android.settings.util.CMDProcessor;

import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedReader;

public class KangDroidMiscSettings extends SettingsPreferenceFragment implements Indexable, Preference.OnPreferenceChangeListener {
	
	private static final String SELINUX_SETTING = "selinux";
	static final String ENFORCING_SELINUX = "Enforcing";
	static final String PERMISSIVE_SELINUX = "Permissive";
	private static final String RESTART_SYSTEMUI = "restart_systemui";
	
	private SwitchPreference mSelinux;
	private Preference mRestartSystemUI;
	String mSelinuxValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.kangdroid_misc_settings);
		PreferenceScreen prefSet = getPreferenceScreen();
		mSelinuxValue = CMDProcessor.startSuCommand("getenforce").getStdout();
		
		mRestartSystemUI = prefSet.findPreference(RESTART_SYSTEMUI);
		
        mSelinux = (SwitchPreference) prefSet.findPreference(SELINUX_SETTING);
        mSelinux.setOnPreferenceChangeListener(this);

        if (mSelinuxValue == ENFORCING_SELINUX) {
            mSelinux.setChecked(true);
            mSelinux.setSummary(R.string.selinux_enforcing_title);
        } else if (mSelinuxValue != ENFORCING_SELINUX) {
            mSelinux.setChecked(false);
            mSelinux.setSummary(R.string.selinux_permissive_title);
        }
    }
	
    @Override
    public void onResume() {
        super.onResume();
    }
	
    public boolean onPreferenceChange(Preference preference, Object objValue) {
		if (preference == mSelinux) {
		if (objValue != null) {
            if (objValue.toString().equals("true")) {
                CMDProcessor.startSuCommand("setenforce 1");
                mSelinux.setSummary(R.string.selinux_enforcing_title);
            } else if (objValue.toString().equals("false")) {
                CMDProcessor.startSuCommand("setenforce 0");
                mSelinux.setSummary(R.string.selinux_permissive_title);
            }
            return true;
			}
		}
        return false;
    }
	
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference == mRestartSystemUI) {
            Helpers.restartSystemUI();
		} else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
		return false;
    }
}