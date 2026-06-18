package com.mzz.zmusicplayer.manage;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;

import androidx.core.content.ContextCompat;

import com.mzz.zmusicplayer.MusicApplication;
import com.mzz.zmusicplayer.play.Player;

/**
 * 监听器管理
 *
 * @author zuozhu.meng
 * @date 2020 /2/23
 */
public class ListenerManager {

    private static final String PERMISSION_READ_PHONE_STATE = android.Manifest.permission.READ_PHONE_STATE;

    public static Object getSystemService(String name) {
        return MusicApplication.getContext().getSystemService(name);
    }

    public static OnAudioFocusChangeListener getOnAudioFocusChangeListener() {
        return focusChange -> {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                pause();
            }
        };
    }

    /**
     * @deprecated 请使用 {@link #registerCallStateListener(Context, TelephonyManager, CallStateRegistration)}
     */
    @Deprecated
    public static PhoneStateListener getMusicPhoneStateListener() {
        return new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                handleCallStateChanged(state);
            }
        };
    }

    public static boolean registerCallStateListener(Context context, TelephonyManager telephonyManager,
                                                    CallStateRegistration registration) {
        if (context == null || telephonyManager == null || registration == null) {
            return false;
        }
        if (!hasPhoneStatePermission(context)) {
            return false;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MusicCallStateCallback callback = new MusicCallStateCallback();
                registration.telephonyCallback = callback;
                telephonyManager.registerTelephonyCallback(context.getMainExecutor(), callback);
            } else {
                PhoneStateListener listener = new PhoneStateListener() {
                    @Override
                    public void onCallStateChanged(int state, String phoneNumber) {
                        super.onCallStateChanged(state, phoneNumber);
                        handleCallStateChanged(state);
                    }
                };
                registration.phoneStateListener = listener;
                telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
            }
            registration.registered = true;
            return true;
        } catch (SecurityException ignored) {
            registration.registered = false;
            return false;
        }
    }

    public static void unregisterCallStateListener(TelephonyManager telephonyManager,
                                                   CallStateRegistration registration) {
        if (telephonyManager == null || registration == null || !registration.registered) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && registration.telephonyCallback != null) {
                telephonyManager.unregisterTelephonyCallback(registration.telephonyCallback);
            } else if (registration.phoneStateListener != null) {
                telephonyManager.listen(registration.phoneStateListener, PhoneStateListener.LISTEN_NONE);
            }
        } catch (SecurityException ignored) {
            // ignore
        } finally {
            registration.phoneStateListener = null;
            registration.telephonyCallback = null;
            registration.registered = false;
        }
    }

    public static boolean hasPhoneStatePermission(Context context) {
        return context != null
                && ContextCompat.checkSelfPermission(context, PERMISSION_READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static void handleCallStateChanged(int state) {
        if (state == TelephonyManager.CALL_STATE_RINGING
                || state == TelephonyManager.CALL_STATE_OFFHOOK) {
            pause();
        }
    }

    private static void pause() {
        Player.getInstance().pause();
    }

    private static class MusicCallStateCallback extends TelephonyCallback
            implements TelephonyCallback.CallStateListener {
        @Override
        public void onCallStateChanged(int state) {
            handleCallStateChanged(state);
        }
    }

    public static class CallStateRegistration {
        private PhoneStateListener phoneStateListener;
        private TelephonyCallback telephonyCallback;
        private boolean registered;

        public boolean isRegistered() {
            return registered;
        }
    }

}
