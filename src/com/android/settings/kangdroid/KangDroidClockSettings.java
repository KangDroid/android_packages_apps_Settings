/*
 * Copyright (C) 2014-2015 The KangDroid Project
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
package com.android.settings.kangdroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;
import android.content.pm.PackageManager;
import android.util.Log;
import com.android.internal.util.crdroid.DeviceUtils;
import android.preference.SwitchPreference;
import java.util.Locale;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.EditText;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreferenceCham;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import com.android.settings.crdroid.SeekBarPreference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class KangDroidClockSettings extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {
        	
	private static final String TAG = "KangDroidClockSettings";
    private static final String STATUS_BAR_CLOCK_STYLE = "status_bar_clock";
    private static final String STATUS_BAR_AM_PM = "status_bar_am_pm";
    private static final String STATUS_BAR_DATE = "status_bar_date";
    private static final String STATUS_BAR_DATE_STYLE = "status_bar_date_style";
    private static final String STATUS_BAR_DATE_FORMAT = "status_bar_date_format";
    private static final String PREF_FONT_STYLE = "font_style";
	private static final String PREF_FONT_SIZE  = "font_size";
	private static final String CLOCK_USE_SECOND = "clock_use_second";
	private static final String PREF_COLOR_PICKER = "clock_color";
	
    public static final int CLOCK_DATE_STYLE_LOWERCASE = 1;
    public static final int CLOCK_DATE_STYLE_UPPERCASE = 2;
    private static final int CUSTOM_CLOCK_DATE_FORMAT_INDEX = 18;
	
    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;
	
    private ListPreference mStatusBarClock;
    private ListPreference mStatusBarAmPm;
    private ListPreference mStatusBarDate;
    private ListPreference mStatusBarDateStyle;
    private ListPreference mStatusBarDateFormat;
	private ListPreference mFontStyle;
	private SwitchPreference mClockUseSecond;
	private ColorPickerPreference mColorPicker; 
	private boolean mCheckPreferences;
	private SeekBarPreference mStatusBarDateSize;
	
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
		createCustomView();
    }
	
    @Override
    public void onResume() {
        super.onResume();
        // Adjust clock position for RTL if necessary
        Configuration config = getResources().getConfiguration();
        if (config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                mStatusBarClock.setEntries(getActivity().getResources().getStringArray(
                        R.array.status_bar_clock_style_entries_rtl));
                mStatusBarClock.setSummary(mStatusBarClock.getEntry());
        }
    }
	
   @Override
   public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
   	final ContentResolver resolver = getActivity().getContentResolver();
   	boolean value;
    if (preference == mClockUseSecond) {
        value = mClockUseSecond.isChecked();
        Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.CLOCK_USE_SECOND, value ? 1 : 0);
        return true;
    }
	return super.onPreferenceTreeClick(preferenceScreen, preference);
   }
   
   @Override
   public boolean onPreferenceChange(Preference preference, Object newValue) {
       AlertDialog dialog;
       ContentResolver resolver = getActivity().getContentResolver();
	   
       if (preference == mStatusBarClock) {
           int clockStyle = Integer.parseInt((String) newValue);
           int index = mStatusBarClock.findIndexOfValue((String) newValue);
           Settings.System.putInt(
                   resolver, STATUS_BAR_CLOCK_STYLE, clockStyle);
           mStatusBarClock.setSummary(mStatusBarClock.getEntries()[index]);
           return true;
       } else if (preference == mColorPicker) {
           String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                   .valueOf(newValue)));
           preference.setSummary(hex);
           int intHex = ColorPickerPreference.convertToColorInt(hex);
           Settings.System.putInt(getActivity().getContentResolver(),
                   Settings.System.STATUSBAR_CLOCK_COLOR, intHex);
           return true;
       } else if (preference == mStatusBarAmPm) {
           int statusBarAmPm = Integer.valueOf((String) newValue);
           int index = mStatusBarAmPm.findIndexOfValue((String) newValue);
           Settings.System.putInt(
                   resolver, STATUS_BAR_AM_PM, statusBarAmPm);
           mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntries()[index]);
           return true;
       } else if (preference == mStatusBarDate) {
           int statusBarDate = Integer.valueOf((String) newValue);
           int index = mStatusBarDate.findIndexOfValue((String) newValue);
           Settings.System.putInt(
                   resolver, STATUS_BAR_DATE, statusBarDate);
           mStatusBarDate.setSummary(mStatusBarDate.getEntries()[index]);
           return true;
       } else if (preference == mStatusBarDateStyle) {
           int statusBarDateStyle = Integer.parseInt((String) newValue);
           int index = mStatusBarDateStyle.findIndexOfValue((String) newValue);
           Settings.System.putInt(
                   resolver, STATUS_BAR_DATE_STYLE, statusBarDateStyle);
           mStatusBarDateStyle.setSummary(mStatusBarDateStyle.getEntries()[index]);
           return true;
       } else if (preference ==  mStatusBarDateFormat) {
           int index = mStatusBarDateFormat.findIndexOfValue((String) newValue);
           if (index == CUSTOM_CLOCK_DATE_FORMAT_INDEX) {
               AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
               alert.setTitle(R.string.status_bar_date_string_edittext_title);
               alert.setMessage(R.string.status_bar_date_string_edittext_summary);

               final EditText input = new EditText(getActivity());
               String oldText = Settings.System.getString(
                   getActivity().getContentResolver(),
                   Settings.System.STATUS_BAR_DATE_FORMAT);
               if (oldText != null) {
                   input.setText(oldText);
               }
               alert.setView(input);

               alert.setPositiveButton(R.string.menu_save, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialogInterface, int whichButton) {
                       String value = input.getText().toString();
                       if (value.equals("")) {
                           return;
                       }
                       Settings.System.putString(getActivity().getContentResolver(),
                           Settings.System.STATUS_BAR_DATE_FORMAT, value);

                       return;
                   }
               });

               alert.setNegativeButton(R.string.menu_cancel,
                   new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialogInterface, int which) {
                       return;
                   }
               });
               dialog = alert.create();
               dialog.show();
           } else {
               if ((String) newValue != null) {
                   Settings.System.putString(getActivity().getContentResolver(),
                       Settings.System.STATUS_BAR_DATE_FORMAT, (String) newValue);
               }
           }
           return true;
       } else if (preference == mFontStyle) {
           int val = Integer.parseInt((String) newValue);
           int index = mFontStyle.findIndexOfValue((String) newValue);
           Settings.System.putInt(getActivity().getContentResolver(),
                   Settings.System.STATUSBAR_CLOCK_FONT_STYLE, val);
           mFontStyle.setSummary(mFontStyle.getEntries()[index]);
           return true;
        } else if (preference == mStatusBarDateSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.STATUSBAR_CLOCK_FONT_SIZE, width);
            return true;
       }
	   return false;
   }
	
	private PreferenceScreen createCustomView() {
		addPreferencesFromResource(R.xml.kangdroid_clock_settings);
		
        ContentResolver resolver = getActivity().getContentResolver();
		PreferenceScreen prefSet = getPreferenceScreen();
        PackageManager pm = getPackageManager();
        Resources systemUiResources;
        try {
            systemUiResources = pm.getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            Log.e(TAG, "can't access systemui resources",e);
            return null;
        }
        mStatusBarClock = (ListPreference) findPreference(STATUS_BAR_CLOCK_STYLE);
        mStatusBarAmPm = (ListPreference) findPreference(STATUS_BAR_AM_PM);
        mStatusBarDate = (ListPreference) findPreference(STATUS_BAR_DATE);
        mStatusBarDateStyle = (ListPreference) findPreference(STATUS_BAR_DATE_STYLE);
        mStatusBarDateFormat = (ListPreference) findPreference(STATUS_BAR_DATE_FORMAT);
		
        int clockStyle = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_CLOCK, 1);
        mStatusBarClock.setValue(String.valueOf(clockStyle));
        mStatusBarClock.setSummary(mStatusBarClock.getEntry());
        mStatusBarClock.setOnPreferenceChangeListener(this);
		
        if (DateFormat.is24HourFormat(getActivity())) {
            mStatusBarAmPm.setEnabled(false);
            mStatusBarAmPm.setSummary(R.string.status_bar_am_pm_info);
        } else {
            int statusBarAmPm = Settings.System.getInt(resolver,
                    Settings.System.STATUS_BAR_AM_PM, 2);
            mStatusBarAmPm.setValue(String.valueOf(statusBarAmPm));
            mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntry());
            mStatusBarAmPm.setOnPreferenceChangeListener(this);
        }
		
        int showDate = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_DATE, 0);
        mStatusBarDate.setValue(String.valueOf(showDate));
        mStatusBarDate.setSummary(mStatusBarDate.getEntry());
        mStatusBarDate.setOnPreferenceChangeListener(this);

        int dateStyle = Settings.System.getInt(resolver,
                Settings.System.STATUS_BAR_DATE_STYLE, 0);
        mStatusBarDateStyle.setValue(String.valueOf(dateStyle));
        mStatusBarDateStyle.setSummary(mStatusBarDateStyle.getEntry());
        mStatusBarDateStyle.setOnPreferenceChangeListener(this);

        mStatusBarDateFormat.setOnPreferenceChangeListener(this);
        mStatusBarDateFormat.setSummary(mStatusBarDateFormat.getEntry());
        if (mStatusBarDateFormat.getValue() == null) {
            mStatusBarDateFormat.setValue("EEE");
        }

        parseClockDateFormats();
		
        mFontStyle = (ListPreference) findPreference(PREF_FONT_STYLE);
        mFontStyle.setOnPreferenceChangeListener(this);
        mFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUSBAR_CLOCK_FONT_STYLE,
                5)));
        mFontStyle.setSummary(mFontStyle.getEntry());
		
        mClockUseSecond = (SwitchPreference) prefSet.findPreference(CLOCK_USE_SECOND);
        mClockUseSecond.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
               Settings.System.CLOCK_USE_SECOND, 0) == 1));
        mColorPicker = (ColorPickerPreference) findPreference(PREF_COLOR_PICKER);
        mColorPicker.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_CLOCK_COLOR, -2);
        if (intColor == -2) {
            intColor = systemUiResources.getColor(systemUiResources.getIdentifier(
                    "com.android.systemui:color/status_bar_clock_color", null, null));
            mColorPicker.setSummary(getResources().getString(R.string.default_string));
        } else {
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mColorPicker.setSummary(hexColor);
        }
        mColorPicker.setNewPreviewColor(intColor);
		
        mStatusBarDateSize = (SeekBarPreference) findPreference(PREF_FONT_SIZE);
        mStatusBarDateSize.setValue(Settings.System.getInt(resolver,
                Settings.System.STATUSBAR_CLOCK_FONT_SIZE, 14));
        mStatusBarDateSize.setOnPreferenceChangeListener(this);
		
        setHasOptionsMenu(true);
        mCheckPreferences = true;
        return prefSet;
	}
	
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_reset_teal)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }
	
    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }
	
    private void parseClockDateFormats() {
        // Parse and repopulate mClockDateFormats's entries based on current date.
        String[] dateEntries = getResources().getStringArray(R.array.status_bar_date_format_entries_values);
        CharSequence parsedDateEntries[];
        parsedDateEntries = new String[dateEntries.length];
        Date now = new Date();

        int lastEntry = dateEntries.length - 1;
        int dateFormat = Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_DATE_STYLE, 0);
        for (int i = 0; i < dateEntries.length; i++) {
            if (i == lastEntry) {
                parsedDateEntries[i] = dateEntries[i];
            } else {
                String newDate;
                CharSequence dateString = DateFormat.format(dateEntries[i], now);
                if (dateFormat == CLOCK_DATE_STYLE_LOWERCASE) {
                    newDate = dateString.toString().toLowerCase();
                } else if (dateFormat == CLOCK_DATE_STYLE_UPPERCASE) {
                    newDate = dateString.toString().toUpperCase();
                } else {
                    newDate = dateString.toString();
                }

                parsedDateEntries[i] = newDate;
            }
        }
        mStatusBarDateFormat.setEntries(parsedDateEntries);
    }
	
    private void enableStatusBarClockDependents() {
        int clockStyle = Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.STATUS_BAR_CLOCK, 1);
        if (clockStyle == 0) {
            mStatusBarDate.setEnabled(false);
            mStatusBarDateStyle.setEnabled(false);
			mStatusBarDateSize.setEnabled(false);
            mStatusBarDateFormat.setEnabled(false);
        } else {
            mStatusBarDate.setEnabled(true);
            mStatusBarDateStyle.setEnabled(true);
			mStatusBarDateSize.setEnabled(true);
            mStatusBarDateFormat.setEnabled(true);
        }
    }
	
    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        KangDroidClockSettings getOwner() {
            return (KangDroidClockSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.status_bar_clock_style_reset_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.dlg_ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getActivity().getContentResolver(),
                                Settings.System.STATUSBAR_CLOCK_COLOR, -2);
                            getOwner().createCustomView();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }
			
}
