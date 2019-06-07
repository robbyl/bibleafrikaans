package tz.co.wadau.bibleinafrikaans.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import java.util.List;

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.adapter.VersesAdapter;
import tz.co.wadau.bibleinafrikaans.customviews.HidingScrollListener;
import tz.co.wadau.bibleinafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleinafrikaans.model.Verse;

public class ChapterPageFragment extends Fragment {

    final private String TAG = "ChapterPageFragment";
    private static final String ARG_BOOK_NO = "bookNo";
    private static final String ARG_CHAPTER_NO = "chapterNo";
    private static final String ARG_VERSE_NO = "verseNo";
    private int bookNo, chapterNo, verseNo;
    private RecyclerView verseRecyclerView;
    private LinearLayout bottomToolbarLayout;

    public ChapterPageFragment() {
        // Required empty public constructor
    }

    public static ChapterPageFragment newInstance(int bookNo, int chapterNo, int verseNo) {
        ChapterPageFragment fragment = new ChapterPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BOOK_NO, bookNo);
        args.putInt(ARG_CHAPTER_NO, chapterNo);
        args.putInt(ARG_VERSE_NO, verseNo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            bookNo = getArguments().getInt(ARG_BOOK_NO);
            chapterNo = getArguments().getInt(ARG_CHAPTER_NO);
            verseNo = getArguments().getInt(ARG_VERSE_NO);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bottomToolbarLayout = (LinearLayout) getActivity().findViewById(R.id.bottom_toolbar_container);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chapter_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verseRecyclerView = (RecyclerView) view.findViewById(R.id.verses_recycler_view);
        verseRecyclerView.setTag(chapterNo);

        loadVerses(bookNo, chapterNo, verseNo);

        verseRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }

            @Override
            public void onShow() {
                showViews();
            }
        });
    }

    private void loadVerses(int bookNo, int chapterNo, int verseNo) {
        DbFileHelper db = new DbFileHelper(getContext());
        List<Verse> verses = db.getVerses(bookNo, chapterNo);
        VersesAdapter mAdapter = new VersesAdapter(verses, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        verseRecyclerView.setLayoutManager(layoutManager);
        verseRecyclerView.setItemAnimator(new DefaultItemAnimator());
        verseRecyclerView.setAdapter(mAdapter);
        verseRecyclerView.scrollToPosition(verseNo - 1);
    }

    private void hideViews() {
        if (bottomToolbarLayout != null) {
            bottomToolbarLayout.animate().translationY(bottomToolbarLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        }
    }

    private void showViews() {
        if (bottomToolbarLayout != null) {
            bottomToolbarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }
}
