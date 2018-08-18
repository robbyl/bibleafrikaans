package tz.co.wadau.bibleafrikaans.fragment;

import android.support.v4.app.Fragment;

import tz.co.wadau.bibleafrikaans.BackPressImpl;
import tz.co.wadau.bibleafrikaans.OnBackPressListener;


public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }
}
