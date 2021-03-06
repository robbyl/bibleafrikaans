package tz.co.wadau.bibleinafrikaans.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import tz.co.wadau.bibleinafrikaans.fragment.VersesSearchResultsPageFragment;
import tz.co.wadau.bibleinafrikaans.model.Verse;

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
