package tz.co.wadau.bibleafrikaans.data;

import android.provider.BaseColumns;

class DbFileContract {

    public static final class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "key_english";

        public static final String COLUMN_BOOK_B = "b";
        public static final String COLUMN_BOOK_N = "n";

    }

    public static final class ChapterEntry implements BaseColumns {
        public static final String TABLE_NAME = "t_sw";

        public static final String COLUMN_CHAPTER_B = "b";
        public static final String COLUMN_CHAPTER_C = "c";
    }

    public static final class VerseEntry implements BaseColumns {
        public static final String TABLE_NAME = "t_sw";

        public static final String COLUMN_VERSE_ID = "id";
        public static final String COLUMN_VERSE_B = "b";
        public static final String COLUMN_VERSE_C = "c";
        public static final String COLUMN_VERSE_V = "v";
        public static final String COLUMN_VERSE_T = "t";
    }

    public static final class SpecialVerseEntry implements BaseColumns {
        public static final String TABLE_NAME = "special_verses";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_VERSE_CATEGORY_ID = "verse_category_id";
        public static final String COLUMN_VERSE_NO = "verse_no";
        public static final String COLUMN_VERSE_TEXT = "verse_text";
    }

    public static final class SpecialVerseCategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "special_verses_category";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_CATEGORY = "category";
    }
}
