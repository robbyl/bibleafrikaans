package tz.co.wadau.bibleafrikaans.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.List;

import tz.co.wadau.bibleafrikaans.R;
import tz.co.wadau.bibleafrikaans.adapter.BooksAdapter;
import tz.co.wadau.bibleafrikaans.customviews.ItemClickSupport;
import tz.co.wadau.bibleafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleafrikaans.model.Book;

public class NewTestamentFragment extends RootFragment {

    public static final String TAG = "NewTestamentFragment";
    private OldTestamentFragment.OnBookSelectedListener mListener;
    private RecyclerView recyclerView;
    public static BooksAdapter newStatementAdapter;
    public static List<Book> newStatementBooks;
    public static List<Book> tempNewStmtBooks;

    public NewTestamentFragment() {
        // Required empty public constructor
    }

    public static NewTestamentFragment newInstance() {
        return new NewTestamentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_testament, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.books_list_recycler_view);
        loadNewStatementBooks();

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                listSelectedNewTestamentBookChapters(position);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OldTestamentFragment.OnBookSelectedListener) {
            mListener = (OldTestamentFragment.OnBookSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBookSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadNewStatementBooks() {
        DbFileHelper db = new DbFileHelper(getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        newStatementBooks = db.getNewStatementBooks();
        tempNewStmtBooks = newStatementBooks;
        newStatementAdapter = new BooksAdapter(newStatementBooks);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(newStatementAdapter);
    }

    public void listSelectedNewTestamentBookChapters(int position) {
        Book mBook = newStatementBooks.get(position);

        ChaptersListFragment chaptersListFragment = ChaptersListFragment.newInstance(mBook.getNumber(), mBook.getName());
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.new_testament_books_containter, chaptersListFragment).commit();
    }
}
