package tz.co.wadau.biblekingjamesversion.model;

public class Bookmark {
    private int id;
    private int verseId;
    private int bookNumber;
    private String bookName;
    private int chapterNumber;
    private int verseNumber;
    private String createdDate;

    public Bookmark(){}

    public Bookmark(int id, int verseId, int bookNumber, String bookName, int chapterNumber, int verseNumber, String createdDate) {
        this.id = id;
        this.verseId = verseId;
        this.bookNumber = bookNumber;
        this.bookName = bookName;
        this.chapterNumber = chapterNumber;
        this.verseNumber = verseNumber;
        this.createdDate = createdDate;
    }

    public Bookmark(int bookNumber, String bookName, int chapterNumber, int verseNumber, String createdDate) {
        this.bookNumber = bookNumber;
        this.bookName = bookName;
        this.chapterNumber = chapterNumber;
        this.verseNumber = verseNumber;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public int getVerseId() {
        return verseId;
    }

    public void setVerseId(int verseId) {
        this.verseId = verseId;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public int getVerseNumber() {
        return verseNumber;
    }

    public void setVerseNumber(int verseNumber) {
        this.verseNumber = verseNumber;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
