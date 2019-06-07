package tz.co.wadau.bibleinafrikaans.fragment;

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

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.adapter.BooksAdapter;
import tz.co.wadau.bibleinafrikaans.customviews.ItemClickSupport;
import tz.co.wadau.bibleinafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleinafrikaans.model.Book;

public class OldTestamentFragment extends RootFragment {

    private final String TAG = "OldTestamentFragment";
    private OnBookSelectedListener mListener;
    public static BooksAdapter oldStatementAdapter;
    private RecyclerView booksListRecyclerView;
    public static List<Book> oldStatementBooks;
    public static List<Book> tempOldStmtBooks;

    public OldTestamentFragment() {
    }

    public static OldTestamentFragment newInstance() {
        return new OldTestamentFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_old_testament, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        booksListRecyclerView = (RecyclerView) view.findViewById(R.id.books_list_recycler_view);
        loadOldStatementBooks();

        ItemClickSupport.addTo(booksListRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                listSelectedOldTestamentBookChapters(position);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBookSelectedListener) {
            mListener = (OnBookSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBookSelectedListener");
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


    private void loadOldStatementBooks() {
        DbFileHelper db = new DbFileHelper(getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        oldStatementBooks = db.getOldStatementBooks();
        tempOldStmtBooks = oldStatementBooks;
        oldStatementAdapter = new BooksAdapter(oldStatementBooks);
        booksListRecyclerView.setLayoutManager(layoutManager);
        booksListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        booksListRecyclerView.setAdapter(oldStatementAdapter);
    }

    public void listSelectedOldTestamentBookChapters(int position){
        Book mBook = oldStatementBooks.get(position);

        ChaptersListFragment chaptersListFragment =  ChaptersListFragment.newInstance(mBook.getNumber(), mBook.getName());
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.old_testament_books_containter, chaptersListFragment).commit();
    }

    public interface OnBookSelectedListener {
        void onBookSelected(Book book);
    }
}
