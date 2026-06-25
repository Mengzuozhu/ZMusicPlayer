package com.mzz.zmusicplayer.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * ViewPager2适配器
 * @author : Mzz
 * date : 2024/12/19
 * description : 用于ViewPager2的Fragment适配器
 */
public class MusicPager2Adapter extends FragmentStateAdapter {

    private final List<MusicPage> fragments;

    public MusicPager2Adapter(@NonNull FragmentActivity fragmentActivity, List<MusicPage> fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position).getFragment();
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    public String getPageTitle(int position) {
        return fragments.get(position).getPageTitle();
    }
}
