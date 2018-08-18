package tz.co.wadau.biblekingjamesversion.model;


public class SpecialVerse {
    private int id;
    private String categoryName;
    private String verseName;
    private String verseText;

    public SpecialVerse() {
    }

    public SpecialVerse(int id, String categoryName, String verseName, String verseText) {
        this.id = id;
        this.categoryName = categoryName;
        this.verseName = verseName;
        this.verseText = verseText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getVerseName() {
        return verseName;
    }

    public void setVerseName(String verseName) {
        this.verseName = verseName;
    }

    public String getVerseText() {
        return verseText;
    }

    public void setVerseText(String verseText) {
        this.verseText = verseText;
    }
}
