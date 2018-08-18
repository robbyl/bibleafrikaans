package tz.co.wadau.bibleafrikaans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import tz.co.wadau.bibleafrikaans.adapter.VerseHighlightsAdapter;
import tz.co.wadau.bibleafrikaans.customviews.colorpicker.ColorPickerDialog;
import tz.co.wadau.bibleafrikaans.customviews.colorpicker.ColorPickerSwatch;
import tz.co.wadau.bibleafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleafrikaans.data.DbHelper;
import tz.co.wadau.bibleafrikaans.model.VerseHighlight;
import tz.co.wadau.bibleafrikaans.utils.Utils;

import static tz.co.wadau.bibleafrikaans.utils.Utils.colorChoices;


public class VerseHighlightsActivity extends AppCompatActivity
        implements VerseHighlightsAdapter.OnVerseHighlightClickListener,
        SearchView.OnQueryTextListener {

    private final String TAG = VerseHighlightsActivity.class.getSimpleName();
    private List<VerseHighlight> verseHighlights;
    private VerseHighlightsAdapter verseHighlightsAdapter;
    private RecyclerView verseHighlightsRecyclerView;
    private LinearLayoutManager layoutMananger;
    private Context myContext;
    private String mSelectedColor = "";
    private LinearLayout noHighlightedVersesWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setupTheme(this);
        setContentView(R.layout.activity_verse_highlights);

        myContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.verse_highlights_toolbar);
        verseHighlightsRecyclerView = (RecyclerView) findViewById(R.id.verse_highlights_recycler_view);
        noHighlightedVersesWrapper = (LinearLayout) findViewById(R.id.no_highlighted_verses_wrapper);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

        new LoadHighlightedVerses().execute(); //Load highlighted verses in the background
    }

    private void loadVerseHighlights() {
        DbHelper dbHelper = new DbHelper(this);
        verseHighlights = dbHelper.getAllVerseHighlights();
        verseHighlightsAdapter = new VerseHighlightsAdapter(verseHighlights, this);
        layoutMananger = new LinearLayoutManager(this);
    }

    private void setupEmptyHighlights() {
        if (verseHighlights.isEmpty()) {
            verseHighlightsRecyclerView.setVisibility(View.GONE);
            noHighlightedVersesWrapper.setVisibility(View.VISIBLE);
        } else {
            verseHighlightsRecyclerView.setVisibility(View.VISIBLE);
            noHighlightedVersesWrapper.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_verse_highlights, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_color_filter:
                showColorPickerDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(mSelectedColor)) {
            mSelectedColor = "";
            verseHighlightsAdapter.filterVerseHighlights(verseHighlights); //Restore verse highlights list
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onVerseHighlightClicked(VerseHighlight verseHighlight) {

        DbFileHelper dbFileHelper = new DbFileHelper(this);
        int totalChapters = dbFileHelper.getChapters(verseHighlight.getBookNumber()).size();

        if (Utils.isTablet(this)) {
            Bundle arguments = new Bundle();
            arguments.putInt(ChapterActivity.BOOK_NUMBER, verseHighlight.getBookNumber());
            arguments.putString(ChapterActivity.BOOK_NAME, verseHighlight.getBookName());
            arguments.putInt(ChapterActivity.CHAPTER_NUMBER, verseHighlight.getChapterNumber());
            arguments.putInt(ChapterActivity.VERSE_NUMBER, verseHighlight.getVerseNumber());
            arguments.putInt(ChapterActivity.CHAPTER_TOTAL_NUMBER, totalChapters);

            Intent bibleIntent = new Intent(this, BibleActivity.class);
            bibleIntent.putExtras(arguments);
            startActivity(bibleIntent);
        } else {

            Intent chaptersIntent = new Intent(getApplicationContext(), ChapterActivity.class);
            chaptersIntent.putExtra(ChapterActivity.BOOK_NUMBER, verseHighlight.getBookNumber());
            chaptersIntent.putExtra(ChapterActivity.BOOK_NAME, verseHighlight.getBookName());
            chaptersIntent.putExtra(ChapterActivity.CHAPTER_NUMBER, verseHighlight.getChapterNumber());
            chaptersIntent.putExtra(ChapterActivity.VERSE_NUMBER, verseHighlight.getVerseNumber());
            chaptersIntent.putExtra(ChapterActivity.CHAPTER_TOTAL_NUMBER, totalChapters);

            startActivity(chaptersIntent);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchVerseHighlights(newText);
        return true;
    }

    private void searchVerseHighlights(String keyWord) {
        keyWord = keyWord.toLowerCase();
        List<VerseHighlight> mFilteredVerseHighlights = new ArrayList<>();

        for (VerseHighlight verseHighlight : verseHighlights) {
            String verseHighlightTitle = (verseHighlight.getBookName() + " " + verseHighlight.getChapterNumber()
                    + ":" + verseHighlight.getVerseNumber()).toLowerCase();
            String verseHighlightText = verseHighlight.getVerseText().toLowerCase();
            if (verseHighlightTitle.contains(keyWord) || verseHighlightText.contains(keyWord)) {
                mFilteredVerseHighlights.add(verseHighlight);
            }
        }

        verseHighlightsAdapter.filterVerseHighlights(mFilteredVerseHighlights);
    }

    private void showColorPickerDialog() {
        int[] mColors = colorChoices(myContext);
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.color_picker_default_title, mColors, 0, 4, ColorPickerDialog.SIZE_SMALL);
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mSelectedColor = Utils.formatColorToHex(color).toLowerCase();
                filterByColor(mSelectedColor);
            }
        });

        colorPickerDialog.show(((Activity) myContext).getFragmentManager(), "set notes color");
    }

    private void filterByColor(String color) {
        List<VerseHighlight> filteredVerseHighlights = new ArrayList<>();

        for (VerseHighlight verseHighlight : verseHighlights) {
            String verseHighlightColor = verseHighlight.getColor().toLowerCase();
            if (verseHighlightColor.contains(color)) {
                filteredVerseHighlights.add(verseHighlight);
            }
        }

        verseHighlightsAdapter.filterVerseHighlights(filteredVerseHighlights);
    }

    private class LoadHighlightedVerses extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            loadVerseHighlights();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            verseHighlightsRecyclerView.setLayoutManager(layoutMananger);
            verseHighlightsRecyclerView.setItemAnimator(new DefaultItemAnimator());
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(), layoutMananger.getOrientation());
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.divider_horizontal_bright));
            verseHighlightsRecyclerView.addItemDecoration(dividerItemDecoration);
            verseHighlightsRecyclerView.setAdapter(verseHighlightsAdapter);
            setupEmptyHighlights();
        }
    }
}
