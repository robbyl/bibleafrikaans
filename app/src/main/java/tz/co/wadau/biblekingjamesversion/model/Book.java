package tz.co.wadau.biblekingjamesversion.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private int id;
    private int number;
    private String name;

    public Book() {
    }

    public Book(int number, String name) {
        this.number = number;
        this.name = name;
    }

    public Book(int id, int number, String name) {
        this.id = id;
        this.number = number;
        this.name = name;
    }

    private Book(Parcel in) {
        this.number = in.readInt();
        this.name = in.readString();
    }


    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeString(name);
    }
}
