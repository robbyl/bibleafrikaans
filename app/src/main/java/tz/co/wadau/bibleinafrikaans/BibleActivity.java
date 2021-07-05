package tz.co.wadau.bibleinafrikaans;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import java.util.List;

import tz.co.wadau.bibleinafrikaans.adapter.BooksPagerAdapter;
import tz.co.wadau.bibleinafrikaans.adapter.VersesAdapter;
import tz.co.wadau.bibleinafrikaans.alarm.AlarmNotification;
import tz.co.wadau.bibleinafrikaans.data.DataHelper;
import tz.co.wadau.bibleinafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleinafrikaans.fragment.BaseExampleFragment;
import tz.co.wadau.bibleinafrikaans.fragment.ChaptersListFragment;
import tz.co.wadau.bibleinafrikaans.fragment.OldTestamentFragment;
import tz.co.wadau.bibleinafrikaans.fragment.RightPanelChapterFragment;
import tz.co.wadau.bibleinafrikaans.fragment.SetScrollSpeedFragment;
import tz.co.wadau.bibleinafrikaans.fragment.SetTextSizeFragment;
import tz.co.wadau.bibleinafrikaans.model.Book;
import tz.co.wadau.bibleinafrikaans.model.BookSuggestion;
import tz.co.wadau.bibleinafrikaans.model.Chapter;
import tz.co.wadau.bibleinafrikaans.model.Verse;
import tz.co.wadau.bibleinafrikaans.utils.AdManager;
import tz.co.wadau.bibleinafrikaans.utils.Utils;

import static tz.co.wadau.bibleinafrikaans.R.id.vpPager;
import static tz.co.wadau.bibleinafrikaans.fragment.NewTestamentFragment.newStatementAdapter;
import static tz.co.wadau.bibleinafrikaans.fragment.NewTestamentFragment.newStatementBooks;
import static tz.co.wadau.bibleinafrikaans.fragment.NewTestamentFragment.tempNewStmtBooks;
import static tz.co.wadau.bibleinafrikaans.fragment.OldTestamentFragment.oldStatementAdapter;
import static tz.co.wadau.bibleinafrikaans.fragment.OldTestamentFragment.oldStatementBooks;
import static tz.co.wadau.bibleinafrikaans.fragment.OldTestamentFragment.tempOldStmtBooks;
import static tz.co.wadau.bibleinafrikaans.fragment.RightPanelChapterFragment.autoScroll;
import static tz.co.wadau.bibleinafrikaans.fragment.RightPanelChapterFragment.countDownTimer;
import static tz.co.wadau.bibleinafrikaans.fragment.RightPanelChapterFragment.isCountdownRunning;
import static tz.co.wadau.bibleinafrikaans.fragment.RightPanelChapterFragment.mRunable;
import static tz.co.wadau.bibleinafrikaans.fragment.RightPanelChapterFragment.setScrollSpeed;
import static tz.co.wadau.bibleinafrikaans.fragment.RightPanelChapterFragment.setTextSize;
import static tz.co.wadau.bibleinafrikaans.fragment.RightPanelChapterFragment.verseRecyclerView;

