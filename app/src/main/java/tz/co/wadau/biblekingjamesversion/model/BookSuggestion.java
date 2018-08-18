package tz.co.wadau.biblekingjamesversion.model;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;


public class BookSuggestion implements SearchSuggestion {
    private String bookName;
    private boolean isHistory = false;

    public BookSuggestion(String bookName) {
        this.bookName = bookName;
    }

    public BookSuggestion(Parcel source) {
        this.bookName = source.readString();
        this.isHistory = source.readInt() != 0;
    }

    public static final Creator<BookSuggestion> CREATOR = new Creator<BookSuggestion>() {
        @Override
        public BookSuggestion createFromParcel(Parcel in) {
            return new BookSuggestion(in);
        }

        @Override
        public BookSuggestion[] newArray(int size) {
            return new BookSuggestion[size];
        }
    };

    public String getBookName() {
        return bookName;
    }

    public boolean getIsHistory() {
        return isHistory;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setIsHistory(boolean history) {
        isHistory = history;
    }

    @Override
    public String getBody() {
        return bookName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookName);
        dest.writeInt(isHistory ? 1 : 0);
    }
}
