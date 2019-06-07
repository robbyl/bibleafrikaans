package tz.co.wadau.bibleinafrikaans.data;

import android.content.Context;
import android.util.Log;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tz.co.wadau.bibleinafrikaans.model.Book;
import tz.co.wadau.bibleinafrikaans.model.BookSuggestion;

public class DataHelper {

    private static final String TAG = "DataHelperClass";
    private static List<Book> mBooks = new ArrayList<>();
    private static List<Book> mOldStmntBooks = new ArrayList<>();
    private static List<Book> mNewStmntBooks = new ArrayList<>();

    private static List<BookSuggestion> sBookSuggestions = new ArrayList<>();

    public interface OnFindBooksListener {
        void onResults(List<Book> results);
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<BookSuggestion> results);
    }

    public static List<BookSuggestion> getHistory(Context context, int currBooksTabPosition, int count) {

        initBookWrapperList(context, currBooksTabPosition);

        List<BookSuggestion> suggestionList = new ArrayList<>();
        BookSuggestion bookSuggestion;
        for (int i = 0; i < count; i++) {
            bookSuggestion = new BookSuggestion(mBooks.get(i).getName());
            bookSuggestion.setIsHistory(true);
            suggestionList.add(bookSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    private static void resetSuggestionsHistory() {
        for (BookSuggestion bookSuggestion : sBookSuggestions) {
            bookSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, int currBooksTabPosition, String query, final int limit,
                                       final OnFindSuggestionsListener listener) {
        initBookWrapperList(context, currBooksTabPosition);

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                DataHelper.resetSuggestionsHistory();
                List<BookSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (Book book : mBooks) {
                        if (book.getName().toUpperCase().startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(new BookSuggestion(book.getName()));
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<BookSuggestion>() {
                    @Override
                    public int compare(BookSuggestion lhs, BookSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<BookSuggestion>) results.values);
                }
            }
        }.filter(query);

    }

    public static void findBooks(Context context, int currBooksTabPosition, String query, final OnFindBooksListener listener) {
        initBookWrapperList(context, currBooksTabPosition);

        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {


                List<Book> suggestionList = new ArrayList<>();

                if (!(constraint == null || constraint.length() == 0)) {

                    for (Book bookWrapper : mBooks) {
                        if (bookWrapper.getName().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(bookWrapper);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<Book>) results.values);
                }
            }
        }.filter(query);

    }

    private static void initBookWrapperList(Context context, int currBooksTabPosition) {
        DbFileHelper db = new DbFileHelper(context);

        if (mOldStmntBooks.isEmpty()) {
            mOldStmntBooks = db.getOldStatementBooks();
            Log.d(TAG, "Initializing old statement suggestion list");
        }

        if (mNewStmntBooks.isEmpty()) {
            mNewStmntBooks = db.getNewStatementBooks();
            Log.d(TAG, "Initializing new statement suggestion list");
        }

        if (currBooksTabPosition == 0) {
            mBooks = mOldStmntBooks;
        } else if (currBooksTabPosition == 1) {
            mBooks = mNewStmntBooks;
        }
    }
}