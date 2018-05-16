package com.perfect.library.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by Canghaixiao.
 * Time : 2017/1/3 16:14.
 * Function :
 */

public class BasePerfectFragment extends Fragment {
    public Object mPerfectFragmentTag;

    public Object getFragmentTag() {
        return mPerfectFragmentTag;
    }

    public void setFragmentTag(Object perfectFragmentTag) {
        this.mPerfectFragmentTag = perfectFragmentTag;
    }
}
