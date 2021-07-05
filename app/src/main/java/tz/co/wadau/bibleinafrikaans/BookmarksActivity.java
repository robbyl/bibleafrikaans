package tz.co.wadau.bibleinafrikaans;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tz.co.wadau.bibleinafrikaans.adapter.BookmarksAdapter;
import tz.co.wadau.bibleinafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleinafrikaans.data.DbHelper;
import tz.co.wadau.bibleinafrikaans.model.Bookmark;
import tz.co.wadau.bibleinafrikaans.utils.Utils;

public class BookmarksActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener,
        BookmarksAdapter.OnBookmarkClickListener {

    public String TAG = BookmarksActivity.class.getSimpleName();
    private RecyclerView bookmarkRecyclerView;
    private BookmarksAdapter bookmarkAdapter;
    private List<Bookmark> bookmarks;
    private LinearLayout noBookmarkWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setupTheme(this);
        setContentView(R.layout.activity_bookmarks);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.bookmarks_toolbar);
        noBookmarkWrapper = (LinearLayout) findViewById(R.id.no_bookmark_wrapper);
        setSupportActionBar(mToolbar);
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        loadBookmarks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupEmptyBookmarksView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_bookmarks, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public void onBookmarkClicked(Bookmark bookmark) {
        DbFileHelper dbFileHelper = new DbFileHelper(this);
        int totalChapters = dbFileHelper.getChapters(bookmark.getBookNumber()).size();

        if (Utils.isTablet(this)) {
            Bundle arguments = new Bundle();
            arguments.putInt(ChapterActivity.BOOK_NUMBER, bookmark.getBookNumber());
            arguments.putString(ChapterActivity.BOOK_NAME, bookmark.getBookName());
            arguments.putInt(ChapterActivity.CHAPTER_NUMBER, bookmark.getChapterNumber());
            arguments.putInt(ChapterActivity.VERSE_NUMBER, bookmark.getVerseNumber());
            arguments.putInt(ChapterActivity.CHAPTER_TOTAL_NUMBER, totalChapters);

            Intent bibleIntent = new Intent(this, BibleActivity.class);
            bibleIntent.putExtras(arguments);
            startActivity(bibleIntent);
        } else {

            Intent chaptersIntent = new Intent(getApplicationContext(), ChapterActivity.class);
            chaptersIntent.putExtra(ChapterActivity.BOOK_NUMBER, bookmark.getBookNumber());
            chaptersIntent.putExtra(ChapterActivity.BOOK_NAME, bookmark.getBookName());
            chaptersIntent.putExtra(ChapterActivity.CHAPTER_NUMBER, bookmark.getChapterNumber());
            chaptersIntent.putExtra(ChapterActivity.VERSE_NUMBER, bookmark.getVerseNumber());
            chaptersIntent.putExtra(ChapterActivity.CHAPTER_TOTAL_NUMBER, totalChapters);

            startActivity(chaptersIntent);
        }
    }

    private void loadBookmarks() {
        DbHelper db = new DbHelper(this);
        bookmarks = db.getAllBookmarks();
        bookmarkAdapter = new BookmarksAdapter(bookmarks, this);
        bookmarkRecyclerView = (RecyclerView) findViewById(R.id.bookmarks_recycler_view);
        bookmarkRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookmarkRecyclerView.setItemAnimator(new DefaultItemAnimator());
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Utils.isTablet(this)) {
            startActivity(new Intent(this, BibleActivity.class));
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchBookmarks(newText);
        return true;
    }

    private void searchBookmarks(String keyWord) {
        keyWord = keyWord.toLowerCase();
        List<Bookmark> filteredBookmarks = new ArrayList<>();

        for (Bookmark bookmark : bookmarks) {
            String bookmarkTitle = (bookmark.getBookName() + " " + bookmark.getChapterNumber()
                    + ":" + bookmark.getVerseNumber()).toLowerCase();
            String bookmarkDate = bookmark.getCreatedDate().toLowerCase();
            if (bookmarkTitle.contains(keyWord) || bookmarkDate.contains(keyWord)) {
                filteredBookmarks.add(bookmark);
            }
        }

        bookmarkAdapter.filterBookmarks(filteredBookmarks);
    }

    private void setupEmptyBookmarksView() {
        if (bookmarks.isEmpty()) {
            bookmarkRecyclerView.setVisibility(View.GONE);
            noBookmarkWrapper.setVisibility(View.VISIBLE);
        } else {
            bookmarkRecyclerView.setVisibility(View.VISIBLE);
            noBookmarkWrapper.setVisibility(View.GONE);
        }

        if (Utils.isBlackThemeEnabled(this)) {
            AppCompatImageView imageView = (AppCompatImageView) findViewById(R.id.no_bookmarks_icon);
            TextView textView1 = (TextView) findViewById(R.id.empty_bookmarks_text1);
            TextView textView2 = (TextView) findViewById(R.id.empty_bookmarks_text2);

            imageView.setImageAlpha(100);
            textView1.setTextColor(Color.BLACK);
            textView2.setTextColor(Color.BLACK);
        }
    }
}
