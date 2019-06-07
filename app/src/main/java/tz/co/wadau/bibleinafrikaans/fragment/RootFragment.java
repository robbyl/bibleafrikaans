package tz.co.wadau.bibleinafrikaans.fragment;

import android.support.v4.app.Fragment;

import tz.co.wadau.bibleinafrikaans.BackPressImpl;
import tz.co.wadau.bibleinafrikaans.OnBackPressListener;


public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }
}
