package com.mzz.zmusicplayer.manage;

import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.play.Player;

/**
 * 监听器管理
 *
 * @author zuozhu.meng
 * @date 2020 /2/23
 */
public class ListenerManager {

    /**
     * Gets system service.
     *
     * @param name the name
     * @return the system service
     */
    public static Object getSystemService(String name) {
        return MusicApplication.getContext().getSystemService(name);
    }

    /**
     * 音频焦点监听者
     *
     * @return the on audio focus change listener
     */
    public static OnAudioFocusChangeListener getOnAudioFocusChangeListener() {
        return focusChange -> {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                pause();
            }
        };
    }

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
                    pause();
                }
            }
        };
    }

    private static void pause() {
        Player.getInstance().pause();
    }
}
