/*
 * Copyright (C) 2015 The CyanogenMod project
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

package com.android.settings.notification;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.crdroid.SeekBarPreference;

import java.util.ArrayList;
import java.util.List;

public class NotificationManagerSettings extends SettingsPreferenceFragment
        implements Indexable {

    private static final String TAG = NotificationManagerSettings.class.getSimpleName();

    private static final String KEY_LOCK_SCREEN_NOTIFICATIONS = "lock_screen_notifications";
    private static final String PREF_HEADS_UP_GLOBAL_SWITCH = "heads_up_global_switch";
    private static final String PREF_HEADS_UP_SNOOZE_TIME = "heads_up_snooze_time";
    private static final String PREF_HEADS_UP_TIME_OUT = "heads_up_time_out";

    private boolean mSecure;
    private int mLockscreenSelectedValue;
    private DropDownPreference mLockscreen;

    private ListPreference mHeadsUpGlobalSwitch;
    private SeekBarPreference mHeadsUpSnoozeTime;
    private SeekBarPreference mHeadsUpTimeOut;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.notification_manager_settings);
        mSecure = new LockPatternUtils(getActivity()).isSecure();
        initLockscreenNotifications();

        Resources systemUiResources;
        try {
            systemUiResources =
                    getPackageManager().getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            return;
        }

        mHeadsUpSnoozeTime = (SeekBarPreference) findPreference(PREF_HEADS_UP_SNOOZE_TIME);
        mHeadsUpSnoozeTime.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int headsUpSnoozeTime = (Integer) newValue;
                return Settings.System.putInt(getContentResolver(),
                        Settings.System.HEADS_UP_SNOOZE_TIME,
                        headsUpSnoozeTime * 60 * 1000);
            }
        });
        final int defaultSnoozeTime = systemUiResources.getInteger(systemUiResources.getIdentifier(
                    "com.android.systemui:integer/heads_up_snooze_time", null, null));
        final int headsUpSnoozeTime = Settings.System.getInt(getContentResolver(),
                Settings.System.HEADS_UP_SNOOZE_TIME, defaultSnoozeTime);
        mHeadsUpSnoozeTime.setValue(headsUpSnoozeTime / 60 / 1000);

        mHeadsUpTimeOut = (SeekBarPreference) findPreference(PREF_HEADS_UP_TIME_OUT);
        mHeadsUpTimeOut.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int headsUpTimeOut = (Integer) newValue;
                return Settings.System.putInt(getContentResolver(),
                        Settings.System.HEADS_UP_NOTIFCATION_DECAY,
                        headsUpTimeOut * 1000);
            }
        });
        final int defaultTimeOut = systemUiResources.getInteger(systemUiResources.getIdentifier(
                    "com.android.systemui:integer/heads_up_notification_decay", null, null));
        final int headsUpTimeOut = Settings.System.getInt(getContentResolver(),
                Settings.System.HEADS_UP_NOTIFCATION_DECAY, defaultTimeOut);
        mHeadsUpTimeOut.setValue(headsUpTimeOut / 1000);

        mHeadsUpGlobalSwitch = (ListPreference) findPreference(PREF_HEADS_UP_GLOBAL_SWITCH);
        mHeadsUpGlobalSwitch.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int headsUpGlobalSwitch = Integer.valueOf((String) newValue);
                updateHeadsUpGlobalSwitchSummary(headsUpGlobalSwitch);
                return Settings.System.putInt(getContentResolver(),
                        Settings.System.HEADS_UP_GLOBAL_SWITCH,
                        headsUpGlobalSwitch);
            }
        });
        final int headsUpGlobalSwitch = Settings.System.getInt(getContentResolver(),
                Settings.System.HEADS_UP_GLOBAL_SWITCH, 1);
        mHeadsUpGlobalSwitch.setValue(String.valueOf(headsUpGlobalSwitch));
        updateHeadsUpGlobalSwitchSummary(headsUpGlobalSwitch);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // === Lockscreen (public / private) notifications ===

    private void initLockscreenNotifications() {
        mLockscreen = (DropDownPreference) findPreference(KEY_LOCK_SCREEN_NOTIFICATIONS);
        if (mLockscreen == null) {
            Log.i(TAG, "Preference not found: " + KEY_LOCK_SCREEN_NOTIFICATIONS);
            return;
        }

        mLockscreen.addItem(R.string.lock_screen_notifications_summary_show,
                R.string.lock_screen_notifications_summary_show);
        if (mSecure) {
            mLockscreen.addItem(R.string.lock_screen_notifications_summary_hide,
                    R.string.lock_screen_notifications_summary_hide);
        }
        mLockscreen.addItem(R.string.lock_screen_notifications_summary_disable,
                R.string.lock_screen_notifications_summary_disable);
        updateLockscreenNotifications();
        mLockscreen.setCallback(new DropDownPreference.Callback() {
            @Override
            public boolean onItemSelected(int pos, Object value) {
                final int val = (Integer) value;
                if (val == mLockscreenSelectedValue) {
                    return true;
                }
                final boolean enabled = val != R.string.lock_screen_notifications_summary_disable;
                final boolean show = val == R.string.lock_screen_notifications_summary_show;
                Settings.Secure.putInt(getContentResolver(),
                        Settings.Secure.LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS, show ? 1 : 0);
                Settings.Secure.putInt(getContentResolver(),
                        Settings.Secure.LOCK_SCREEN_SHOW_NOTIFICATIONS, enabled ? 1 : 0);
                mLockscreenSelectedValue = val;
                return true;
            }
        });
    }

    private void updateLockscreenNotifications() {
        if (mLockscreen == null) {
            return;
        }
        final boolean enabled = getLockscreenNotificationsEnabled();
        final boolean allowPrivate = !mSecure || getLockscreenAllowPrivateNotifications();
        mLockscreenSelectedValue = !enabled ? R.string.lock_screen_notifications_summary_disable :
                allowPrivate ? R.string.lock_screen_notifications_summary_show :
                        R.string.lock_screen_notifications_summary_hide;
        mLockscreen.setSelectedValue(mLockscreenSelectedValue);
    }


    private boolean getLockscreenNotificationsEnabled() {
        return Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.LOCK_SCREEN_SHOW_NOTIFICATIONS, 0) != 0;
    }

    private boolean getLockscreenAllowPrivateNotifications() {
        return Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.LOCK_SCREEN_ALLOW_PRIVATE_NOTIFICATIONS, 0) != 0;
    }

    private void updateHeadsUpGlobalSwitchSummary(int value) {
        String summary;
        switch (value) {
            case 0:     summary = getResources().getString(
                                    R.string.heads_up_global_switch_summary_disabled);
                        mHeadsUpSnoozeTime.setEnabled(false);
                        mHeadsUpTimeOut.setEnabled(false);
                        break;
            case 1:     summary = getResources().getString(
                                    R.string.heads_up_global_switch_summary_perapp);
                        mHeadsUpSnoozeTime.setEnabled(true);
                        mHeadsUpTimeOut.setEnabled(true);
                        break;
            case 2:     summary = getResources().getString(
                                    R.string.heads_up_global_switch_summary_forced);
                        mHeadsUpSnoozeTime.setEnabled(true);
                        mHeadsUpTimeOut.setEnabled(true);
                        break;
            default:    summary = "";
                        break;
        }
        mHeadsUpGlobalSwitch.setSummary(summary);
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                            boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.notification_manager_settings;
                    result.add(sir);

                    return result;
                }
            };
}
