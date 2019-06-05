package com.mzz.zmusicplayer.adapter;

import android.support.v4.app.Fragment;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author : Mzz
 * date : 2019 2019/6/4 19:30
 * description :
 */
@AllArgsConstructor
public class PageFragment {
    @Getter
    Fragment fragment;
    @Getter
    String pageTitle;
}