public class BibleActivity extends AppCompatActivity
        implements BaseExampleFragment.BaseExampleFragmentCallbacks,
        OldTestamentFragment.OnBookSelectedListener,
        NavigationView.OnNavigationItemSelectedListener,
        ChaptersListFragment.OnChapterSelectedListerner,
        VersesAdapter.OnVerseClickedLister,
        SetTextSizeFragment.SetTextSizeDialogListener,
        SetScrollSpeedFragment.SetScrollSpeedListener {

    private final String TAG = BibleActivity.class.getSimpleName();
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private BooksPagerAdapter booksPagerAdapter;
    private FloatingSearchView mSearchView;
    private String mLastQuery = "";
    public static boolean mTwoPane;
    private Context mContext;
    private int currTab;
    private long mBackPressed;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private CoordinatorLayout coordinatorLayout;
    private int songTitleClick, CLICKS_TILL_AD_SHOW = 4;
    public static boolean SHOW_AD_WHEN_LOADED;
    public static final int UPDATE_REQUEST_CODE = 6;
    AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Utils.setupTheme(this);
        setContentView(R.layout.activity_bible);

        NavigationView navigationView = findViewById(R.id.nav_view);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        mSearchView = findViewById(R.id.floating_search_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        viewPager = findViewById(vpPager);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        mContext = this;
        songTitleClick = 0;

        mSearchView.attachNavigationDrawerToMenuButton(drawerLayout);
        navigationView.setNavigationItemSelectedListener(this);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.old_statement));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.new_statement));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        initializeLayoutPane();
        new LoadBooks().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        setupFloatingSearch();
        tabLayout.addOnTabSelectedListener(onTabSelectedListener);

        openChapterFromBookmark();
        setupDailyVersesNotification(this);

        SHOW_AD_WHEN_LOADED = true;
        AdManager.initialize(this);
        AdManager.createAd();

        checkForAppUpdate();
    }

    public BibleActivity() {
        super();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // currently visible tab Fragment
        OnBackPressListener currentFragment = (OnBackPressListener) booksPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem());
        CardView bookTitleView = findViewById(R.id.chapters_list_title);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //Close navigation drawerLayout if it is open
            drawer.closeDrawer(GravityCompat.START);
        } else if (currentFragment != null && bookTitleView != null) {
            // lets see if the currentFragment or any of its childFragment can handle onBackPressed
            currentFragment.onBackPressed();
        } else if (resetBookListToOriginalState()) {
            Log.d(TAG, "Just reset filtered book list to original state");
        } else if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Snackbar.make(coordinatorLayout, R.string.tap_to_exit, Snackbar.LENGTH_LONG).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Handle navigation view item clicks here.
            Context context = getApplicationContext();

            switch (menuItem.getItemId()) {
                case R.id.nav_bookmarks:
                    startActivity(new Intent(context, BookmarksActivity.class));
                    break;
                case R.id.nav_verse_highlight:
                    startActivity(new Intent(context, VerseHighlightsActivity.class));
                    break;
                case R.id.nav_notes:
                    startActivity(new Intent(context, NotesActivity.class));
                    break;
                case R.id.nav_search:
                    startActivity(new Intent(context, VerseSearchActivity.class));
                    break;
                case R.id.nav_share:
                    Utils.startShareActivity(context);
                    break;
                case R.id.nav_rate:
                    launchMarket();
                    break;
                case R.id.nav_settings:
                    startActivity(new Intent(context, SettingsActivity.class));
                    break;
            }
        }, 200);

        return true;
    }

    @Override
    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {
        searchView.attachNavigationDrawerToMenuButton(drawerLayout);
    }

    @Override
    public void onBookSelected(Book book) {
//        Toast.makeText(getApplicationContext(), "This is from the parent activity " + book.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChapterSelected(Chapter chapter) {

        songTitleClick++;
        InterstitialAd mInterstitialAd = AdManager.getAd();

        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(ChapterActivity.BOOK_NUMBER, chapter.getBookNumber());
            arguments.putString(ChapterActivity.BOOK_NAME, chapter.getBookName());
            arguments.putInt(ChapterActivity.CHAPTER_NUMBER, chapter.getNumber());
            arguments.putInt(ChapterActivity.CHAPTER_TOTAL_NUMBER, chapter.getTotalChapters());

            final RightPanelChapterFragment fragment = new RightPanelChapterFragment();
            fragment.setArguments(arguments);
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

            if (mInterstitialAd != null && (SHOW_AD_WHEN_LOADED || (songTitleClick % CLICKS_TILL_AD_SHOW) == 0)) {
                mInterstitialAd.show();

                mInterstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        SHOW_AD_WHEN_LOADED = false;
                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        transaction.replace(R.id.verse_container, fragment).commitAllowingStateLoss();
                        AdManager.createAd();
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {

                    }

                    @Override
                    public void onAdLoaded(Ad ad) {

                    }

                    @Override
                    public void onAdClicked(Ad ad) {

                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                    }
                });
            } else {
                transaction.replace(R.id.verse_container, fragment).commitNow();
            }

        } else {
            final Intent versesIntent = new Intent(getApplicationContext(), ChapterActivity.class);
            versesIntent.putExtra(ChapterActivity.BOOK_NUMBER, chapter.getBookNumber());
            versesIntent.putExtra(ChapterActivity.BOOK_NAME, chapter.getBookName());
            versesIntent.putExtra(ChapterActivity.CHAPTER_NUMBER, chapter.getNumber());
            versesIntent.putExtra(ChapterActivity.CHAPTER_TOTAL_NUMBER, chapter.getTotalChapters());

            if (mInterstitialAd != null && (SHOW_AD_WHEN_LOADED || (songTitleClick % CLICKS_TILL_AD_SHOW) == 0)) {
                mInterstitialAd.show();

                mInterstitialAd.buildLoadAdConfig().withAdListener(new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        SHOW_AD_WHEN_LOADED = false;
                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        AdManager.adShowed = true;
                        startActivity(versesIntent);
                        AdManager.createAd();
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {

                    }

                    @Override
                    public void onAdLoaded(Ad ad) {

                    }

                    @Override
                    public void onAdClicked(Ad ad) {

                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                    }
                });
            } else {
                startActivity(versesIntent);
            }
        }

        Log.d(TAG, "Opened verses from the book " + chapter.getBookNumber() + " and chapter " + chapter.getNumber());
    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener((oldQuery, newQuery) -> {

            if (!oldQuery.equals("") && newQuery.equals("")) {
                //Reset filtered book list to original state
                mSearchView.clearSuggestions();
                resetBookListToOriginalState();

            } else {

                currTab = viewPager.getCurrentItem();

                DataHelper.findSuggestions(mContext, currTab, newQuery, 5
                        , results -> {

                            //this will swap the data and
                            //render the collapse/expand animations as necessary
                            mSearchView.swapSuggestions(results);
                        });
            }

            Log.d(TAG, "onSearchTextChanged()");
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                currTab = viewPager.getCurrentItem();
                final BookSuggestion bookSuggestion = (BookSuggestion) searchSuggestion;

                Log.d(TAG, "Book suggestion clicked " + bookSuggestion.getBookName());
                DataHelper.findBooks(mContext, currTab, bookSuggestion.getBody(),
                        results -> {
                            //Filter Old Testament list when there are mached oldStatementBooks
                            if (results.size() > 0) {
                                swapCurrTabBooks(results);
                            }
                        });
                Log.d(TAG, "onSuggestionClicked()");

                mLastQuery = searchSuggestion.getBody();
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                currTab = viewPager.getCurrentItem();

                DataHelper.findBooks(mContext, currTab, query,
                        results -> {
                            //Replace Old statement oldStatementBooks when user click soft search button
                            if (results.size() > 0) {
//                                    oldStatementBooks = results;
                                swapCurrTabBooks(results);
                            }
                        });
                Log.d(TAG, "onSearchAction()");
            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                currTab = viewPager.getCurrentItem();
                //show suggestions when search bar gains focus (typically history suggestions)
                mSearchView.swapSuggestions(DataHelper.getHistory(mContext, currTab, 3));
                Log.d(TAG, "onFocus()");
            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);
                resetBookListToOriginalState();

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());
                Log.d(TAG, "onFocusCleared()");
            }
        });


        //handle menu clicks the same way as you would
        //in a regular activity
        mSearchView.setOnMenuItemClickListener(item -> {
            //just print action
            Toast.makeText(mContext.getApplicationContext(), item.getTitle(),
                    Toast.LENGTH_SHORT).show();
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(() -> Log.d(TAG, "onHomeClicked()"));

        /*
         * Here you have access to the left icon and the text of a given suggestion
         * item after as it is bound to the suggestion list. You can utilize this
         * callback to change some properties of the left icon and the text. For example, you
         * can load the left icon images using your favorite image loading library, or change text color.
         *
         *
         * Important:
         * Keep in mind that the suggestion list is a RecyclerView, so views are reused for different
         * items in the list.
         */
        mSearchView.setOnBindSuggestionCallback((suggestionView, leftIcon, textView, item, itemPosition) -> {
            BookSuggestion bookSuggestion = (BookSuggestion) item;

            String textColor = "#000000";
            String textLight = "#787878";

            if (bookSuggestion.getIsHistory()) {
                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_history_black_24dp, null));

                Util.setIconColor(leftIcon, Color.parseColor(textColor));
                leftIcon.setAlpha(.36f);
            } else {
                leftIcon.setAlpha(0.0f);
                leftIcon.setImageDrawable(null);
            }

            textView.setTextColor(Color.parseColor(textColor));
            String text = bookSuggestion.getBody()
                    .replaceFirst(mSearchView.getQuery(),
                            "<font color=\"" + textLight + "\">" + mSearchView.getQuery() + "</font>");
            textView.setText(Html.fromHtml(text));
        });
    }

    private void swapCurrTabBooks(List<Book> results) {
        currTab = viewPager.getCurrentItem();

        switch (currTab) {
            case 0:
                //Filter old statement oldStatementBooks
                oldStatementAdapter.swapData(results);
                oldStatementBooks = results;
                Log.d(TAG, "Swapping old Testament book list");
                break;
            case 1:
                //Filter new statement oldStatementBooks
                newStatementAdapter.swapData(results);
                newStatementBooks = results;
                Log.d(TAG, "Swapping new statement book list");
                break;
        }
    }

    private void initializeLayoutPane() {
        //            setupActionBar();
        mTwoPane = findViewById(R.id.verse_container) != null;
    }

    private boolean resetBookListToOriginalState() {
        Log.d(TAG, "resetBookListToOriginalState()");

        int mCurrPos = viewPager.getCurrentItem();
        if (mCurrPos == 0 && (oldStatementBooks.size() != tempOldStmtBooks.size())) {
            swapCurrTabBooks(tempOldStmtBooks);
            return true;
        } else if (mCurrPos == 1 && (newStatementBooks.size() != tempNewStmtBooks.size())) {
            swapCurrTabBooks(tempNewStmtBooks);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onVerseClicked(Verse verse) {
        if (mRunable != null && isCountdownRunning) {
            countDownTimer.cancel();
            verseRecyclerView.removeCallbacks(mRunable);
            isCountdownRunning = false;
            Toast.makeText(this, R.string.auto_scroll_stoped, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSetTextDialogPositiveClick(int seekBarProgress) {
        setTextSize(seekBarProgress);
    }

    @Override
    public void onSetScrollSpeedDialogPositiveClick(int seekBarProgress) {
        setScrollSpeed(seekBarProgress);
        autoScroll();
    }

    private void openChapterFromBookmark() {
        Bundle args = getIntent().getExtras();

        if (args != null && args.get(ChapterActivity.BOOK_NUMBER) != null) {
            RightPanelChapterFragment fragment = new RightPanelChapterFragment();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.verse_container, fragment).commitNow();

            Log.d(TAG, "Check this if is null");
        }
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.unable_to_find_play_store, Toast.LENGTH_LONG).show();
        }
    }

    public void setupDailyVersesNotification(Context context) {

        AlarmNotification alarmNotification = new AlarmNotification();
        if (!alarmNotification.alarmUp(context)) {
            alarmNotification.setAlarm(context);
        } else {
            Log.d(TAG, "Alarm is present no need to create one");
        }
    }

    public class LoadBooks extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DbFileHelper dbFileHelper = new DbFileHelper(mContext);
            dbFileHelper.initializeDb();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            booksPagerAdapter = new BooksPagerAdapter(getResources(), getSupportFragmentManager());
            viewPager.setAdapter(booksPagerAdapter);
        }
    }

    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    public void checkForAppUpdate() {
        SharedPreferences preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int runTimes = preferences.getInt("RUN_TIMES", 0);

        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(state -> {
            switch (state.installStatus()) {
                case InstallStatus.DOWNLOADING:
                    long bytesDownloaded = state.bytesDownloaded();
                    long totalBytesToDownload = state.totalBytesToDownload();
                    // Implement progress bar.
                    break;

                case InstallStatus.DOWNLOADED:
                    popupSnackbarForCompleteUpdate();
                    break;
            }
        });

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                    && appUpdateInfo.clientVersionStalenessDays() != null
//                    && appUpdateInfo.clientVersionStalenessDays() >= DAYS_FOR_FLEXIBLE_UPDATE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                    //Only check for app apdate if the app is run in multiple of 4
                    && runTimes % 4 == 0) {

                try {
                    // Request the update.
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.update_downloaded, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.restart, view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }
}
