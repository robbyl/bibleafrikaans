package tz.co.wadau.bibleafrikaans;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;

import tz.co.wadau.bibleafrikaans.adapter.VersesSearchResultsAdapter;
import tz.co.wadau.bibleafrikaans.adapter.VersesSearchResultsPagerAdapter;
import tz.co.wadau.bibleafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleafrikaans.model.Verse;
import tz.co.wadau.bibleafrikaans.utils.Utils;

public class VersesSearchResultsActivity extends AppCompatActivity
        implements VersesSearchResultsAdapter.OnVerseResultClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private VersesSearchResultsPagerAdapter verseResultsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setupTheme(this);
        setContentView(R.layout.activity_verses_search_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_verse_search_results);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout_verses_search_results);
        viewPager = (ViewPager) findViewById(R.id.verse_search_results);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.all));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.old_statement));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.new_statement));

        onNewIntent(getIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        searchVerses(intent);
    }


    private void searchVerses(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            DbFileHelper dbFileHelper = new DbFileHelper(this);
            List<Verse> searchedVerses = dbFileHelper.searchVerses(query);
            verseResultsPagerAdapter = new VersesSearchResultsPagerAdapter(getSupportFragmentManager(), searchedVerses, query, tabLayout.getTabCount());
            viewPager.setAdapter(verseResultsPagerAdapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout.addOnTabSelectedListener(listener);
        }
    }

    TabLayout.OnTabSelectedListener listener = new TabLayout.OnTabSelectedListener() {

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

    @Override
    public void onVerseResultClicked(Verse verseResult) {
        DbFileHelper dbFileHelper = new DbFileHelper(this);
        int totalChapters = dbFileHelper.getChapters(verseResult.getBookNumber()).size();

        if (Utils.isTablet(this)) {
            Bundle arguments = new Bundle();
            arguments.putInt(ChapterActivity.BOOK_NUMBER, verseResult.getBookNumber());
            arguments.putString(ChapterActivity.BOOK_NAME, verseResult.getBookName());
            arguments.putInt(ChapterActivity.CHAPTER_NUMBER, verseResult.getChapterNumber());
            arguments.putInt(ChapterActivity.VERSE_NUMBER, verseResult.getVerseNumber());
            arguments.putInt(ChapterActivity.CHAPTER_TOTAL_NUMBER, totalChapters);

            Intent bibleIntent = new Intent(this, BibleActivity.class);
            bibleIntent.putExtras(arguments);
            startActivity(bibleIntent);
        } else {

            Intent chaptersIntent = new Intent(getApplicationContext(), ChapterActivity.class);
            chaptersIntent.putExtra(ChapterActivity.BOOK_NUMBER, verseResult.getBookNumber());
            chaptersIntent.putExtra(ChapterActivity.BOOK_NAME, verseResult.getBookName());
            chaptersIntent.putExtra(ChapterActivity.CHAPTER_NUMBER, verseResult.getChapterNumber());
            chaptersIntent.putExtra(ChapterActivity.VERSE_NUMBER, verseResult.getVerseNumber());
            chaptersIntent.putExtra(ChapterActivity.CHAPTER_TOTAL_NUMBER, totalChapters);

            startActivity(chaptersIntent);
        }
    }
}
