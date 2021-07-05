package tz.co.wadau.bibleinafrikaans.fragment;


import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

import tz.co.wadau.bibleinafrikaans.ChapterActivity;
import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.ViewPagerTransformsLibrary.src.com.ToxicBakery.viewpager.transforms.StackTransformer;
import tz.co.wadau.bibleinafrikaans.adapter.ChaptersPagerAdapter;

public class RightPanelChapterFragment extends Fragment
        implements SetTextSizeFragment.SetTextSizeDialogListener {

    private static final String TAG = RightPanelChapterFragment.class.getSimpleName();
    private ActionBar actionBar;
    private int bookNumber, chapterNumber, verseNumber, chapterTotalNumber;
    private String bookName;
    private AppCompatActivity mContext;
    private static ViewPager viewPager;
    private SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEdit;
    public static ChaptersPagerAdapter chaptersPagerAdapter;
    public static RecyclerView verseRecyclerView;
    public static Runnable mRunable;
    public static CountDownTimer countDownTimer;
    public static boolean isCountdownRunning = false;
    public static final int MIN_SCROLL_SPEED = 1;
    public static int scrollSpeed;
    private LinearLayout chapterTutorialContainer, closeTutorial;


    public RightPanelChapterFragment() {
        // Required empty public constructor
    }

//    public static RightPanelChapterFragment newInstance(String param1, String param2) {
//        RightPanelChapterFragment fragment = new RightPanelChapterFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mContext = (AppCompatActivity) getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEdit = mSharedPreferences.edit();
        setupScreenBrightness();

        if (getArguments() != null) {
            bookNumber = getArguments().getInt(ChapterActivity.BOOK_NUMBER);
            bookName = getArguments().getString(ChapterActivity.BOOK_NAME);
            chapterNumber = getArguments().getInt(ChapterActivity.CHAPTER_NUMBER);
            verseNumber = getArguments().getInt(ChapterActivity.VERSE_NUMBER);
            chapterTotalNumber = getArguments().getInt(ChapterActivity.CHAPTER_TOTAL_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_right_panel_chapter, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.verses_view_pager2);
        Toolbar toolbar = (Toolbar) mContext.findViewById(R.id.verses_right_panel_toolbar);
        chapterTutorialContainer = (LinearLayout) view.findViewById(R.id.chapter_tutorial_container);
        closeTutorial = (LinearLayout) view.findViewById(R.id.close_tutorial);
        mContext.setSupportActionBar(toolbar);
        actionBar = mContext.getSupportActionBar();
        actionBar.setTitle(bookName + " " + chapterNumber); //Set chapter number
        showChapterTutorialOnFirstRun();
        chaptersPagerAdapter = new ChaptersPagerAdapter(getChildFragmentManager(), bookNumber,
                chapterNumber, verseNumber, chapterTotalNumber);
        viewPager.setAdapter(chaptersPagerAdapter);
        viewPager.setPageTransformer(true, new StackTransformer());
        viewPager.setCurrentItem(Integer.valueOf(chapterNumber - 1), false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                //Set action bar
                int currPos = position + 1;
                actionBar.setTitle(bookName + " " + currPos);

                //Remove scrolling when a page is changed
                if (isCountdownRunning) {
                    countDownTimer.cancel();
                    verseRecyclerView.removeCallbacks(mRunable);
                    isCountdownRunning = false;

                    Log.d(TAG, "Closing auto-scroll timer");
                }
                mRunable = null;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        closeTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Close tutorial overlay
                chapterTutorialContainer.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_right_panel_chapter, menu);
        SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_verse).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(mContext.getComponentName()));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_orientation:
                changeOrientation();
                break;
//            case R.id.menu_change_theme:
//                changeTheme();
//                break;
            case R.id.menu_auto_scroll:
                showSetScrollSpeedDialog();
                break;
            case R.id.menu_change_font_size:
                showSetTextSizeDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSetTextDialogPositiveClick(int seekBarProgress) {
        setTextSize(seekBarProgress);
        Log.d(TAG, "Verse text size changed to " + seekBarProgress);
    }

    private void changeOrientation() {
        if (mContext.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public static void setTextSize(float size) {
        int currPagerItem = viewPager.getCurrentItem();
        mEdit.putInt(SettingsFragment.KEY_PREFS_TEXT_SIZE, (int) size);
        mEdit.apply();
        viewPager.setAdapter(chaptersPagerAdapter); //Reload viewpager to pick up new font size
        viewPager.setCurrentItem(currPagerItem); //Restore previous pager position
        Log.d(TAG, "Set text size " + (14 + size));
    }

    public static void setScrollSpeed(int speed) {
        scrollSpeed = MIN_SCROLL_SPEED + speed;
        mEdit.putInt(SettingsFragment.KEY_PREFS_AUTO_SCROLL_SPEED, speed);
        mEdit.apply();
        Log.d(TAG, "Auto-scroll speed " + scrollSpeed);
    }

    private void showSetTextSizeDialog() {
        DialogFragment dialogFragment = new SetTextSizeFragment();
        dialogFragment.show(mContext.getSupportFragmentManager(), "SetTextSizeFragment");
    }

    private void showSetScrollSpeedDialog() {
        DialogFragment mDialogFragment = new SetScrollSpeedFragment();
        mDialogFragment.show(mContext.getSupportFragmentManager(), "SetScrollSpeedFragment");
    }

    public static void autoScroll() {
        final long totalScrollTime = Long.MAX_VALUE; //total scroll time.
        final int scrollPeriod = 50; // every 50 ms scroll will happened. smaller values for smoother

        verseRecyclerView = (RecyclerView) viewPager.findViewWithTag(viewPager.getCurrentItem() + 1);
        mRunable = new Runnable() {
            @Override
            public void run() {
                countDownTimer = new CountDownTimer(totalScrollTime, scrollPeriod) {
                    public void onTick(long millisUntilFinished) {
                        verseRecyclerView.scrollBy(0, scrollSpeed);
                    }

                    public void onFinish() {
                    }
                }.start();
            }
        };
        verseRecyclerView.post(mRunable);
        isCountdownRunning = true;
        Log.d(TAG, "Runnable started");
    }

    private void setupScreenBrightness() {
        if (mSharedPreferences.getBoolean(SettingsFragment.KEY_PREFS_STAY_AWAKE, true)) {
            mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void showChapterTutorialOnFirstRun() {
        SharedPreferences tutorialPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isFirstRun = tutorialPreferences.getBoolean("TUTORIAL_FIRST_RUN", true);
        if (isFirstRun) {
            chapterTutorialContainer.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = tutorialPreferences.edit();
            editor.putBoolean("TUTORIAL_FIRST_RUN", false);
            editor.apply();
        }
    }
}
