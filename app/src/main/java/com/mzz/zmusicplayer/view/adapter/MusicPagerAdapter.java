package com.mzz.zmusicplayer.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author : Mzz
 * date : 2019 2019/6/4 19:19
 * description :
 */
public class MusicPagerAdapter extends FragmentPagerAdapter {

    private final List<MusicPage> fragments;

    public MusicPagerAdapter(FragmentManager fm, List<MusicPage> fragments) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position).fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getPageTitle();
    }

}

