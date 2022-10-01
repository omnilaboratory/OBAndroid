package com.omni.wallet.baselibrary.view.datePicker;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class Sound {

    private SoundPool mSoundPool;
    private AudioManager mAudioManager;
    private float mCurrVolume;
    private Context mContext;
    private int mSoundId;

    public Sound(Context context) {
        mContext = context;
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 0);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void playSoundEffect() {
        mCurrVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        if (mSoundId > 0) {
            mSoundPool.play(mSoundId, mCurrVolume, mCurrVolume, 0, 0, 1);
        } else {
            mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK, mCurrVolume);
        }
    }

    public void setCustomSound(int resId) {
        mSoundId = mSoundPool.load(mContext, resId, 1);
    }

}
