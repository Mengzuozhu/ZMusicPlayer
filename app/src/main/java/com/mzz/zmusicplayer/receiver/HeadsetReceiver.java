package com.mzz.zmusicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mzz.zmusicplayer.play.Player;

/**
 * 耳机监听者
 * @author : Mzz
 * date : 2019 2019/6/2 15:17
 * description :
 */
public class HeadsetReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
            String state = "state";
            if (intent.hasExtra(state) && intent.getIntExtra(state, 0) == 0) {
                //拔出耳机后，暂停音乐
                Player.getInstance().pause();
            }
        }
    }
}
