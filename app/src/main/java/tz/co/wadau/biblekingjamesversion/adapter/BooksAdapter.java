package tz.co.wadau.biblekingjamesversion.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tz.co.wadau.biblekingjamesversion.R;
import tz.co.wadau.biblekingjamesversion.model.Book;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder>{
    private List<Book> books;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        MyViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.book_title);
        }
    }

    public BooksAdapter(List<Book> books) {
        this.books = books;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_book, null);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Book book = books.get(position);
        holder.title.setText(book.getName());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void swapData(List<Book> books){
        this.books = books;
        notifyDataSetChanged();
    }
}
