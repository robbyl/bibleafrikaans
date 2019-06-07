package tz.co.wadau.bibleinafrikaans.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.model.Verse;

import static android.content.ContentValues.TAG;

public class VersesSearchResultsAdapter extends RecyclerView.Adapter<VersesSearchResultsAdapter.VersesSearchResultsViewHolder> {

    private List<Verse> verseResults;
    private String searchQuery;
    private Context context;
    private OnVerseResultClickListener verseResultClickListener;

    public VersesSearchResultsAdapter(List<Verse> verses, Context context, String searchQuery) {
        verseResults = verses;
        this.searchQuery = searchQuery;
        this.context = context;

        if (context instanceof OnVerseResultClickListener) {
            verseResultClickListener = (OnVerseResultClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnVerseResultClickListener");
        }
    }

    public class VersesSearchResultsViewHolder extends RecyclerView.ViewHolder {

        private TextView verseResultHeader, verseResultText;
        private CardView verseResultCard;

        public VersesSearchResultsViewHolder(View itemView) {
            super(itemView);
            verseResultHeader = (TextView) itemView.findViewById(R.id.verse_search_results_header);
            verseResultText = (TextView) itemView.findViewById(R.id.verse_search_results_text);
            verseResultCard = (CardView) itemView.findViewById(R.id.verse_result_card);
        }
    }

    @Override
    public VersesSearchResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_verse_search_result, null);
        return new VersesSearchResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VersesSearchResultsViewHolder holder, final int position) {

        Verse verse = verseResults.get(position);
        String bookName = verse.getBookName();
        int chapterNo = verse.getChapterNumber();
        int verseNo = verse.getVerseNumber();

        int startPos = verse.getText().toLowerCase(Locale.US).indexOf(searchQuery.toLowerCase(Locale.US));
        int endPos = startPos + searchQuery.length();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(verse.getText());
        int searchQueryHighlightColor = ContextCompat.getColor(context, R.color.colorVerseSearchQuery);
        stringBuilder.setSpan(new BackgroundColorSpan(ColorUtils.setAlphaComponent(searchQueryHighlightColor, 130)),
                startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.verseResultHeader.setText(bookName + " " + chapterNo + ":" + verseNo);
        holder.verseResultText.setText(stringBuilder);

        holder.verseResultCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verseResultClicked(position);
                Log.d(TAG, "Verse result " + position + " clicked");
            }
        });


    }

    @Override
    public int getItemCount() {
        return verseResults.size();
    }

    public interface OnVerseResultClickListener {
        void onVerseResultClicked(Verse verseResult);
    }

    private void verseResultClicked(int position) {
        if (verseResultClickListener != null) {
            verseResultClickListener.onVerseResultClicked(verseResults.get(position));
        }
    }
}
