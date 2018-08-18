package tz.co.wadau.bibleafrikaans.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.SeekBar;

import tz.co.wadau.bibleafrikaans.R;

import static tz.co.wadau.bibleafrikaans.ChapterActivity.MIN_SCROLL_SPEED;
import static tz.co.wadau.bibleafrikaans.ChapterActivity.scrollSpeed;

public class SetScrollSpeedFragment extends DialogFragment {
    SetScrollSpeedFragment.SetScrollSpeedListener mListener;
    public final String TAG = "SetScrollSpeedFragment";


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final SeekBar seekBar = new AppCompatSeekBar(getContext());
        seekBar.setProgress(scrollSpeed - MIN_SCROLL_SPEED);
        seekBar.setMax(5);
        seekBar.setPadding(64, 48, 64, 20);

        builder.setTitle(R.string.set_auto_scroll_speed).setView(seekBar)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onSetScrollSpeedDialogPositiveClick(seekBar.getProgress());
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SetScrollSpeedFragment.SetScrollSpeedListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement SetScrollSpeedListener");
        }
    }

    public interface SetScrollSpeedListener {
        void onSetScrollSpeedDialogPositiveClick(int seekBarProgress);
    }
}
