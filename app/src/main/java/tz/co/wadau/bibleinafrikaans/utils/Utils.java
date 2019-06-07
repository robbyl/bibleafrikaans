package tz.co.wadau.bibleinafrikaans.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.fragment.SettingsFragment;

import static tz.co.wadau.bibleinafrikaans.NotesActivity.PREFS_IS_MULTI_COLUMN_VEW;

public class Utils {

    public static boolean isTablet(Context context) {
       return context.getResources().getBoolean(R.bool.isTablet);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatted.format(date);
    }

    public static String formatDateLongFormat(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat mFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return mFormat.format(date);
    }

    public static long getTimeInMills(String dateStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = sdf.parse(dateStr);
        return date.getTime();
    }

    public static Double dateDiffInDays(Long HighMills, Long LowMills) {
        return ((HighMills - LowMills) / (24d * 60d * 60d * 1000d));
    }

    public static String formatToSystemDateFormat(Context context) {
        //Reading system date format
        Format dateFormat = android.text.format.DateFormat.getDateFormat(context);
        String pattern = ((SimpleDateFormat) dateFormat).toLocalizedPattern();

        SimpleDateFormat systemDateFormat = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        return systemDateFormat.format(calendar.getTime());
    }

    public static String formatColorToHex(int intColor) {
        return String.format("#%06X", (0xFFFFFF & intColor));
    }

    public static void startShareActivity(Context context) {
        String shareText = "Download 'Bible Afrikaas' at  " +
                "https://play.google.com/store/apps/details?id=tz.co.wadau.bibleafrikaans";
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.setType("text/plain");

        String title = context.getResources().getString(R.string.chooser_title);
        Intent chooser = Intent.createChooser(shareIntent, title);
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(new Intent(chooser));
        }
    }

    public static void setupTheme(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isBlack = sharedPreferences.getBoolean(SettingsFragment.KEY_PREFS_NIGHT_MODE, false);
        if (isBlack) {
            context.setTheme(R.style.NightModeTheme);
        } else {
            context.setTheme(R.style.AppTheme);
        }
    }

    public static boolean isBlackThemeEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(SettingsFragment.KEY_PREFS_NIGHT_MODE, false);
    }

    public static boolean isMultiColumnView(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(PREFS_IS_MULTI_COLUMN_VEW, true);
    }

    public static int[] colorChoices(Context context) {
        int[] mColorChoices = null;
        String[] colorArray = context.getResources().getStringArray(R.array.default_color_choice_values);
        if (colorArray != null && colorArray.length > 0) {
            mColorChoices = new int[colorArray.length];
            for (int i = 0; i < colorArray.length; i++) {
                mColorChoices[i] = Color.parseColor(colorArray[i]);
            }
        }
        return mColorChoices;
    }

    public static void setStringArrayPref(Context context, String key, ArrayList<Integer> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public static ArrayList<Integer> getIntegerArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<Integer> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    Integer url = a.optInt(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
}
