package tz.co.wadau.bibleinafrikaans.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.data.DbHelper;
import tz.co.wadau.bibleinafrikaans.model.VerseHighlight;


public class VerseHighlightsAdapter extends RecyclerView.Adapter<VerseHighlightsAdapter.VerseHighlightViewHolder> {
    
    private final String TAG = VerseHighlightsAdapter.class.getSimpleName();
    private List<VerseHighlight> verseHighlights;
    private Context mContext;
    private OnVerseHighlightClickListener verseHighlightClickListener;
    private SparseBooleanArray selectedVerseHighlights = new SparseBooleanArray();
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    public class VerseHighlightViewHolder extends RecyclerView.ViewHolder {
        public TextView verseHighlightHeader;
        private TextView verseHighlighted;
        private RelativeLayout verseHighlightsWrapper;

        public VerseHighlightViewHolder(View view) {
            super(view);
            verseHighlightHeader = (TextView) view.findViewById(R.id.verse_highlight_header);
            verseHighlighted = (TextView) view.findViewById(R.id.verse_highlighted);
            verseHighlightsWrapper = (RelativeLayout) view.findViewById(R.id.verse_highlight_wrapper);
        }
    }

    public VerseHighlightsAdapter(List<VerseHighlight> verseHighlights, Context context) {
        this.verseHighlights = verseHighlights;
        this.mContext = context;
        actionModeCallback = new ActionModeCallback();

        if (mContext instanceof OnVerseHighlightClickListener) {
            verseHighlightClickListener = (OnVerseHighlightClickListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString() + " must implement OnVerseHighlightClickListener");
        }
    }

    @Override
    public VerseHighlightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View bookView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_verse_highlight, null);
        return new VerseHighlightViewHolder(bookView);
    }

    @Override
    public void onBindViewHolder(VerseHighlightViewHolder holder, final int position) {
        VerseHighlight verseHighlight = verseHighlights.get(position);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(verseHighlight.getVerseText());
        int verseLength = verseHighlight.getVerseText().length();
        int highlightColor = Color.parseColor(verseHighlight.getColor());
        stringBuilder.setSpan(new BackgroundColorSpan(ColorUtils.setAlphaComponent(highlightColor, 130)),
                0, verseLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        holder.verseHighlightHeader.setText(verseHighlight.getBookName() + " " + verseHighlight.getChapterNumber() + ":" + verseHighlight.getVerseNumber());
        holder.verseHighlighted.setText(stringBuilder);
        toggleSelectionBackround(holder, position);

        holder.verseHighlightsWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (actionMode != null) {
                    toggleSelection(position);
                } else {
                    verseHighlightClicked(position);
                }
                Log.d(TAG, "VerseHighlight " + position + " clicked");
            }
        });

        holder.verseHighlightsWrapper.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (actionMode == null) {
                    actionMode = ((AppCompatActivity) mContext).startSupportActionMode(actionModeCallback);
                }

                toggleSelection(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return verseHighlights.size();
    }

    public void filterVerseHighlights(List<VerseHighlight> fverseHighlights) {
        this.verseHighlights = fverseHighlights;
        notifyDataSetChanged();
    }

    public interface OnVerseHighlightClickListener {
        void onVerseHighlightClicked(VerseHighlight verseHighlight);
    }

    private void verseHighlightClicked(int position) {
        if (verseHighlightClickListener != null) {
            verseHighlightClickListener.onVerseHighlightClicked(verseHighlights.get(position));
        }
    }

    //BOOKMARKS SELECTION
    private void toggleSelection(int position) {

        if (selectedVerseHighlights.get(position, false)) {
            selectedVerseHighlights.delete(position);
        } else {
            selectedVerseHighlights.put(position, true);
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
        return selectedVerseHighlights.size();
    }

    private List<Integer> getSelectedVerseHighlights() {
        int selectedTotal = selectedVerseHighlights.size();
        List<Integer> items = new ArrayList<>();

        for (int i = 0; i < selectedTotal; i++) {
            items.add(selectedVerseHighlights.keyAt(i));
        }
        return items;
    }

    private void clearSelection() {
        List<Integer> selection = getSelectedVerseHighlights();
        selectedVerseHighlights.clear();

        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    private boolean isSelected(int position) {
        return getSelectedVerseHighlights().contains(position);
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_verse_highlight, menu);
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
                    deleteSelectedVerseHighlights(mode);
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            clearSelection();
            actionMode = null;
        }

    }

    private void toggleSelectionBackround(VerseHighlightViewHolder holder, int position){
        if(isSelected(position)){
            holder.verseHighlightsWrapper.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorSelectedNotes));
        }else {
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.verseHighlightsWrapper.setBackgroundResource(outValue.resourceId);
        }
    }

    private void deleteSelectedVerseHighlights(final ActionMode mode) {

        final DbHelper dbHelper = new DbHelper(mContext);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String dialogTitle = getSelectedItemCount() > 1 ? mContext.getString(R.string.delete_verse_highlights) : mContext.getString(R.string.delete_verse_highlight);

        builder.setTitle(dialogTitle)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Integer> selectedVerseHighlights = getSelectedVerseHighlights();
                        int selectedSize = getSelectedItemCount();
                        List<Integer> verseHighlightIds = new ArrayList<>();

                        for (int m = 0; m < selectedSize; m++) {
                            verseHighlightIds.add(verseHighlights.get(selectedVerseHighlights.get(m)).getVerseId());
                        }

                        removeItems(selectedVerseHighlights);
                        mode.finish();
                        dialog.cancel();
                        dbHelper.deleteVerseHighlight(verseHighlightIds);
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

    private void removeItem(int position) {
        verseHighlights.remove(position);
        notifyItemRemoved(position);
    }

    private void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            verseHighlights.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }
}
