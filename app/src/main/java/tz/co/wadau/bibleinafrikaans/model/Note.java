package tz.co.wadau.bibleinafrikaans.model;

public class Note {
    private long id;
    private String text;
    private String title;
    private String color;
    private String createdAt;

    public Note() {
    }

    public Note(String text, String title, String createdAt) {
        this.text = text;
        this.title = title;
        this.createdAt = createdAt;
    }

    public Note(long id, String title, String text, String color, String createdAt) {
        this.id = id;
        this.text = text;
        this.title = title;
        this.color = color;
        this.createdAt = createdAt;
    }

    public Note(long id, String title, String text, String color) {
        this.id = id;
        this.text = text;
        this.title = title;
        this.color = color;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
