package com.mzz.zmusicplayer.receiver;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.mzz.zmusicplayer.play.Player;

/**
 * App state listener.
 *
 * @author zuozhu.meng
 * @date 2020 /2/23
 */
public class AppStateListener {

    /**
     * 获取电话状态监听者
     *
     * @return the music phone state listener
     */
    public static PhoneStateListener getMusicPhoneStateListener() {
        return new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                //响铃或接听电话，则暂停播放
                if (state == TelephonyManager.CALL_STATE_RINGING || state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    Player.getInstance().pause();
                }
            }
        };
    }
}
