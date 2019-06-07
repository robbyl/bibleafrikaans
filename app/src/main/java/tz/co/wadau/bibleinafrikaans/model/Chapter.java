package tz.co.wadau.bibleinafrikaans.model;

public class Chapter {
    private int bookNumber;
    private String bookName;
    private int number;
    private int totalChapters;

    public Chapter(){
    }

    public int getNumber() {
        return number;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getTotalChapters() {
        return totalChapters;
    }

    public void setTotalChapters(int totalChapters) {
        this.totalChapters = totalChapters;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
