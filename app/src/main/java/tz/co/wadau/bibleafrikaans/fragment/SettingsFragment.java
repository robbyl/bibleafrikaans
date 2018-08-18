package tz.co.wadau.bibleafrikaans.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import tz.co.wadau.bibleafrikaans.R;
import tz.co.wadau.bibleafrikaans.alarm.AlarmNotification;
import tz.co.wadau.bibleafrikaans.customviews.TimePreference;
import tz.co.wadau.bibleafrikaans.customviews.TimePreferenceDialogFragmentCompat;
import tz.co.wadau.bibleafrikaans.data.DbHelper;

public class SettingsFragment extends PreferenceFragmentCompat {

    public String TAG = SettingsFragment.class.getSimpleName();
    public static final String KEY_PREFS_TEXT_SIZE = "prefs_text_size";
    public static final String KEY_PREFS_AUTO_SCROLL_SPEED = "prefs_auto_scroll_speed";
    public static final String KEY_PREFS_NIGHT_MODE = "prefs_night_mode";
    public static final String KEY_PREFS_STAY_AWAKE = "prefs_stay_awake";
    public static final String KEY_PREFS_TYPE_FACE = "prefs_typeface";
    public static final String KEY_PREFS_CLEAR_HIGHLIGHTS = "prefs_clear_all_highlights";
    public static final String KEY_PREFS_DAILY_VERSES = "prefs_daily_verses";
    public static final String KEY_PREFS_VERSE_NOTFN_TIME = "prefs_verse_notification_time";
    private Context context;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        context = getContext();
        bindPreferenceSummaryToValue(findPreference(KEY_PREFS_TYPE_FACE));
        bindPreferenceSummaryToValue(findPreference(KEY_PREFS_VERSE_NOTFN_TIME));
        Preference clearHighlightsPreference = findPreference(KEY_PREFS_CLEAR_HIGHLIGHTS);
        clearHighlightsPreference.setOnPreferenceClickListener(preferenceClickListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            switch (key) {
                case KEY_PREFS_TYPE_FACE:
                    bindPreferenceSummaryToValue(findPreference(KEY_PREFS_TYPE_FACE));
                    break;
                case KEY_PREFS_NIGHT_MODE:
                    getActivity().recreate();
                    break;
                case KEY_PREFS_DAILY_VERSES:
                    setupDailyVerseNotification(context);
                    break;
                case KEY_PREFS_VERSE_NOTFN_TIME:
                    bindPreferenceSummaryToValue(findPreference(KEY_PREFS_VERSE_NOTFN_TIME));
                    setupDailyVerseNotification(context);
                    break;
            }
        }
    };

    Preference.OnPreferenceClickListener preferenceClickListener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            clearVerseHighlights();
            return true;
        }
    };

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment fragment;
        if (preference instanceof TimePreference) {
            fragment = TimePreferenceDialogFragmentCompat.newInstance(preference);
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(),
                    "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else super.onDisplayPreferenceDialog(preference);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        if(preference instanceof TimePreference){
            preference.setSummary(sharedPreferences.getString(preference.getKey(), "06:00"));
        }else {
            preference.setSummary(sharedPreferences.getString(preference.getKey(), ""));
        }

    }

    private void clearVerseHighlights() {
        DbHelper dbHelper = new DbHelper(context);
        dbHelper.clearAllVerseHighlights();
        Toast.makeText(context, R.string.verse_highlights_cleared, Toast.LENGTH_SHORT).show();
    }

    private void setupDailyVerseNotification(Context context){
        AlarmNotification notification = new AlarmNotification();
        notification.setAlarm(context);
    }
}
