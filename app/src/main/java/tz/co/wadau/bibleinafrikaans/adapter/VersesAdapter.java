package tz.co.wadau.bibleinafrikaans.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.customviews.colorpicker.ColorPickerDialog;
import tz.co.wadau.bibleinafrikaans.customviews.colorpicker.ColorPickerSwatch;
import tz.co.wadau.bibleinafrikaans.data.DbHelper;
import tz.co.wadau.bibleinafrikaans.fragment.SettingsFragment;
import tz.co.wadau.bibleinafrikaans.model.Bookmark;
import tz.co.wadau.bibleinafrikaans.model.Verse;
import tz.co.wadau.bibleinafrikaans.model.VerseHighlight;
import tz.co.wadau.bibleinafrikaans.utils.Utils;

import static tz.co.wadau.bibleinafrikaans.utils.Utils.colorChoices;

public class VersesAdapter extends RecyclerView.Adapter<VersesAdapter.VerseViewHolder> {

    private final String TAG = VersesAdapter.class.getSimpleName();
    private List<Verse> verses;
    private OnVerseClickedLister verseClickedLister;
    private Context context;
    private int textSize;
    private String typeFace;
    public static final int MIN_VERSE_TEXT_SIZE = 14;
    private SparseBooleanArray selectedVerses = new SparseBooleanArray();
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;
    public int mSelectedColor = Color.parseColor("#FDFC9D");
    List<VerseHighlight> verseHighlights;
    public boolean removeHighlight = false;

    public class VerseViewHolder extends RecyclerView.ViewHolder {
        public TextView verseNumber;
        public TextView verseText;
        public AppCompatImageView verseBookmarkIcon;


        VerseViewHolder(final View view) {
            super(view);

            verseNumber = (TextView) view.findViewById(R.id.verse_number);
            verseText = (TextView) view.findViewById(R.id.verse_text);
            verseBookmarkIcon = (AppCompatImageView) view.findViewById(R.id.verse_bookmark_icon);
            verseNumber.setTextSize(textSize - 2);
            verseText.setTextSize(textSize);

            if (!typeFace.equals("Default")) {
                Typeface typeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/" + typeFace.toLowerCase() + ".ttf");
                verseNumber.setTypeface(typeface);
                verseText.setTypeface(typeface);
                Log.d(TAG, "Setting custom type face " + typeFace);
            }
        }
    }

    public VersesAdapter(List<Verse> verses, Context context) {
        this.verses = verses;
        this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        typeFace = preferences.getString(SettingsFragment.KEY_PREFS_TYPE_FACE, "Default");
        textSize = MIN_VERSE_TEXT_SIZE + preferences.getInt(SettingsFragment.KEY_PREFS_TEXT_SIZE, 4);
        actionModeCallback = new ActionModeCallback();
    }

