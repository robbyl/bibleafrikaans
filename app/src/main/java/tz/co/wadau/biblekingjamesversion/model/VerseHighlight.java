package tz.co.wadau.biblekingjamesversion.model;

public class VerseHighlight {
    private int id;
    private int verseId;
    private int bookNumber;
    private String bookName;
    private int chapterNumber;
    private int verseNumber;

    private String verseText;
    private String createdAt;
    private String color;

    public VerseHighlight(){}

    public VerseHighlight(int id, int verseId, String color) {
        this.id = id;
        this.verseId = verseId;
        this.color = color;
    }

    public VerseHighlight(int verseId, String color) {
        this.verseId = verseId;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVerseId() {
        return verseId;
    }

    public void setVerseId(int verseId) {
        this.verseId = verseId;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public int getVerseNumber() {
        return verseNumber;
    }

    public void setVerseNumber(int verseNumber) {
        this.verseNumber = verseNumber;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getVerseText() {
        return verseText;
    }

    public void setVerseText(String verseText) {
        this.verseText = verseText;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
