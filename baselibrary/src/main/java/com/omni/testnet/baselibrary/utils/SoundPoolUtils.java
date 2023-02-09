package com.omni.testnet.baselibrary.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import java.util.List;

public class SoundPoolUtils {

    private static final String TAG = SoundPoolUtils.class.getSimpleName();
    private Context mContext;
    private SoundPool mSoundPool;
    private boolean mLoadComplete = false;
    private SparseIntArray mSoundIdArray = new SparseIntArray();
    private float mVolume = 0.4f;
    private AudioManager mAudioManager;
    private float mMaxVolume;

    public SoundPoolUtils(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        // 获取当前设备的最大音量
        this.mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        this.mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        this.mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mLoadComplete = true;
            }
        });
    }

    /**
     * 加载声音资源
     */
    public void loadSoundRes(List<Integer> list) {
        if (list != null && list.size() > 0) {
            mSoundIdArray.clear();
            for (Integer id : list) {
                int soundId = mSoundPool.load(mContext, id, 1);
                mSoundIdArray.put(list.indexOf(id), soundId);
            }
        }
    }

    /**
     * 播放
     */
    public void play(int index) {
        int soundId = mSoundIdArray.get(index);
        if (soundId != 0) {
            mSoundPool.play(soundId, mVolume, mVolume, 1, 0, 1.0f);
        }
    }

    /**
     * 音量键加
     */
    public void onVolumeUp() {
        // 获取当前音量
        float currentVolume = getCurrentVolume();
        // 根据当前设备的最大音量计算出0-1之间的音量值
        this.mVolume = 1 / mMaxVolume * currentVolume;
    }

    /**
     * 音量键减
     */
    public void onVolumeDown() {
        // 获取当前音量
        float currentVolume = getCurrentVolume();
        // 根据当前设备的最大音量计算出0-1之间的音量值
        this.mVolume = 1 / mMaxVolume * currentVolume;
    }

    /**
     * 获取当前音量
     */
    private int getCurrentVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 释放资源
     */
    public void release() {
        mSoundPool.release();
    }

}
