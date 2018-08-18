package tz.co.wadau.biblekingjamesversion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tz.co.wadau.biblekingjamesversion.adapter.NotesAdapter;
import tz.co.wadau.biblekingjamesversion.customviews.ItemClickSupport;
import tz.co.wadau.biblekingjamesversion.customviews.colorpicker.ColorPickerDialog;
import tz.co.wadau.biblekingjamesversion.customviews.colorpicker.ColorPickerSwatch;
import tz.co.wadau.biblekingjamesversion.data.DbHelper;
import tz.co.wadau.biblekingjamesversion.model.Note;
import tz.co.wadau.biblekingjamesversion.utils.Utils;

import static tz.co.wadau.biblekingjamesversion.AddNoteActivity.NOTE_DATE;
import static tz.co.wadau.biblekingjamesversion.utils.Utils.isMultiColumnView;
import static tz.co.wadau.biblekingjamesversion.utils.Utils.isTablet;


public class NotesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private final String TAG = NotesActivity.class.getSimpleName();
    public static final String PREFS_IS_MULTI_COLUMN_VEW = "prefs_multi_column_view";
    private ActionBar mActionBar;
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private DbHelper db;
    private LinearLayout emptyNotesLayout;
    private List<Note> mNotes = new ArrayList<>();
    private int mSelectedColor = Color.parseColor("#FDFC9D");
    private MenuItem toggleNotesView;
    private int columnSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.notes_toolbar);
        setupBlackTheme();
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        actionModeCallback = new ActionModeCallback();
        emptyNotesLayout = (LinearLayout) findViewById(R.id.empty_notes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        columnSize = isTablet(this) ? 4 : 2;
        setNotes(this, isMultiColumnView(this));
        setupEmptyNotesView();

        ItemClickSupport.addTo(notesRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                if (actionMode != null) {
                    toggleSelection(position);
                } else {
                    showNoteDetails(mNotes.get(position).getId(), mNotes.get(position).getCreatedAt());
                }
            }
        });

        ItemClickSupport.addTo(notesRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                if (actionMode == null) {
                    actionMode = startSupportActionMode(actionModeCallback);
                }
                toggleSelection(position);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
        toggleNotesView = menu.findItem(R.id.action_toggle_notes_view);

        if (!isMultiColumnView(this)) {
            toggleNotesView.setIcon(R.drawable.ic_action_view_multi_column);
            toggleNotesView.setTitle(R.string.multi_column_view);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_notes_view:
                toggleNotesColumns();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchNotes(newText);
        return true;
    }

    private void toggleSelection(int position) {
        notesAdapter.toggleSelection(position);
        int count = notesAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private void setNotes(Context context, Boolean isMultiColumn) {
        db = new DbHelper(context);
        mNotes = db.getAllNotes();
        notesAdapter = new NotesAdapter(mNotes);
        notesRecyclerView = (RecyclerView) findViewById(R.id.notes_recycler_view);
        if (isMultiColumn) {

            notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(columnSize, 1));
        } else {
            notesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        notesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        notesRecyclerView.setAdapter(notesAdapter);
    }

    private void showNoteDetails(Long noteId, String noteCreatedDate) {
        Intent mIntent = new Intent(getApplicationContext(), AddNoteActivity.class);
        mIntent.putExtra(AddNoteActivity.NOTE_ID, noteId);
        mIntent.putExtra(NOTE_DATE, noteCreatedDate);
        startActivity(mIntent);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_notes, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteSelectedNotes(mode);
                    return true;
                case R.id.action_note_color:
                    setNotesColor(mode);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            notesAdapter.clearSelection();
            actionMode = null;
        }
    }

    private void deleteSelectedNotes(final ActionMode mode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String dialogTitle = notesAdapter.getSelectedItemCount() > 1 ? getString(R.string.delete_notes) : getString(R.string.delete_note);
        builder.setTitle(dialogTitle)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Integer> selectedNotes = notesAdapter.getSelectedItems();
                        int selectedSize = selectedNotes.size();
                        List<Integer> noteIds = new ArrayList<>();

                        for (int m = 0; m < selectedSize; m++) {
                            noteIds.add((int) mNotes.get(selectedNotes.get(m)).getId());
                        }

                        notesAdapter.removeItems(selectedNotes);
                        setupEmptyNotesView();
                        mode.finish();
                        dialog.cancel();
                        db.deleteNotes(noteIds);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setNotesColor(final ActionMode mode) {
        int[] mColors = Utils.colorChoices(this);
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.color_picker_default_title, mColors, mSelectedColor, 4, ColorPickerDialog.SIZE_SMALL);
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mSelectedColor = color;

                List<Integer> mSelectedNotes = notesAdapter.getSelectedItems();
                int selectedSize = mSelectedNotes.size();
                List<Integer> noteIds = new ArrayList<>();

                for (int m = 0; m < selectedSize; m++) {
                    noteIds.add((int) mNotes.get(mSelectedNotes.get(m)).getId());
                    mNotes.get(mSelectedNotes.get(m)).setColor(Utils.formatColorToHex(mSelectedColor));
                }

                notesAdapter.notifyDataSetChanged();
                mode.finish();
                db.updateNotesColor(noteIds, Utils.formatColorToHex(mSelectedColor));

                Log.d(TAG, "Selected color is " + Utils.formatColorToHex(mSelectedColor));
            }
        });

        colorPickerDialog.show(getFragmentManager(), "set notes color");
    }

    private void setupEmptyNotesView() {
        if (mNotes.isEmpty()) {
            notesRecyclerView.setVisibility(View.GONE);
            emptyNotesLayout.setVisibility(View.VISIBLE);
        } else {
            notesRecyclerView.setVisibility(View.VISIBLE);
            emptyNotesLayout.setVisibility(View.GONE);
        }
    }

    public void addNewNote(View view) {
        startActivity(new Intent(this, AddNoteActivity.class));
    }

    private void setupBlackTheme() {

        if (Utils.isBlackThemeEnabled(this)) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_notes);
            AppCompatImageView imageView = (AppCompatImageView) findViewById(R.id.empty_notes_icon);
            TextView textView = (TextView) findViewById(R.id.empty_notes_text);

            linearLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWindowBackground));
            imageView.setImageAlpha(100);
            textView.setTextColor(Color.BLACK);
        }
    }

    private void toggleNotesColumns() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        if (isMultiColumnView(this)) {
            toggleNotesView.setIcon(R.drawable.ic_action_view_multi_column);
            toggleNotesView.setTitle(R.string.multi_column_view);
            notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            notesRecyclerView.setAdapter(notesAdapter);
            editor.putBoolean(NotesActivity.PREFS_IS_MULTI_COLUMN_VEW, false);
            editor.apply();
        } else {
            toggleNotesView.setIcon(R.drawable.ic_action_view_single_column);
            toggleNotesView.setTitle(R.string.single_column_view);
            notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(columnSize, 1));
            notesRecyclerView.setAdapter(notesAdapter);
            editor.putBoolean(NotesActivity.PREFS_IS_MULTI_COLUMN_VEW, true);
            editor.apply();
        }
    }

    private void searchNotes(String keyWord){
        List<Note> filteredNotes = new ArrayList<>();
        keyWord = keyWord.toLowerCase();

        for (Note note : mNotes) {
            String noteText = note.getText().toLowerCase();
            String date = Utils.formatDateLongFormat(note.getCreatedAt()).toLowerCase();
            if (noteText.contains(keyWord) || date.contains(keyWord)) {
                filteredNotes.add(note);
            }
        }

        notesAdapter.setFilter(filteredNotes);
    }
}
