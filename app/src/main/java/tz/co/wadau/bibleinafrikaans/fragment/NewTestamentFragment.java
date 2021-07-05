package tz.co.wadau.bibleinafrikaans.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.adapter.BooksAdapter;
import tz.co.wadau.bibleinafrikaans.customviews.ItemClickSupport;
import tz.co.wadau.bibleinafrikaans.data.DbFileHelper;
import tz.co.wadau.bibleinafrikaans.model.Book;

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
