package tz.co.wadau.bibleinafrikaans.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import tz.co.wadau.bibleinafrikaans.data.DbContract.BookmarkEntry;
import tz.co.wadau.bibleinafrikaans.data.DbContract.VerseHighlightEntry;
import tz.co.wadau.bibleinafrikaans.data.DbFileContract.BookEntry;
import tz.co.wadau.bibleinafrikaans.data.DbFileContract.ChapterEntry;
import tz.co.wadau.bibleinafrikaans.data.DbFileContract.SpecialVerseEntry;
import tz.co.wadau.bibleinafrikaans.data.DbFileContract.VerseEntry;
import tz.co.wadau.bibleinafrikaans.model.Book;
import tz.co.wadau.bibleinafrikaans.model.Chapter;
import tz.co.wadau.bibleinafrikaans.model.SpecialVerse;
import tz.co.wadau.bibleinafrikaans.model.Verse;

public class DbFileHelper extends SQLiteOpenHelper {
    private static String TAG = DbFileHelper.class.getSimpleName();
    public static final String PREFS_KEY_DB_VER = "prefs_db_version";

    //Database name
    private static final String DATABASE_NAME = "bible_afrikaans.db";
    //destination path (location) of database on device
    private static String DATABASE_PATH;

    //Database version
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase mDataBase;
    private final Context mContext;

