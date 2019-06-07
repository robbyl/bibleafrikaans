package tz.co.wadau.bibleinafrikaans.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.model.Chapter;

public class ChaptersAdapter extends RecyclerView.Adapter<ChaptersAdapter.MyViewHolder> {
    private List<Chapter> chapters;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.chapter_number);
        }
    }

    public ChaptersAdapter(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    @Override
    public ChaptersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_chapter, null);
        return new ChaptersAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChaptersAdapter.MyViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.title.setText("Chapter " + chapter.getNumber());
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }
}