    @Override
    public VerseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_verse, null);
        return new VerseViewHolder(listView);
    }

    @Override
    public void onBindViewHolder(final VerseViewHolder holder, final int position) {
        final Verse verse = verses.get(position);
        holder.verseNumber.setText(String.valueOf(verse.getVerseNumber()));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(verse.getText());
        int verseLength = verse.getText().length();

        if (verse.isBookmarked()) {
            holder.verseBookmarkIcon.setImageResource(R.drawable.ic_bookmark_red);
            holder.verseBookmarkIcon.setVisibility(View.VISIBLE);
        } else {
            holder.verseBookmarkIcon.setVisibility(View.GONE);
        }

        if (isSelected(position)) {
            stringBuilder.setSpan(new BackgroundColorSpan(ContextCompat.getColor(context,
                    R.color.colorSelectedVersesDayTheme)), 0, verseLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (verse.getHighlightColor() != null) {
            int highlightColor = Color.parseColor(verse.getHighlightColor());
            stringBuilder.setSpan(new BackgroundColorSpan(ColorUtils.setAlphaComponent(highlightColor, 130)),
                    0, verseLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            stringBuilder.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), 0, verseLength,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        holder.verseText.setText(stringBuilder);

        holder.verseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verseClicked(position);
                if (actionMode != null) {
                    toggleSelection(position);
                    toggleHighlightAction(position);
                }
            }
        });

        holder.verseText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setupActionMode(position);
                toggleSelection(position);
                toggleHighlightAction(position);
                return true;
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (context instanceof OnVerseClickedLister) {
            verseClickedLister = (OnVerseClickedLister) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnVerseClickedLister");
        }
    }

    @Override
    public int getItemCount() {
        return verses.size();
    }

    private void verseClicked(int position) {
        if (verseClickedLister != null) {
            verseClickedLister.onVerseClicked(verses.get(position));
        }
    }

    public interface OnVerseClickedLister {
        void onVerseClicked(Verse verse);
    }

    //VERSES SELECTION
    private void toggleSelection(int position) {

        if (selectedVerses.get(position, false)) {
            selectedVerses.delete(position);
        } else {
            selectedVerses.put(position, true);
        }

        notifyItemChanged(position);
        int count = getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private int getSelectedItemCount() {
        return selectedVerses.size();
    }

    private List<Integer> getSelectedItems() {
        int selectedTotal = selectedVerses.size();
        List<Integer> items = new ArrayList<>(selectedTotal);
        for (int i = 0; i < selectedTotal; i++) {
            items.add(selectedVerses.keyAt(i));
        }
        return items;
    }

    private void clearSelection() {
        List<Integer> selection = getSelectedItems();
        selectedVerses.clear();
        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    private boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_verses, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_copy:
                    copyVerses();
                    break;
                case R.id.action_share_verse:
                    shareVerses();
                    break;
                case R.id.action_highlight:
                    highlight();
                    break;
                case R.id.action_bookmark:
                    addBookmark();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearSelection();
            actionMode = null;
        }

        public String selectedVerseTexts() {
            List<Integer> selectedPositions = getSelectedItems();
            Verse firstSelectedVerse = verses.get(selectedPositions.get(0));
            String toSelect = firstSelectedVerse.getBookName() + " " + firstSelectedVerse.getChapterNumber();
            for (int position : selectedPositions) {
                Verse selectedVerse = verses.get(position);
                toSelect += "\n";
                toSelect += selectedVerse.getVerseNumber() + " " + selectedVerse.getText();
                toSelect += "\n";
            }
            return toSelect;
        }

        public void copyVerses() {
            String toCopy = selectedVerseTexts();
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Verses for Biblia Takatifu", toCopy.trim());
            clipboard.setPrimaryClip(clip);
            actionMode.finish();
            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_LONG).show();
        }

        public void shareVerses() {
            String shareText = selectedVerseTexts();
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.setType("text/plain");

            String title = context.getResources().getString(R.string.share_verses);
            Intent chooser = Intent.createChooser(shareIntent, title);
            chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(new Intent(chooser));
            }
        }
    }

    private void setVersesHighlightColor() {
        final DbHelper database = new DbHelper(context);
        int[] mColors = colorChoices(context);
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.color_picker_default_title, mColors, mSelectedColor, 4, ColorPickerDialog.SIZE_SMALL);
        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                mSelectedColor = color;

                List<Integer> selectedPositions = getSelectedItems();
                List<Integer> verseIds = new ArrayList<>();

                for (int position : selectedPositions) {
                    Verse selectedVerse = verses.get(position);
                    selectedVerse.setHighlightColor(Utils.formatColorToHex(mSelectedColor));
                    verseIds.add(selectedVerse.getId());
                }

                database.updateVerseHighlightColor(verseIds, Utils.formatColorToHex(mSelectedColor));
                notifyDataSetChanged();
                actionMode.finish();
                database.close();

                Log.d(TAG, "Selected color is " + Utils.formatColorToHex(mSelectedColor));
            }
        });

        colorPickerDialog.show(((Activity) context).getFragmentManager(), "set notes color");
    }

    private void removeVersesHighlightColor() {
        final DbHelper database = new DbHelper(context);
        List<Integer> selectedPositions = getSelectedItems();
        List<Integer> verseIds = new ArrayList<>();

        for (int position : selectedPositions) {
            Verse selectedVerse = verses.get(position);
            selectedVerse.setHighlightColor("#FFFFFFFF"); //transparent
            verseIds.add(selectedVerse.getId());
        }

        database.deleteVerseHighlight(verseIds);
        notifyDataSetChanged();
        actionMode.finish();
        database.close();
    }

    private void addBookmark() {
        DbHelper database = new DbHelper(context);

        List<Integer> selectedPositions = getSelectedItems();
        List<Bookmark> bookmarks = new ArrayList<>();

        for (int position : selectedPositions) {
            Bookmark bookmark = new Bookmark();
            Verse selectedVerse = verses.get(position);
            selectedVerse.setBookmarked(true);

            bookmark.setVerseId(selectedVerse.getId());
            bookmark.setBookNumber(selectedVerse.getBookNumber());
            bookmark.setChapterNumber(selectedVerse.getChapterNumber());
            bookmark.setVerseNumber(selectedVerse.getVerseNumber());
            bookmarks.add(bookmark);
        }

        database.addBookmarks(bookmarks);
        notifyDataSetChanged();
        actionMode.finish();
        Toast.makeText(context, R.string.bookmark_added, Toast.LENGTH_SHORT).show();
        database.close();
    }

    private void setupActionMode(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) context).startSupportActionMode(actionModeCallback);
            Verse mVerse = verses.get(position);
            DbHelper helper = new DbHelper(context);
            verseHighlights = helper.getChapterVerseHighlights(mVerse.getBookNumber(), mVerse.getChapterNumber());
        }
    }

    private void toggleHighlightAction(int position) {
        List<Integer> verseIds = new ArrayList<>();
        for (VerseHighlight verseHighlight : verseHighlights) {
            verseIds.add(verseHighlight.getVerseId());
        }

        if (actionMode != null) {

            MenuItem item = actionMode.getMenu().findItem(R.id.action_highlight);

            if (verseIds.contains(verses.get(position).getId())) {
                item.setIcon(R.drawable.ic_action_remote_highlight);
                item.setTitle(R.string.remove_highlights);
                removeHighlight = true;
            } else {
                item.setIcon(R.drawable.ic_action_set_color);
                item.setTitle(R.string.highlight);
                removeHighlight = false;
            }
        }
    }

    public void highlight() {
        if (removeHighlight) {
            removeVersesHighlightColor();
        } else {
            setVersesHighlightColor();
        }
    }
}