    public DbFileHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        DATABASE_PATH = context.getDatabasePath(DATABASE_NAME).toString();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Do nothing
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Upgrade is handled in initializeBibleDb() BibleActivity.class
    }

    @Override
    public synchronized void close() {

        if (mDataBase != null)
            mDataBase.close();

        super.close();
    }

    public void createDatabase() {
        boolean isDbPresent = isDbPresent();

        if (isDbPresent) {
            //Do nothing database is already present
            Log.d(TAG, "Database is present no need to create one");
        } else {
            try {
                copyDatabase();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(PREFS_KEY_DB_VER, DATABASE_VERSION);
                editor.apply();
                Log.d(TAG, DATABASE_NAME + " database copied from assets");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void openDataBase() throws SQLException {

        //Open the database
        mDataBase = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
    }

    public boolean isDbPresent() {
        SQLiteDatabase mDataBase = null;
        try {
            mDataBase = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READONLY);
            close();
        } catch (SQLException e) {
            //Database is not present yet.
        }

        return mDataBase != null;
    }

    private void copyDatabase() throws IOException {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P)
            getReadableDatabase();
        //Open local db (the assets folder) as the input stream
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME);

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(DATABASE_PATH);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void initializeDb() {

        // INITIALIZING TENZI DB
//        DbFileHelper db = new DbFileHelper(mContext);

        if (isDbPresent()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            int dbVersion = prefs.getInt(PREFS_KEY_DB_VER, 1);

            if (getVersion() != dbVersion) {
                File dbFile = mContext.getDatabasePath(getName());
                if (!dbFile.delete()) {
                    Log.w(TAG, "Unable to update database");
                } else {
                    Log.d(TAG, "Current db version " + dbVersion + " Deleted");
                }
            }
        }

        createDatabase();
        close();

        //INITIALIZING TENZI METADATA DB
        DbHelper dbHelper = new DbHelper(mContext);
        dbHelper.getReadableDatabase();
        dbHelper.close();
    }

    public Book getBook(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Book book = new Book();

        final String SQL_SELECT_BOOK = "SELECT b, n FROM " + BookEntry.TABLE_NAME + " WHERE b = " + id;
        Cursor cursor = db.rawQuery(SQL_SELECT_BOOK, null);
        if (cursor.moveToFirst()) {
            book.setNumber(cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_B)));
            book.setName(cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_N)));
        }
        cursor.close();
        db.close();
        return book;
    }

    public List<Book> getBooks() {
        SQLiteDatabase db = getReadableDatabase();
        List<Book> books = new ArrayList<>();

        final String SQL_SELECT_BOOKS = "SELECT b, n FROM " + BookEntry.TABLE_NAME;
        Cursor c = db.rawQuery(SQL_SELECT_BOOKS, null);
        if (c.moveToFirst()) {
            do {
                Book bk = new Book();
                bk.setNumber(c.getInt(c.getColumnIndex(BookEntry.COLUMN_BOOK_B)));
                bk.setName(c.getString(c.getColumnIndex(BookEntry.COLUMN_BOOK_N)));
                books.add(bk);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return books;
    }

    public List<Book> getOldStatementBooks() {
        SQLiteDatabase db = getReadableDatabase();
        List<Book> books = new ArrayList<>();

        final String SQL_SELECT_BOOKS = "SELECT b, n FROM " + BookEntry.TABLE_NAME + " LIMIT 0, 39";
        Cursor c = db.rawQuery(SQL_SELECT_BOOKS, null);
        if (c.moveToFirst()) {
            do {
                Book bk = new Book();
                bk.setNumber(c.getInt(c.getColumnIndex(BookEntry.COLUMN_BOOK_B)));
                bk.setName(c.getString(c.getColumnIndex(BookEntry.COLUMN_BOOK_N)));
                books.add(bk);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return books;
    }

    public List<Book> getNewStatementBooks() {
        SQLiteDatabase db = getReadableDatabase();
        List<Book> books = new ArrayList<>();

        final String SQL_SELECT_BOOKS = "SELECT b, n FROM " + BookEntry.TABLE_NAME + " LIMIT 39, 66";
        Cursor c = db.rawQuery(SQL_SELECT_BOOKS, null);
        if (c.moveToFirst()) {
            do {
                Book bk = new Book();
                bk.setNumber(c.getInt(c.getColumnIndex(BookEntry.COLUMN_BOOK_B)));
                bk.setName(c.getString(c.getColumnIndex(BookEntry.COLUMN_BOOK_N)));
                books.add(bk);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return books;
    }

    public List<Chapter> getChapters(int bookId) {
        SQLiteDatabase db = getReadableDatabase();
        List<Chapter> chapters = new ArrayList<>();

        final String SQL_SELECT_CHAPTERS = "SELECT DISTINCT k." + ChapterEntry.COLUMN_CHAPTER_B
                + ", k." + BookEntry.COLUMN_BOOK_N + ", "
                + ChapterEntry.COLUMN_CHAPTER_C + " FROM " + ChapterEntry.TABLE_NAME + " AS t"
                + " INNER JOIN " + BookEntry.TABLE_NAME + " AS k ON k." + BookEntry.COLUMN_BOOK_B
                + " = t." + ChapterEntry.COLUMN_CHAPTER_B + " WHERE t."
                + ChapterEntry.COLUMN_CHAPTER_B + " = " + bookId
                + " ORDER BY " + ChapterEntry.COLUMN_CHAPTER_C + " ASC";

        Cursor c = db.rawQuery(SQL_SELECT_CHAPTERS, null);
        if (c.moveToFirst()) {
            do {
                Chapter cpt = new Chapter();
                cpt.setBookNumber(c.getInt(c.getColumnIndex(ChapterEntry.COLUMN_CHAPTER_B)));
                cpt.setBookName(c.getString(c.getColumnIndex(BookEntry.COLUMN_BOOK_N)));
                cpt.setNumber(c.getInt(c.getColumnIndex(ChapterEntry.COLUMN_CHAPTER_C)));
                cpt.setTotalChapters(c.getCount());
                chapters.add(cpt);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return chapters;
    }

    public List<Verse> getVerses(int bookNo, int chapterNo) {
        SQLiteDatabase db = getReadableDatabase();
        List<Verse> verses = new ArrayList<>();

        final String DB2_PATH = mContext.getDatabasePath(DbHelper.DATABASE_NAME).getPath();
        db.execSQL("ATTACH DATABASE '" + DB2_PATH + "' AS db2");

        final String SQL_SELECT_VERSES = "SELECT t." + VerseEntry.COLUMN_VERSE_B
                + ", b." + BookmarkEntry.COLUMN_VERSE_ID + ", " + VerseEntry.COLUMN_VERSE_ID
                + ", k." + BookEntry.COLUMN_BOOK_N + ", h." + VerseHighlightEntry.COLUMN_COLOR
                + ", t." + VerseEntry.COLUMN_VERSE_C + ", t." + VerseEntry.COLUMN_VERSE_V + ", t."
                + VerseEntry.COLUMN_VERSE_T + " FROM " + VerseEntry.TABLE_NAME + " AS t"
                + " INNER JOIN " + BookEntry.TABLE_NAME + " AS k ON k." + BookEntry.COLUMN_BOOK_B
                + " = t." + VerseEntry.COLUMN_VERSE_B
                + " LEFT JOIN db2." + VerseHighlightEntry.TABLE_NAME + " AS h ON h." + VerseHighlightEntry.COLUMN_VERSE_ID
                + " = t." + VerseEntry.COLUMN_VERSE_ID
                + " LEFT JOIN db2." + BookmarkEntry.TABLE_NAME + " AS b ON b." + BookmarkEntry.COLUMN_VERSE_ID
                + " = t." + VerseEntry.COLUMN_VERSE_ID
                + " WHERE t." + VerseEntry.COLUMN_VERSE_B + " = " + bookNo + " AND t."
                + VerseEntry.COLUMN_VERSE_C + " = " + chapterNo;

        Cursor c = db.rawQuery(SQL_SELECT_VERSES, null);

        if (c.moveToFirst()) {
            do {
                Verse verse = new Verse();
                boolean status = c.getInt(c.getColumnIndex(BookmarkEntry.COLUMN_VERSE_ID)) > 0;
                verse.setId(c.getInt(c.getColumnIndex(VerseEntry.COLUMN_VERSE_ID)));
                verse.setBookNumber(c.getInt(c.getColumnIndex(VerseEntry.COLUMN_VERSE_B)));
                verse.setBookName(c.getString(c.getColumnIndex(BookEntry.COLUMN_BOOK_N)));
                verse.setChapterNumber(c.getInt(c.getColumnIndex(VerseEntry.COLUMN_VERSE_C)));
                verse.setVerseNumber(c.getInt(c.getColumnIndex(VerseEntry.COLUMN_VERSE_V)));
                verse.setText(c.getString(c.getColumnIndex(VerseEntry.COLUMN_VERSE_T)));
                verse.setHighlightColor(c.getString(c.getColumnIndex(VerseHighlightEntry.COLUMN_COLOR)));
                verse.setBookmarked(status);
                verses.add(verse);
            } while (c.moveToNext());
        }

        c.close();
        db.close();
        return verses;
    }

    public List<Verse> searchVerses(String query) {
        SQLiteDatabase db = getReadableDatabase();
        List<Verse> verses = new ArrayList<>();

        final String SQL_SELECT_VERSES = "SELECT t." + VerseEntry.COLUMN_VERSE_B + ", "
                + BookEntry.COLUMN_BOOK_N
                + ", t." + VerseEntry.COLUMN_VERSE_C + ", t." + VerseEntry.COLUMN_VERSE_V + ", t."
                + VerseEntry.COLUMN_VERSE_T + " FROM " + VerseEntry.TABLE_NAME + " AS t"
                + " INNER JOIN " + BookEntry.TABLE_NAME + " AS k ON k." + BookEntry.COLUMN_BOOK_B
                + " = t." + VerseEntry.COLUMN_VERSE_B + " WHERE t."
                + VerseEntry.COLUMN_VERSE_T + " LIKE '%" + query + "%'";

        Cursor c = db.rawQuery(SQL_SELECT_VERSES, null);
        if (c.moveToFirst()) {
            do {
                Verse verse = new Verse();
                verse.setBookNumber(c.getInt(c.getColumnIndex(VerseEntry.COLUMN_VERSE_B)));
                verse.setBookName(c.getString(c.getColumnIndex(BookEntry.COLUMN_BOOK_N)));
                verse.setChapterNumber(c.getInt(c.getColumnIndex(VerseEntry.COLUMN_VERSE_C)));
                verse.setVerseNumber(c.getInt(c.getColumnIndex(VerseEntry.COLUMN_VERSE_V)));
                verse.setText(c.getString(c.getColumnIndex(VerseEntry.COLUMN_VERSE_T)));
                verses.add(verse);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return verses;
    }

    public SpecialVerse getSpecialVerse(int verseId) {
        SQLiteDatabase db = getReadableDatabase();

        Calendar today = Calendar.getInstance(Locale.getDefault());
        int day = today.get(Calendar.DAY_OF_YEAR);

        final String SQL_TODAY_VERSE = "SELECT * FROM " + SpecialVerseEntry.TABLE_NAME
                + " WHERE " + SpecialVerseEntry.COLUMN_ID + " = " + verseId;

        Cursor c = db.rawQuery(SQL_TODAY_VERSE, null);
        SpecialVerse specialVerse = new SpecialVerse();

        if (c.moveToFirst()) {
            specialVerse.setId(c.getInt(c.getColumnIndex(SpecialVerseEntry.COLUMN_ID)));
            specialVerse.setVerseName(c.getString(c.getColumnIndex(SpecialVerseEntry.COLUMN_VERSE_NO)));
            specialVerse.setVerseText(c.getString(c.getColumnIndex(SpecialVerseEntry.COLUMN_VERSE_TEXT)));
        }
        c.close();
        db.close();
        return specialVerse;
    }

    public int getVersion(){
        return DATABASE_VERSION;
    }

    public String getName(){
        return DATABASE_NAME;
    }
}