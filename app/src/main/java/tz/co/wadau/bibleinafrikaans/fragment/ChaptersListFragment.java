package tz.co.wadau.bibleinafrikaans.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tz.co.wadau.bibleinafrikaans.BibleActivity;
import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.adapter.ChaptersAdapter;
import tz.co.wadau.bibleinafrikaans.customviews.ItemClickSupport;
import tz.co.wadau.bibleinafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleinafrikaans.model.Chapter;

public class ChaptersListFragment extends RootFragment {

    public static final String TAG = "RightPanelChapterFragment";
    private OnChapterSelectedListerner mListener;
    private RecyclerView chapterRecyclerView;
    private List<Chapter> chapters;
    private View selectedListItem;
    private TypedValue typedValue;
    private int mBookNo;
    private String mBookName;

    public ChaptersListFragment() {
    }

    public static ChaptersListFragment newInstance(int bookNo, String bookName) {
        ChaptersListFragment chaptersListFragment = new ChaptersListFragment();

        Bundle args = new Bundle();
        args.putInt("BOOK_NO", bookNo);
        args.putString("BOOK_NAME", bookName);
        chaptersListFragment.setArguments(args);
        return chaptersListFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBookNo = getArguments().getInt("BOOK_NO", 1);
        mBookName = getArguments().getString("BOOK_NAME");
        typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorChapterSelectedBg, typedValue, true);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView bookTitle = (TextView) view.findViewById(R.id.book_title);
        chapterRecyclerView = (RecyclerView) view.findViewById(R.id.chapters_list_recycler_view);
        bookTitle.setText(mBookName);
        loadChapters(mBookNo);

        ItemClickSupport.addTo(chapterRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                chapterSelected(position);

                //Toggle chapter list selection if layout is two pane
                if(BibleActivity.mTwoPane){
                    if(selectedListItem != null){
                        //remove previously selected list item
                        selectedListItem.setBackgroundColor(Color.TRANSPARENT);
                    }

                    v.setBackgroundResource(typedValue.resourceId);
                    selectedListItem = v;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chapters_list, container, false);
    }

    private void chapterSelected(int position) {
        if (mListener != null) {
            mListener.onChapterSelected(chapters.get(position));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnChapterSelectedListerner) {
            mListener = (OnChapterSelectedListerner) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnChapterSelectedListerner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnChapterSelectedListerner {
        void onChapterSelected(Chapter chapter);
    }

    private void loadChapters(int bookId) {
        DbFileHelper db = new DbFileHelper(getContext());
        chapters = db.getChapters(bookId);
        ChaptersAdapter mAdapter = new ChaptersAdapter(chapters);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        chapterRecyclerView.setLayoutManager(layoutManager);
        chapterRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chapterRecyclerView.setAdapter(mAdapter);
    }
}
