package com.mzz.zmusicplayer.song;

import com.mzz.zmusicplayer.contract.MainContract;

/**
 * author : Mzz
 * date : 2019 2019/6/3 20:14
 * description :
 */
public interface PlayListener {

    void setMainPresenter(MainContract.Presenter mainPresenter);

    void updatePlayList(PlayList playList);

    void setPlayingIndex(int playingIndex);
}
