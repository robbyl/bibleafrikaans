package tz.co.wadau.biblekingjamesversion.fragment;

import android.support.v4.app.Fragment;

import tz.co.wadau.biblekingjamesversion.BackPressImpl;
import tz.co.wadau.biblekingjamesversion.OnBackPressListener;


public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }
}
