package com.mzz.zmusicplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * author : Mzz
 * date : 2019 2019/6/4 19:19
 * description :
 */
public class MusicPagerAdapter extends FragmentPagerAdapter {

    private final List<MusicPage> fragments;

    public MusicPagerAdapter(FragmentManager fm, List<MusicPage> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position).fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getPageTitle();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}

