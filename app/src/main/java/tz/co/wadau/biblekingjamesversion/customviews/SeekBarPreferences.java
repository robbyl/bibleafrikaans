package tz.co.wadau.biblekingjamesversion.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import tz.co.wadau.biblekingjamesversion.R;

public class SeekBarPreferences extends Preference
        implements OnSeekBarChangeListener {

    private int mProgress;
    private int mMax;
    private String mTitle;
    private String mSummary;
    private boolean mTrackingTouch;

    public SeekBarPreferences(
            Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference, defStyle, 0);
        setMax(a.getInt(R.styleable.SeekBarPreference_max, mMax));
        setTitle(a.getString(R.styleable.SeekBarPreference_title));
        setSummary(a.getString(R.styleable.SeekBarPreference_summary));
        a.recycle();
        setLayoutResource(R.layout.seek_bar_preference);
    }

    public SeekBarPreferences(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPreferences(Context context) {
        this(context, null);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        SeekBar seekBar = (SeekBar) holder.findViewById(R.id.seekbar);
        TextView textView = (TextView) holder.findViewById(R.id.title);
        TextView summary = (TextView) holder.findViewById(R.id.summary);
        textView.setText(mTitle);
        showSummary(summary, mSummary);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(mMax);
        seekBar.setProgress(mProgress);
        seekBar.setEnabled(isEnabled());
    }

    @Override
    public CharSequence getSummary() {
        return null;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setProgress(restoreValue ? getPersistedInt(mProgress)
                : (Integer) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

//    @Override
//    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        if (event.getAction() != KeyEvent.ACTION_UP) {
//            if (keyCode == KeyEvent.KEYCODE_PLUS
//                    || keyCode == KeyEvent.KEYCODE_EQUALS) {
//                setProgress(getProgress() + 1);
//                return true;
//            }
//            if (keyCode == KeyEvent.KEYCODE_MINUS) {
//                setProgress(getProgress() - 1);
//                return true;
//            }
//        }
//        return false;
//    }


    private void setTitle(String title) {
        this.mTitle = title;
    }

    private void showSummary(TextView textView, String summary) {
        if (!TextUtils.isEmpty(mSummary)) {
            textView.setText(mSummary);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void setSummary(String summary) {
        this.mSummary = summary;
    }

    private void setMax(int max) {
        if (max != mMax) {
            mMax = max;
            notifyChanged();
        }
    }

    private void setProgress(int progress) {
        setProgress(progress, true);
    }

    private void setProgress(int progress, boolean notifyChanged) {
        if (progress > mMax) {
            progress = mMax;
        }
        if (progress < 0) {
            progress = 0;
        }
        if (progress != mProgress) {
            mProgress = progress;
            persistInt(progress);
            if (notifyChanged) {
                notifyChanged();
            }
        }
    }

    public int getProgress() {
        return mProgress;
    }

    /**
     * Persist the seekBar's progress value if callChangeListener
     * returns true, otherwise set the seekBar's progress to the stored value
     */
    private void syncProgress(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (progress != mProgress) {
            if (callChangeListener(progress)) {
                setProgress(progress, false);
            } else {
                seekBar.setProgress(mProgress);
            }
        }
    }

    @Override
    public void onProgressChanged(
            SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && !mTrackingTouch) {
            syncProgress(seekBar);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mTrackingTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mTrackingTouch = false;
        if (seekBar.getProgress() != mProgress) {
            syncProgress(seekBar);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        /*
         * Suppose a client uses this preference type without persisting. We
         * must save the instance state so it is able to, for example, survive
         * orientation changes.
         */

        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        // Save the instance state
        final SavedState myState = new SavedState(superState);
        myState.progress = mProgress;
        myState.max = mMax;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        // Restore the instance state
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        mProgress = myState.progress;
        mMax = myState.max;
        notifyChanged();
    }

    /**
     * SavedState, a subclass of {@link BaseSavedState}, will store the state
     * of MyPreference, a subclass of Preference.
     * <p>
     * It is important to always call through to super methods.
     */
    private static class SavedState extends BaseSavedState {
        int progress;
        int max;

        public SavedState(Parcel source) {
            super(source);

            // Restore the click counter
            progress = source.readInt();
            max = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            // Save the click counter
            dest.writeInt(progress);
            dest.writeInt(max);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
