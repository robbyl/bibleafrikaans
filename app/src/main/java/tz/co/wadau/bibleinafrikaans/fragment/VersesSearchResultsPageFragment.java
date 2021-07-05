package tz.co.wadau.bibleinafrikaans.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.adapter.VersesSearchResultsAdapter;
import tz.co.wadau.bibleinafrikaans.model.Verse;

public class VersesSearchResultsPageFragment extends Fragment {

    private final String TAG = VersesSearchResultsPageFragment.class.getSimpleName();
    private static List<Verse> mSearchResults;
    private RecyclerView verseResultsRecyclerView;
    private VersesSearchResultsAdapter versesSearchResultsAdapter;
    static String mSearchQuery;
    private static int mCurrTabPosition;
    private List<Verse> categorizedResults;
    private TextView searchResultsTotal;

    private static final String ARG_SEARCH_QUERY = "searchQuery";
    private static final String ARG_CURR_TAB_POSITION = "currTabPosition";

    public VersesSearchResultsPageFragment() {
    }

    public static VersesSearchResultsPageFragment newInstance(List<Verse> searchResults, String searchQuery, int currTabPosition) {
        VersesSearchResultsPageFragment fragment = new VersesSearchResultsPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SEARCH_QUERY, searchQuery);
        args.putInt(ARG_CURR_TAB_POSITION, currTabPosition);
        mSearchResults = searchResults;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSearchQuery = getArguments().getString(ARG_SEARCH_QUERY);
            mCurrTabPosition = getArguments().getInt(ARG_CURR_TAB_POSITION);
            categorizedResults = getSearchResults(mCurrTabPosition);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verseResultsRecyclerView = (RecyclerView) view.findViewById(R.id.verse_results_recyclerview);
        searchResultsTotal = (TextView) view.findViewById(R.id.search_result_total);
        TextView searchResultsTextview = (TextView) view.findViewById(R.id.search_results_textview);
        searchResultsTextview.setText(getString(R.string.search_results_for) + ": " + mSearchQuery);
        verseResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        verseResultsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        versesSearchResultsAdapter = new VersesSearchResultsAdapter(categorizedResults, getContext(), mSearchQuery);
        searchResultsTotal.setText(getString(R.string.total) + " " + String.valueOf(categorizedResults.size()));
        verseResultsRecyclerView.setAdapter(versesSearchResultsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verses_search_results_page, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private List<Verse> getSearchResults(int position) {
        List<Verse> results = new ArrayList<>();
        List<Verse> oldStatementResults = new ArrayList<>();
        List<Verse> newStatementResults = new ArrayList<>();

        for (Verse verse : mSearchResults) {
            if (verse.getBookNumber() >= 39) {
                newStatementResults.add(verse);
            } else {
                oldStatementResults.add(verse);
            }
        }

        switch (position) {
            case 0:
                results = mSearchResults;
                break;
            case 1:
                results = oldStatementResults;
                break;
            case 2:
                results = newStatementResults;
                break;
        }

        return results;
    }
}
