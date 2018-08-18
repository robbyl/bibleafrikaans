package tz.co.wadau.biblekingjamesversion.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import tz.co.wadau.biblekingjamesversion.fragment.VersesSearchResultsPageFragment;
import tz.co.wadau.biblekingjamesversion.model.Verse;

public class VersesSearchResultsPagerAdapter extends FragmentStatePagerAdapter {
    private final String TAG = VersesSearchResultsPagerAdapter.class.getSimpleName();
    private List<Verse> searchResults;
    private String searchQuery;
    private int tabCount;


    public VersesSearchResultsPagerAdapter(FragmentManager fragmentManager, List<Verse> searchResults, String searchQuery, int tabCount) {
        super(fragmentManager);
        this.searchResults = searchResults;
        this.searchQuery = searchQuery;
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        return VersesSearchResultsPageFragment.newInstance(searchResults, searchQuery, position);
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
