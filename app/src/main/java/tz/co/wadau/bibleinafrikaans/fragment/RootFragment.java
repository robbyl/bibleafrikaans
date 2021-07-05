package tz.co.wadau.bibleinafrikaans.fragment;

import androidx.fragment.app.Fragment;

import tz.co.wadau.bibleinafrikaans.BackPressImpl;
import tz.co.wadau.bibleinafrikaans.OnBackPressListener;


public class RootFragment extends Fragment implements OnBackPressListener {

    @Override
    public boolean onBackPressed() {
        return new BackPressImpl(this).onBackPressed();
    }
}
