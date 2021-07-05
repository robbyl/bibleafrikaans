package tz.co.wadau.bibleinafrikaans.adapter;


import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tz.co.wadau.bibleinafrikaans.R;
import tz.co.wadau.bibleinafrikaans.utils.Utils;
import tz.co.wadau.bibleinafrikaans.model.Note;

public class NotesAdapter extends SelectableAdapter<NotesAdapter.NotesViewHolder> {

    private List<Note> notes;

    public class NotesViewHolder extends RecyclerView.ViewHolder {
        public TextView noteBody;
        public TextView noteDate;
        public CardView noteCardView;
        public View selectedOverlay;
        public RelativeLayout noteListBg;

        public NotesViewHolder(View itemView) {
            super(itemView);
            noteBody = (TextView) itemView.findViewById(R.id.note_body);
            noteDate = (TextView) itemView.findViewById(R.id.note_date);
            noteCardView = (CardView) itemView.findViewById(R.id.note_card_view);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
            noteListBg = (RelativeLayout) itemView.findViewById(R.id.notes_list_bg);
        }
    }

    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void setFilter(List<Note> notesFilter) {
        this.notes = notesFilter;
        notifyDataSetChanged();
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_note, null);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.noteBody.setText(note.getText());
        holder.noteDate.setText(Utils.formatDateLongFormat(note.getCreatedAt()));
        holder.noteCardView.setCardBackgroundColor(Color.parseColor(note.getColor()));
        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
        if (isSelected(position)) {
            holder.noteListBg.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorSelectedNotes));
        } else {
            holder.noteListBg.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private void removeItem(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            notes.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }
}
