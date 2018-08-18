package tz.co.wadau.bibleafrikaans;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.List;

import tz.co.wadau.bibleafrikaans.adapter.VersesSearchResultsAdapter;
import tz.co.wadau.bibleafrikaans.adapter.VersesSearchResultsPagerAdapter;
import tz.co.wadau.bibleafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleafrikaans.model.Verse;
import tz.co.wadau.bibleafrikaans.utils.Utils;


public class VerseSearchActivity extends AppCompatActivity
        implements VersesSearchResultsAdapter.OnVerseResultClickListener {
    private ViewPager viewPager;
    private VersesSearchResultsPagerAdapter verseResultsPagerAdapter;
    private TabLayout tabLayout;
    private LinearLayout emptySearchVerse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setupTheme(this);
        setContentView(R.layout.activity_verse_search);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout_verses_search);
        viewPager = (ViewPager) findViewById(R.id.verse_search_results);
        emptySearchVerse = (LinearLayout) findViewById(R.id.empty_search_verse);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.all));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.old_statement));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.new_statement));

        tabLayout.addOnTabSelectedListener(listener);

        FloatingSearchView searchVerses = (FloatingSearchView) findViewById(R.id.verse_search);
        searchVerses.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                //Do nothinng
            }

            @Override
            public void onSearchAction(String currentQuery) {
                //Perfom search
                performVerseSearch(currentQuery);
            }
        });
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

    private void performVerseSearch(String query) {

        if (!TextUtils.isEmpty(query)) {

            DbFileHelper dbFileHelper = new DbFileHelper(getApplicationContext());
            List<Verse> searchedVerses = dbFileHelper.searchVerses(query);
            verseResultsPagerAdapter = new VersesSearchResultsPagerAdapter(getSupportFragmentManager(), searchedVerses, query, tabLayout.getTabCount());
            if (searchedVerses.size() > 0) {
                emptySearchVerse.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
            } else {
                emptySearchVerse.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
            }
            viewPager.setAdapter(verseResultsPagerAdapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        }
    }
}
