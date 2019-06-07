package tz.co.wadau.bibleinafrikaans.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tz.co.wadau.bibleinafrikaans.data.DbContract.BookmarkEntry;
import tz.co.wadau.bibleinafrikaans.data.DbContract.NoteEntry;
import tz.co.wadau.bibleinafrikaans.data.DbContract.VerseHighlightEntry;
import tz.co.wadau.bibleinafrikaans.data.DbFileContract.BookEntry;
import tz.co.wadau.bibleinafrikaans.data.DbFileContract.VerseEntry;
import tz.co.wadau.bibleinafrikaans.model.Bookmark;
import tz.co.wadau.bibleinafrikaans.model.Note;
import tz.co.wadau.bibleinafrikaans.model.VerseHighlight;

public class DbHelper extends SQLiteOpenHelper {

    private final String TAG = DbHelper.class.getSimpleName();
    //Database name
    public static final String DATABASE_NAME = "bible_meta_data.db";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase mDataBase;
    private final Context mContext;

    private final String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME + " ( "
            + NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NoteEntry.COLUMN_TITLE + " TEXT, "
            + NoteEntry.COLUMN_TEXT + " TEXT, "
            + NoteEntry.COLUMN_COLOR + " TEXT  DEFAULT '#FDFC9D', "
            + NoteEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT (DATETIME('now','localtime')))";

    private final String SQL_CREATE_VERSE_HIGHLIGHT_TABLE = "CREATE TABLE " + VerseHighlightEntry.TABLE_NAME
            + " ( " + VerseHighlightEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VerseHighlightEntry.COLUMN_VERSE_ID + " INTEGER UNIQUE, "
            + VerseHighlightEntry.COLUMN_BOOK_NO + " INTEGER , "
            + VerseHighlightEntry.COLUMN_CHAPTER_NO + " INTEGER , "
            + VerseHighlightEntry.COLUMN_VERSE_NO + " INTEGER , "
            + VerseHighlightEntry.COLUMN_COLOR + " TEXT, "
            + VerseHighlightEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT (DATETIME('now','localtime')))";

    private final String SQL_CREATE_BOOKMARKS_TABLE = "CREATE TABLE " + BookmarkEntry.TABLE_NAME
            + " ( " + BookmarkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BookmarkEntry.COLUMN_VERSE_ID + " INTEGER UNIQUE, "
            + BookmarkEntry.COLUMN_BOOK_NO + " INTEGER , "
            + BookmarkEntry.COLUMN_CHAPTER_NO + " INTEGER , "
            + BookmarkEntry.COLUMN_VERSE_NO + " INTEGER , "
            + BookmarkEntry.COLUMN_TITLE + " TEXT, "
            + BookmarkEntry.COLUMN_COLOR + " TEXT, "
            + BookmarkEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT (DATETIME('now','localtime')))";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_NOTES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VERSE_HIGHLIGHT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_BOOKMARKS_TABLE);
        Log.d(TAG, "Database " + DATABASE_NAME + " created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        final String SQL_ADD_COLUMN_VERSE_ID = "ALTER TABLE " + VerseHighlightEntry.TABLE_NAME + " ADD COLUMN " + VerseHighlightEntry.COLUMN_BOOK_NO + " INTEGER";
        final String SQL_ADD_COLUMN_CHAPTER_NO = "ALTER TABLE " + VerseHighlightEntry.TABLE_NAME + " ADD COLUMN " + VerseHighlightEntry.COLUMN_CHAPTER_NO + " INTEGER";
        final String SQL_ADD_COLUMN_VERSE_NO = "ALTER TABLE " + VerseHighlightEntry.TABLE_NAME + " ADD COLUMN " + VerseHighlightEntry.COLUMN_VERSE_NO + " INTEGER";
        final String SQL_ADD_COLUMN_CREATED_AT = "ALTER TABLE " + VerseHighlightEntry.TABLE_NAME + " ADD COLUMN " + VerseHighlightEntry.COLUMN_CREATED_AT + " TEXT";
        final String SQL_UPDATE_HIGHLIGHTS = "UPDATE " + VerseHighlightEntry.TABLE_NAME + " SET "
                + VerseHighlightEntry.COLUMN_BOOK_NO + " = SUBSTR(" + VerseHighlightEntry.COLUMN_VERSE_ID + ", -8, 2), "
                + VerseHighlightEntry.COLUMN_CHAPTER_NO + " = SUBSTR(" + VerseHighlightEntry.COLUMN_VERSE_ID + ", -6, 3), "
                + VerseHighlightEntry.COLUMN_VERSE_NO + " = SUBSTR(" + VerseHighlightEntry.COLUMN_VERSE_ID + ", -3)";

        switch (oldVersion) {
            case 1:
                database.execSQL(SQL_ADD_COLUMN_VERSE_ID);
                database.execSQL(SQL_ADD_COLUMN_CHAPTER_NO);
                database.execSQL(SQL_ADD_COLUMN_VERSE_NO);
                database.execSQL(SQL_ADD_COLUMN_CREATED_AT);
                database.execSQL(SQL_UPDATE_HIGHLIGHTS);
        }
    }


    //NOTES
    public long insertNote(Note note) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_TITLE, note.getTitle());
        values.put(NoteEntry.COLUMN_TEXT, note.getText());
        values.put(NoteEntry.COLUMN_COLOR, note.getColor());


        //If note is present update it
        if (note.getId() > 0) {
            db.update(NoteEntry.TABLE_NAME, values, NoteEntry._ID + " =?",
                    new String[]{String.valueOf(note.getId())});
            Log.d(TAG, "Note with id " + note.getId() + " updated!");
            db.close();
            return note.getId();
        } else {
            //Else update it
            long noteId = db.insert(NoteEntry.TABLE_NAME, null, values);
            Log.d(TAG, "Note inserted with id " + noteId);
            db.close();
            return noteId;
        }
    }

    public Note getNoteById(Long id) {
        SQLiteDatabase database = getReadableDatabase();
        final String SQL_SELECT_NOTE_BY_DATE = "SELECT * FROM " + NoteEntry.TABLE_NAME
                + " WHERE " + NoteEntry._ID + " = '" + id + "';";

        Cursor c = database.rawQuery(SQL_SELECT_NOTE_BY_DATE, null);
        Note note = new Note();

        if (c.moveToFirst()) {
            note.setId(c.getInt(c.getColumnIndex(NoteEntry._ID)));
            note.setTitle(c.getString(c.getColumnIndex(NoteEntry.COLUMN_TITLE)));
            note.setText(c.getString(c.getColumnIndex(NoteEntry.COLUMN_TEXT)));
            note.setColor(c.getString(c.getColumnIndex(NoteEntry.COLUMN_COLOR)));
            note.setCreatedAt(c.getString(c.getColumnIndex(NoteEntry.COLUMN_CREATED_AT)));
        }
        c.close();
        database.close();
        return note;
    }

    public List<Note> getAllNotes() {
        SQLiteDatabase database = getReadableDatabase();
        List<Note> notes = new ArrayList<>();

        final String SQL_SELECT_ALL_NOTES = "SELECT * FROM " + NoteEntry.TABLE_NAME
                + " ORDER BY " + NoteEntry.COLUMN_CREATED_AT + " DESC";
        Cursor c = database.rawQuery(SQL_SELECT_ALL_NOTES, null);

        if (c.moveToFirst()) {
            do {
                Note note = new Note();
                note.setId(c.getInt(c.getColumnIndex(NoteEntry._ID)));
                note.setText(c.getString(c.getColumnIndex(NoteEntry.COLUMN_TEXT)));
                note.setTitle(c.getString(c.getColumnIndex(NoteEntry.COLUMN_TITLE)));
                note.setColor(c.getString(c.getColumnIndex(NoteEntry.COLUMN_COLOR)));
                note.setCreatedAt(c.getString(c.getColumnIndex(NoteEntry.COLUMN_CREATED_AT)));
                notes.add(note);
            } while (c.moveToNext());
        }

        c.close();
        database.close();
        return notes;
    }

    public void deleteNotes(List<Integer> noteIds) {
        SQLiteDatabase db = getReadableDatabase();
        int noteSize = noteIds.size();
        db.beginTransaction();
        try {
            for (int z = 0; z < noteSize; z++) {
                db.delete(NoteEntry.TABLE_NAME, NoteEntry._ID + " =?", new String[]{String.valueOf(noteIds.get(z))});
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "Deleted notes " + noteSize);

        } finally {
            db.endTransaction();
        }

        db.close();
    }

    public void updateNotesColor(List<Integer> noteIds, String color) {
        SQLiteDatabase db = getReadableDatabase();
        int notesSize = noteIds.size();
        ContentValues values = new ContentValues();
        values.put(NoteEntry.COLUMN_COLOR, color);

        db.beginTransaction();
        try {
            for (int r = 0; r < notesSize; r++) {
                db.update(NoteEntry.TABLE_NAME, values, NoteEntry._ID + " =?", new String[]{String.valueOf(noteIds.get(r))});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    //VERSES
    public void deleteVerseHighlight(List<Integer> verseIds) {
        SQLiteDatabase db = getReadableDatabase();
        int versesSize = verseIds.size();

        db.beginTransaction();
        try {
            for (int r = 0; r < versesSize; r++) {

                db.delete(VerseHighlightEntry.TABLE_NAME, VerseHighlightEntry.COLUMN_VERSE_ID + " = " + verseIds.get(r), null);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void clearAllVerseHighlights() {
        SQLiteDatabase db = getReadableDatabase();
        db.delete(VerseHighlightEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void addBookmarks(List<Bookmark> bookmarks) {
        SQLiteDatabase db = getReadableDatabase();
        int bookmarksSize = bookmarks.size();

        db.beginTransaction();
        try {
            for (int r = 0; r < bookmarksSize; r++) {
                ContentValues values = new ContentValues();
                values.put(BookmarkEntry.COLUMN_VERSE_ID, bookmarks.get(r).getVerseId());
                values.put(BookmarkEntry.COLUMN_BOOK_NO, bookmarks.get(r).getBookNumber());
                values.put(BookmarkEntry.COLUMN_CHAPTER_NO, bookmarks.get(r).getChapterNumber());
                values.put(BookmarkEntry.COLUMN_VERSE_NO, bookmarks.get(r).getVerseNumber());
                db.replace(BookmarkEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    //BOOKMARKS
    public List<Bookmark> getAllBookmarks() {
        DbFileHelper dbFileHelper = new DbFileHelper(mContext);
        SQLiteDatabase database = dbFileHelper.getReadableDatabase();
        List<Bookmark> bookmarks = new ArrayList<>();

        final String DB2_PATH = mContext.getDatabasePath(DbHelper.DATABASE_NAME).getPath();
        database.execSQL("ATTACH DATABASE '" + DB2_PATH + "' AS db2");

        final String SQL_SELECT_ALL_BOOKMARKS = "SELECT " + BookmarkEntry._ID + ", " + BookEntry.COLUMN_BOOK_B
                + ", " + BookEntry.COLUMN_BOOK_N + ", " + BookmarkEntry.COLUMN_CHAPTER_NO
                + ", " + BookmarkEntry.COLUMN_VERSE_NO + ", " + BookmarkEntry.COLUMN_CREATED_AT
                + "  FROM db2." + BookmarkEntry.TABLE_NAME + " AS bm " + "LEFT JOIN " + BookEntry.TABLE_NAME
                + " ON bm." + BookmarkEntry.COLUMN_BOOK_NO + " = " + BookEntry.COLUMN_BOOK_B
                + " ORDER BY " + BookmarkEntry.COLUMN_CREATED_AT + " DESC";

        Log.d(TAG, SQL_SELECT_ALL_BOOKMARKS);

        Cursor c = database.rawQuery(SQL_SELECT_ALL_BOOKMARKS, null);

        if (c.moveToFirst()) {
            do {
                Bookmark bookmark = new Bookmark();
                bookmark.setId(c.getInt(c.getColumnIndex(BookmarkEntry._ID)));
                bookmark.setBookNumber(c.getInt(c.getColumnIndex(BookEntry.COLUMN_BOOK_B)));
                bookmark.setBookName(c.getString(c.getColumnIndex(BookEntry.COLUMN_BOOK_N)));
                bookmark.setChapterNumber(c.getInt(c.getColumnIndex(BookmarkEntry.COLUMN_CHAPTER_NO)));
                bookmark.setVerseNumber(c.getInt(c.getColumnIndex(BookmarkEntry.COLUMN_VERSE_NO)));
                bookmark.setCreatedDate(c.getString(c.getColumnIndex(BookmarkEntry.COLUMN_CREATED_AT)));

                bookmarks.add(bookmark);
            } while (c.moveToNext());
        }

        c.close();
        database.close();
        return bookmarks;
    }

    public void deleteBookmarks(List<Integer> bookmarksIds) {
        SQLiteDatabase db = getReadableDatabase();
        int bookmarksSize = bookmarksIds.size();
        db.beginTransaction();
        try {
            for (int z = 0; z < bookmarksSize; z++) {
                db.delete(BookmarkEntry.TABLE_NAME, BookmarkEntry._ID + " =?", new String[]{String.valueOf(bookmarksIds.get(z))});
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "Deleted bookmarks " + bookmarksSize);

        } finally {
            db.endTransaction();
        }

        db.close();
    }

    //    HIGHLIGHTS
    public List<VerseHighlight> getAllVerseHighlights() {
        DbFileHelper dbFileHelper = new DbFileHelper(mContext);
        SQLiteDatabase db = dbFileHelper.getReadableDatabase();
        List<VerseHighlight> highlights = new ArrayList<>();

        final String DB2_PATH = mContext.getDatabasePath(DbHelper.DATABASE_NAME).getPath();
        db.execSQL("ATTACH DATABASE '" + DB2_PATH + "' AS db2");

        final String SQL_QUERY_ALL_HIGHLIGHTS = "SELECT * FROM db2." + VerseHighlightEntry.TABLE_NAME
                + " AS h LEFT JOIN " + BookEntry.TABLE_NAME + " AS k ON "
                + " h." + VerseHighlightEntry.COLUMN_BOOK_NO + " = k." + BookEntry.COLUMN_BOOK_B
                + " INNER JOIN " + VerseEntry.TABLE_NAME + " AS v ON v." + VerseEntry.COLUMN_VERSE_ID
                + " = h." + VerseHighlightEntry.COLUMN_VERSE_ID + " ORDER BY "
                + VerseHighlightEntry._ID + " DESC";

        Log.d(TAG, SQL_QUERY_ALL_HIGHLIGHTS);

        Cursor c = db.rawQuery(SQL_QUERY_ALL_HIGHLIGHTS, null);

        if (c.moveToFirst()) {
            do {
                VerseHighlight highlight = new VerseHighlight();
                highlight.setId(c.getInt(c.getColumnIndex(VerseHighlightEntry._ID)));
                highlight.setVerseId(c.getInt(c.getColumnIndex(VerseHighlightEntry.COLUMN_VERSE_ID)));
                highlight.setBookNumber(c.getInt(c.getColumnIndex(VerseHighlightEntry.COLUMN_BOOK_NO)));
                highlight.setBookName(c.getString(c.getColumnIndex(BookEntry.COLUMN_BOOK_N)));
                highlight.setChapterNumber(c.getInt(c.getColumnIndex(VerseHighlightEntry.COLUMN_CHAPTER_NO)));
                highlight.setVerseNumber(c.getInt(c.getColumnIndex(VerseHighlightEntry.COLUMN_VERSE_NO)));
                highlight.setVerseText(c.getString(c.getColumnIndex(VerseEntry.COLUMN_VERSE_T)));
                highlight.setCreatedAt(c.getString(c.getColumnIndex(VerseHighlightEntry.COLUMN_CREATED_AT)));
                highlight.setColor(c.getString(c.getColumnIndex(VerseHighlightEntry.COLUMN_COLOR)));

                highlights.add(highlight);
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return highlights;
    }

    public void updateVerseHighlightColor(List<Integer> verseIds, String color) {
        SQLiteDatabase db = getReadableDatabase();
        int versesSize = verseIds.size();

        db.beginTransaction();
        try {
            for (int r = 0; r < versesSize; r++) {
                ContentValues values = new ContentValues();
                String mVerseId = verseIds.get(r).toString();
                int idLength = mVerseId.length();
                Log.d(TAG, "Verse id " + mVerseId);

                String bookNumber = mVerseId.substring(0, idLength - 6);
                String chapterNumber = mVerseId.substring(idLength - 6, idLength - 3);
                String verseNumber = mVerseId.substring(idLength - 3);

                Log.d(TAG, "Expl book no " + bookNumber + " chapter no " + chapterNumber + " verse no " + verseNumber);

                values.put(VerseHighlightEntry.COLUMN_VERSE_ID, verseIds.get(r));
                values.put(VerseHighlightEntry.COLUMN_BOOK_NO, bookNumber);
                values.put(VerseHighlightEntry.COLUMN_CHAPTER_NO, chapterNumber);
                values.put(VerseHighlightEntry.COLUMN_VERSE_NO, verseNumber);
                values.put(VerseHighlightEntry.COLUMN_COLOR, color);

                db.replace(VerseHighlightEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public List<VerseHighlight> getChapterVerseHighlights(int bookNo, int chapterNo) {
        SQLiteDatabase db = getReadableDatabase();
        List<VerseHighlight> highlights = new ArrayList<>();

        String padded = bookNo + "000".substring(String.valueOf(chapterNo).length()) + chapterNo;

        final String SQL_SELECT_CHAPTER_HIGHLIGHTS = "SELECT * FROM " + VerseHighlightEntry.TABLE_NAME
                + " WHERE " + VerseHighlightEntry.COLUMN_VERSE_ID + " LIKE '" + padded + "%'";

        Cursor c = db.rawQuery(SQL_SELECT_CHAPTER_HIGHLIGHTS, null);

        if (c.moveToFirst()) {
            do {
                VerseHighlight highlight = new VerseHighlight();
                highlight.setId(c.getInt(c.getColumnIndex(VerseHighlightEntry._ID)));
                highlight.setVerseId(c.getInt(c.getColumnIndex(VerseHighlightEntry.COLUMN_VERSE_ID)));
                highlight.setColor(c.getString(c.getColumnIndex(VerseHighlightEntry.COLUMN_COLOR)));

                highlights.add(highlight);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return highlights;
    }
}
