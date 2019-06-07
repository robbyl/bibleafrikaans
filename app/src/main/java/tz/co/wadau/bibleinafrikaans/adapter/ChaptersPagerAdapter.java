package tz.co.wadau.bibleinafrikaans.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import tz.co.wadau.bibleinafrikaans.fragment.ChapterPageFragment;

public class ChaptersPagerAdapter extends FragmentStatePagerAdapter {
    private int numberOfChapters;
    private int bookNo;
    private int chapterNo;
    private int verseNo;

    public ChaptersPagerAdapter(FragmentManager fragmentManager, int bookNo, int chapterNo, int verseNo, int numberOfChapters) {
        super(fragmentManager);
        this.numberOfChapters = numberOfChapters;
        this.bookNo = bookNo;
        this.chapterNo = chapterNo;
        this.verseNo = verseNo;
    }

    @Override
    public Fragment getItem(int position) {
        return ChapterPageFragment.newInstance(bookNo, position + 1, verseNo);
    }



    @Override
    public int getCount() {
        return numberOfChapters;
    }
}
