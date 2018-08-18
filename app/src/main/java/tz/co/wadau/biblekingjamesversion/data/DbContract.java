package tz.co.wadau.biblekingjamesversion.data;

import android.provider.BaseColumns;

class DbContract {

    //Notes
    public static class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";

        //Columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_CREATED_AT = "created_at";
    }

    //Verse highlight colors
    public static class VerseHighlightEntry implements BaseColumns {
        public static final String TABLE_NAME = "verses_highlight_color";

        //Columns
        public static final String COLUMN_VERSE_ID = "verse_id";
        public static final String COLUMN_BOOK_NO = "book_number";
        public static final String COLUMN_CHAPTER_NO = "chapter_number";
        public static final String COLUMN_VERSE_NO = "verse_number";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_COLOR = "color";
    }

    //Bookmark
    public static class BookmarkEntry implements BaseColumns {
        public static final String TABLE_NAME = "bookmarks";

        //Columns
        public  static final String COLUMN_VERSE_ID = "verse_id";
        public static final String COLUMN_BOOK_NO = "book_number";
        public static final String COLUMN_CHAPTER_NO = "chapter_number";
        public static final String COLUMN_VERSE_NO = "verse_number";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_CREATED_AT = "created_at";
    }
}
