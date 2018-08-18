package tz.co.wadau.bibleafrikaans.model;

public class Verse {

    private int id;
    private int bookNumber;
    private String bookName;
    private int chapterNumber;
    private int verseNumber;
    private String text;
    private String highlightColor;
    private boolean isBookmarked;

    public Verse() {
    }

    public int getId() {
        return id;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public String getBookName() {
        return bookName;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public int getVerseNumber() {
        return verseNumber;
    }

    public String getText() {
        return text;
    }

    public String getHighlightColor() {
        return highlightColor;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setHighlightColor(String highlightColor) {
        this.highlightColor = highlightColor;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public void setVerseNumber(int verseNumber) {
        this.verseNumber = verseNumber;
    }

    public void setText(String text) {
        this.text = text;
    }
}
