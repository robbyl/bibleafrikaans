package tz.co.wadau.bibleinafrikaans;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.IOException;
import java.util.List;

import tz.co.wadau.bibleinafrikaans.adapter.BooksPagerAdapter;
import tz.co.wadau.bibleinafrikaans.adapter.VersesAdapter;
import tz.co.wadau.bibleinafrikaans.alarm.AlarmNotification;
import tz.co.wadau.bibleinafrikaans.data.DataHelper;
import tz.co.wadau.bibleinafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleinafrikaans.data.DbHelper;
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
import tz.co.wadau.bibleinafrikaans.utils.Utils;

import static tz.co.wadau.bibleinafrikaans.R.id.vpPager;
import static tz.co.wadau.bibleinafrikaans.data.DbFileHelper.PREFS_KEY_DB_VER;
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
    private InterstitialAd mInterstitialAd;
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private CoordinatorLayout coordinatorLayout;
    private final String ADMOB_APP_ID = "ca-app-pub-6949253770172194~2746379503";
    private int songTitleClick, CLICKS_TILL_AD_SHOW = 3;

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

        booksPagerAdapter = new BooksPagerAdapter(getResources(), getSupportFragmentManager());
        viewPager.setAdapter(booksPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        initializeLayoutPane();
        initializeBibleDb();
        setupFloatingSearch();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

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
        });

        openChapterFromBookmark();
        setupDailyVersesNotification(this);

        setupInterstitialAd();
        requestNewInterstitial();
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
        } else if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
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

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
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

            if (mInterstitialAd.isLoaded() && ((songTitleClick % CLICKS_TILL_AD_SHOW) == 0)) {
                mInterstitialAd.show();

                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        if (songTitleClick == CLICKS_TILL_AD_SHOW && songTitleClick <= CLICKS_TILL_AD_SHOW)
                            requestNewInterstitial();
                        transaction.replace(R.id.verse_container, fragment).commitAllowingStateLoss();
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

            if (mInterstitialAd.isLoaded() && ((songTitleClick % CLICKS_TILL_AD_SHOW) == 0)) {
                mInterstitialAd.show();

                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        if (songTitleClick == CLICKS_TILL_AD_SHOW && songTitleClick <= CLICKS_TILL_AD_SHOW)
                            requestNewInterstitial();
                        startActivity(versesIntent);
                    }
                });
            } else {
                startActivity(versesIntent);
            }
        }

        Log.d(TAG, "Opened verses from the book " + chapter.getBookNumber() + " and chapter " + chapter.getNumber());
    }

    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    //Reset filtered book list to original state
                    mSearchView.clearSuggestions();
                    resetBookListToOriginalState();

                } else {

                    currTab = viewPager.getCurrentItem();

                    DataHelper.findSuggestions(mContext, currTab, newQuery, 5
                            , new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<BookSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    mSearchView.swapSuggestions(results);
                                }
                            });
                }

                Log.d(TAG, "onSearchTextChanged()");
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {

                currTab = viewPager.getCurrentItem();
                final BookSuggestion bookSuggestion = (BookSuggestion) searchSuggestion;

                Log.d(TAG, "Book suggestion clicked " + bookSuggestion.getBookName());
                DataHelper.findBooks(mContext, currTab, bookSuggestion.getBody(),
                        new DataHelper.OnFindBooksListener() {

                            @Override
                            public void onResults(List<Book> results) {
                                //Filter Old Testament list when there are mached oldStatementBooks
                                if (results.size() > 0) {
                                    swapCurrTabBooks(results);
                                }
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
                        new DataHelper.OnFindBooksListener() {

                            @Override
                            public void onResults(List<Book> results) {
                                //Replace Old statement oldStatementBooks when user click soft search button
                                if (results.size() > 0) {
//                                    oldStatementBooks = results;
                                    swapCurrTabBooks(results);
                                }
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
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                //just print action
                Toast.makeText(mContext.getApplicationContext(), item.getTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //use this listener to listen to menu clicks when app:floatingSearch_leftAction="showHome"
        mSearchView.setOnHomeActionClickListener(new FloatingSearchView.OnHomeActionClickListener() {
            @Override
            public void onHomeClicked() {
                Log.d(TAG, "onHomeClicked()");
            }
        });

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
        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon,
                                         TextView textView, SearchSuggestion item, int itemPosition) {
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
            }
        });
    }

    private void initializeBibleDb() {
        DbFileHelper db = new DbFileHelper(this);
        DbHelper dbHelper = new DbHelper(this);

        if (db.isDbPresent()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            int dbVersion = prefs.getInt(PREFS_KEY_DB_VER, 1);
            if (db.getVersion() != dbVersion) {
                File dbFile = mContext.getDatabasePath(db.getName());
                if (!dbFile.delete()) {
                    Log.w(TAG, "Unable to update database");
                }
            }
        }

        try {
            db.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SQLiteDatabase mDb = dbHelper.getReadableDatabase();
        mDb.close();
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

    private void setupInterstitialAd() {
        MobileAds.initialize(this, ADMOB_APP_ID);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-6949253770172194/5537738962");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();

                Snackbar.make(coordinatorLayout, R.string.tap_to_exit, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("50941AF57FD6434ECCFA81A57FF7D313")
                .addTestDevice("28D01E8B9AD20EEC0A73479ED41140E9")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }
}
