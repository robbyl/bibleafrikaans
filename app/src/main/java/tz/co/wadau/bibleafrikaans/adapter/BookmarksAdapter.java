package tz.co.wadau.bibleafrikaans.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
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

import tz.co.wadau.bibleafrikaans.R;
import tz.co.wadau.bibleafrikaans.data.DbHelper;
import tz.co.wadau.bibleafrikaans.model.Bookmark;
import tz.co.wadau.bibleafrikaans.utils.Utils;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarkViewHolder> {
    private final String TAG = BookmarksAdapter.class.getSimpleName();
    private List<Bookmark> bookmarks;
    private Context mContext;
    private OnBookmarkClickListener bookmarkClickListener;
    private SparseBooleanArray selectedBookmarks = new SparseBooleanArray();
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    public class BookmarkViewHolder extends RecyclerView.ViewHolder {
        public TextView bookmarkHeader;
        private TextView bookmarkDate;
        private RelativeLayout bookmarksWrapper;

        public BookmarkViewHolder(View view) {
            super(view);
            bookmarkHeader = (TextView) view.findViewById(R.id.bookmark_header);
            bookmarkDate = (TextView) view.findViewById(R.id.bookmark_date);
            bookmarksWrapper = (RelativeLayout) view.findViewById(R.id.bookmark_wrapper);
        }
    }

    public BookmarksAdapter(List<Bookmark> bookmarks, Context context) {
        this.bookmarks = bookmarks;
        this.mContext = context;
        actionModeCallback = new ActionModeCallback();

        if (mContext instanceof OnBookmarkClickListener) {
            bookmarkClickListener = (OnBookmarkClickListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString() + " must implement OnBookmarkClickListener");
        }
    }

    @Override
    public BookmarksAdapter.BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View bookView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bookmark, null);
        return new BookmarkViewHolder(bookView);
    }

    @Override
    public void onBindViewHolder(BookmarksAdapter.BookmarkViewHolder holder, final int position) {
        Bookmark bookmark = bookmarks.get(position);
        holder.bookmarkHeader.setText(bookmark.getBookName() + " " + bookmark.getChapterNumber() + ":" + bookmark.getVerseNumber());
        holder.bookmarkDate.setText(Utils.formatDateLongFormat(bookmark.getCreatedDate()));
        toggleSelectionBackround(holder, position);

        holder.bookmarksWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (actionMode != null) {
                    toggleSelection(position);
                } else {
                    bookmarkClicked(position);
                }
                Log.d(TAG, "Bookmark " + position + " clicked");
            }
        });

        holder.bookmarksWrapper.setOnLongClickListener(new View.OnLongClickListener() {
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
        return bookmarks.size();
    }

    public void filterBookmarks(List<Bookmark> fbookmarks) {
        this.bookmarks = fbookmarks;
        notifyDataSetChanged();
    }

    public interface OnBookmarkClickListener {
        void onBookmarkClicked(Bookmark bookmark);
    }

    private void bookmarkClicked(int position) {
        if (bookmarkClickListener != null) {
            bookmarkClickListener.onBookmarkClicked(bookmarks.get(position));
        }
    }

    //BOOKMARKS SELECTION
    private void toggleSelection(int position) {

        if (selectedBookmarks.get(position, false)) {
            selectedBookmarks.delete(position);
        } else {
            selectedBookmarks.put(position, true);
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
        return selectedBookmarks.size();
    }

    private List<Integer> getSelectedBookmarks() {
        int selectedTotal = selectedBookmarks.size();
        List<Integer> items = new ArrayList<>();

        for (int i = 0; i < selectedTotal; i++) {
            items.add(selectedBookmarks.keyAt(i));
        }
        return items;
    }

    private void clearSelection() {
        List<Integer> selection = getSelectedBookmarks();
        selectedBookmarks.clear();

        for (Integer i : selection) {
            notifyItemChanged(i);
        }
    }

    private boolean isSelected(int position) {
        return getSelectedBookmarks().contains(position);
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.selected_bookmark, menu);
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
                    deleteSelectedBookmarks(mode);
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

    private void toggleSelectionBackround(BookmarkViewHolder holder, int position){
        if(isSelected(position)){
            holder.bookmarksWrapper.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorSelectedNotes));
        }else {
            TypedValue outValue = new TypedValue();
            mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.bookmarksWrapper.setBackgroundResource(outValue.resourceId);
        }
    }

    private void deleteSelectedBookmarks(final ActionMode mode) {

        final DbHelper dbHelper = new DbHelper(mContext);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String dialogTitle = getSelectedItemCount() > 1 ? mContext.getString(R.string.delete_bookmarks) : mContext.getString(R.string.delete_bookmark);

        builder.setTitle(dialogTitle)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Integer> selectedBookmarks = getSelectedBookmarks();
                        int selectedSize = getSelectedItemCount();
                        List<Integer> bookmarkIds = new ArrayList<>();

                        for (int m = 0; m < selectedSize; m++) {
                            bookmarkIds.add(bookmarks.get(selectedBookmarks.get(m)).getId());
                        }

                        removeItems(selectedBookmarks);
                        mode.finish();
                        dialog.cancel();
                        dbHelper.deleteBookmarks(bookmarkIds);
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
        bookmarks.remove(position);
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
            bookmarks.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }
}
