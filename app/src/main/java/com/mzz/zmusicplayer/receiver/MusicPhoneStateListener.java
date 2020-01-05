package com.mzz.zmusicplayer.receiver;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.mzz.zmusicplayer.play.Player;

/**
 * 接听电话状态监听者
 * author : Mzz
 * date : 2019 2019/6/20 10:06
 * description :
 */
public class MusicPhoneStateListener extends PhoneStateListener {

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        //响铃或接听电话，则暂停播放
        if (state == TelephonyManager.CALL_STATE_RINGING || state == TelephonyManager.CALL_STATE_OFFHOOK) {
            Player.getInstance().pause();
        }
    }
}
