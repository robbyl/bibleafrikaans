package tz.co.wadau.bibleinafrikaans;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Toast;

import tz.co.wadau.bibleinafrikaans.ViewPagerTransformsLibrary.src.com.ToxicBakery.viewpager.transforms.StackTransformer;
import tz.co.wadau.bibleinafrikaans.adapter.ChaptersPagerAdapter;
import tz.co.wadau.bibleinafrikaans.adapter.VersesAdapter;
import tz.co.wadau.bibleinafrikaans.fragment.SetScrollSpeedFragment;
import tz.co.wadau.bibleinafrikaans.fragment.SetTextSizeFragment;
import tz.co.wadau.bibleinafrikaans.fragment.SettingsFragment;
import tz.co.wadau.bibleinafrikaans.model.Verse;

public class ChapterActivity extends AppCompatActivity
        implements SetTextSizeFragment.SetTextSizeDialogListener,
        SetScrollSpeedFragment.SetScrollSpeedListener,
        VersesAdapter.OnVerseClickedLister,
        ActionMenuView.OnMenuItemClickListener {

    private static final String TAG = ChapterActivity.class.getSimpleName();
    public static final String BOOK_NUMBER = "tz.co.wadau.bibleafrikaans.BOOK_NUMBER";
    public static final String BOOK_NAME = "tz.co.wadau.bibleafrikaans.BOOK_NAME";
    public static final String CHAPTER_NUMBER = "tz.co.wadau.bibleafrikaans.CHAPTER_NUMBER";
    public static final String VERSE_NUMBER = "tz.co.wadau.bibleafrikaans.VERSE_NUMBER";
    public static final String CHAPTER_TOTAL_NUMBER = "tz.co.wadau.bibleafrikaans.CHAPTER_TOTAL_NUMBER";
    public static final int MIN_SCROLL_SPEED = 1;
    public static int scrollSpeed;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ActionMenuView bottomActionMenu;
    private ActionBar actionBar;
    private int bookNumber, chapterNumber, verseNo, chapterTotalNumber;
    private String bookName;
    private ViewPager viewPager;
    private ChaptersPagerAdapter chaptersPagerAdapter;
    private Menu mMenu;
    public static boolean isBlack;

    public Runnable mRunable;
    private RecyclerView verseRecyclerView;
    public CountDownTimer countDownTimer;
    public boolean isCountdownRunning = false;
    private LinearLayout bottomToolbarLayout;
    private LinearLayout chapterTutorialContainer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isBlack = sharedPreferences.getBoolean(SettingsFragment.KEY_PREFS_NIGHT_MODE, false);
        setupTheme();
        setContentView(R.layout.activity_chapter);

        viewPager = (ViewPager) findViewById(R.id.verses_view_pager);
        Intent intent = getIntent();
        bookNumber = intent.getIntExtra(BOOK_NUMBER, 1);
        bookName = intent.getStringExtra(BOOK_NAME);
        chapterNumber = intent.getIntExtra(CHAPTER_NUMBER, 1);
        verseNo = intent.getIntExtra(VERSE_NUMBER, 1);
        chapterTotalNumber = intent.getIntExtra(CHAPTER_TOTAL_NUMBER, 50);

        Toolbar toolbar = (Toolbar) findViewById(R.id.verses_toolbar);
        bottomActionMenu = (ActionMenuView) findViewById(R.id.bottom_action_menu);
        bottomToolbarLayout = (LinearLayout) findViewById(R.id.bottom_toolbar_container);
        chapterTutorialContainer = (LinearLayout) findViewById(R.id.chapter_tutorial_container);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(bookName + " " + chapterNumber);

        editor = sharedPreferences.edit();
        scrollSpeed = MIN_SCROLL_SPEED + sharedPreferences.getInt(SettingsFragment.KEY_PREFS_AUTO_SCROLL_SPEED, 0);

        setupScreenBrightness();
        bottomActionMenu.setOnMenuItemClickListener(this);
        chaptersPagerAdapter = new ChaptersPagerAdapter(getSupportFragmentManager(),
                bookNumber, chapterNumber, verseNo, chapterTotalNumber);
//        new BookChapters().execute();
        showChapterTutorialOnFirstRun();
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

        Log.d(TAG, "Chapter numbers " + chapterTotalNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chapter, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_verse).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        mMenu = bottomActionMenu.getMenu();
        if (isBlack) {
            getMenuInflater().inflate(R.menu.activity_chapter_bottom_night, mMenu);
        } else {
            getMenuInflater().inflate(R.menu.activity_chapter_bottom_day, mMenu);
        }

        return true;
    }

    @Override
    public void onSetTextDialogPositiveClick(int seekBarProgress) {
        setTextSize(seekBarProgress);
        Log.d(TAG, "Verse text size changed to " + seekBarProgress);
    }


    @Override
    public void onVerseClicked(Verse verse) {
        if (mRunable != null && isCountdownRunning) {
            countDownTimer.cancel();
            verseRecyclerView.removeCallbacks(mRunable);
            isCountdownRunning = false;
            bottomToolbarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            Toast.makeText(this, R.string.auto_scroll_stoped, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSetScrollSpeedDialogPositiveClick(int seekBarProgress) {
        setScrollSpeed(seekBarProgress);
        autoScroll();
    }

    private void changeOrientation() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void changeTheme() {
        if (isBlack) {
            isBlack = false;
            editor.putBoolean(SettingsFragment.KEY_PREFS_NIGHT_MODE, isBlack);
            editor.apply();
            recreate();

        } else {
            isBlack = true;
            editor.putBoolean(SettingsFragment.KEY_PREFS_NIGHT_MODE, isBlack);
            editor.apply();
            recreate();
        }

    }

    private void setTextSize(float size) {
        int currPagerItem = viewPager.getCurrentItem();
        editor.putInt(SettingsFragment.KEY_PREFS_TEXT_SIZE, (int) size);
        editor.apply();
        viewPager.setAdapter(chaptersPagerAdapter); //Reload viewpager to pick up new font size
        viewPager.setCurrentItem(currPagerItem); //Restore previous pager position
        Log.d(TAG, "Set text size " + (14 + size));
    }

    private void setScrollSpeed(int speed) {
        scrollSpeed = MIN_SCROLL_SPEED + speed;
        editor.putInt(SettingsFragment.KEY_PREFS_AUTO_SCROLL_SPEED, speed);
        editor.apply();
        Log.d(TAG, "Auto-scroll speed " + scrollSpeed);
    }

    private void showSetTextSizeDialog() {
        DialogFragment dialogFragment = new SetTextSizeFragment();
        dialogFragment.show(getSupportFragmentManager(), "SetTextSizeFragment");
    }

    private void showSetScrollSpeedDialog() {
        DialogFragment mDialogFragment = new SetScrollSpeedFragment();
        mDialogFragment.show(getSupportFragmentManager(), "SetScrollSpeedFragment");
    }

    private void autoScroll() {
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

    private void setupTheme() {
        if (isBlack) {
            setTheme(R.style.NightModeTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    private void setupScreenBrightness() {
        if (sharedPreferences.getBoolean(SettingsFragment.KEY_PREFS_STAY_AWAKE, true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void closeTutorial(View view) {
        chapterTutorialContainer.setVisibility(View.GONE);
    }

    private void showChapterTutorialOnFirstRun() {
        SharedPreferences tutorialPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = tutorialPreferences.getBoolean("TUTORIAL_FIRST_RUN", true);
        if (isFirstRun) {
            chapterTutorialContainer.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = tutorialPreferences.edit();
            editor.putBoolean("TUTORIAL_FIRST_RUN", false);
            editor.apply();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_orientation:
                changeOrientation();
                break;
            case R.id.menu_change_theme:
                changeTheme();
                break;
            case R.id.menu_auto_scroll:
                showSetScrollSpeedDialog();
                break;
            case R.id.menu_change_font_size:
                showSetTextSizeDialog();
                break;
        }
        return onOptionsItemSelected(item);
    }

    private class BookChapters extends AsyncTask<Object, Object, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            chaptersPagerAdapter = new ChaptersPagerAdapter(getSupportFragmentManager(),
                    bookNumber, chapterNumber, verseNo, chapterTotalNumber);
            showChapterTutorialOnFirstRun();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

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
        }
    }
}
