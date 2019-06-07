package tz.co.wadau.bibleinafrikaans.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.SeekBar;

import tz.co.wadau.bibleinafrikaans.R;

public class SetTextSizeFragment extends DialogFragment {
    private SetTextSizeDialogListener mListener;
    public final String TAG = SetTextSizeFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int mProgress = mPreferences.getInt(SettingsFragment.KEY_PREFS_TEXT_SIZE, 4);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final SeekBar seekBar = new AppCompatSeekBar(getContext());
        seekBar.setProgress(mProgress);
        seekBar.setMax(16);
        seekBar.setPadding(64, 48, 64, 20);

        builder.setTitle(R.string.change_text_size).setView(seekBar)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onSetTextDialogPositiveClick(seekBar.getProgress());
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
            mListener = (SetTextSizeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement SetTextSizeDialogListener");
        }
    }

    public interface SetTextSizeDialogListener {
        void onSetTextDialogPositiveClick(int seekBarProgress);
    }
}
