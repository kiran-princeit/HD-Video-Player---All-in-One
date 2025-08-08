package com.hdvideoplayer.smartplayer.player.data.utils;

import android.content.Context;

import com.hdvideoplayer.smartplayer.player.util.SharedPreferencesUtils;
import com.hdvideoplayer.smartplayer.player.util.PreferenceInfo;

public class SettingPreferences {
    private final Context mContext;

    public SettingPreferences(Context context) {
        this.mContext = context;
    }

    public int getLanguage() {
        return SharedPreferencesUtils.getInt(this.mContext, PreferenceInfo.Setting.LANGUAGE, 0);
    }

    public void setLanguage(int i) {
        SharedPreferencesUtils.putInt(this.mContext, PreferenceInfo.Setting.LANGUAGE, i);
    }

    public int getThemes() {
        return SharedPreferencesUtils.getInt(this.mContext, PreferenceInfo.Setting.THEMES, 0);
    }

    public void setThemes(int i) {
        SharedPreferencesUtils.putInt(this.mContext, PreferenceInfo.Setting.THEMES, i);
    }

    public boolean isInAppSound() {
        return SharedPreferencesUtils.getBoolean(this.mContext, PreferenceInfo.Setting.IN_APP_SOUND, true);
    }

    public void setInAppSound(boolean z) {
        SharedPreferencesUtils.putBoolean(this.mContext, PreferenceInfo.Setting.IN_APP_SOUND, z);
    }

    public boolean isShowNotifications() {
        return SharedPreferencesUtils.getBoolean(this.mContext, PreferenceInfo.Setting.NOTIFICATIONS, true);
    }

    public void setShowNotifications(boolean z) {
        SharedPreferencesUtils.putBoolean(this.mContext, PreferenceInfo.Setting.NOTIFICATIONS, z);
    }

    public boolean isAutoPlayNextVideo() {
        return SharedPreferencesUtils.getBoolean(this.mContext, PreferenceInfo.Setting.AUTO_PLAY_NEXT_VIDEO, true);
    }

    public void setAutoPlayNextVideo(boolean z) {
        SharedPreferencesUtils.putBoolean(this.mContext, PreferenceInfo.Setting.AUTO_PLAY_NEXT_VIDEO, z);
    }

    public boolean isAutoPlayNextMusic() {
        return SharedPreferencesUtils.getBoolean(this.mContext, PreferenceInfo.Setting.AUTO_PLAY_NEXT_MUSIC, true);
    }

    public void setAutoPlayNextMusic(boolean z) {
        SharedPreferencesUtils.putBoolean(this.mContext, PreferenceInfo.Setting.AUTO_PLAY_NEXT_MUSIC, z);
    }

    public boolean isResumeVideo() {
        return SharedPreferencesUtils.getBoolean(this.mContext, PreferenceInfo.Setting.RESUME_VIDEO, true);
    }

    public void setResumeVideo(boolean z) {
        SharedPreferencesUtils.putBoolean(this.mContext, PreferenceInfo.Setting.RESUME_VIDEO, z);
    }

    public int getVideoMode() {
        return SharedPreferencesUtils.getInt(this.mContext, PreferenceInfo.Setting.VIDEO_MODE, 1);
    }

    public void setVideoMode(int i) {
        SharedPreferencesUtils.putInt(this.mContext, PreferenceInfo.Setting.VIDEO_MODE, i);
    }

    public boolean isPitchToZoom() {
        return SharedPreferencesUtils.getBoolean(this.mContext, PreferenceInfo.Setting.PITCH_TO_ZOOM, true);
    }

    public void setPitchToZoom(boolean z) {
        SharedPreferencesUtils.putBoolean(this.mContext, PreferenceInfo.Setting.PITCH_TO_ZOOM, z);
    }

    public boolean isSlideForSound() {
        return SharedPreferencesUtils.getBoolean(this.mContext, PreferenceInfo.Setting.SLIDE_FOR_SOUND, true);
    }

    public void setSlideForSound(boolean z) {
        SharedPreferencesUtils.putBoolean(this.mContext, PreferenceInfo.Setting.SLIDE_FOR_SOUND, z);
    }

    public boolean isSlideForBrightness() {
        return SharedPreferencesUtils.getBoolean(this.mContext, PreferenceInfo.Setting.SLIDE_FOR_BRIGHTNESS, true);
    }

    public void setSlideForBrightness(boolean z) {
        SharedPreferencesUtils.putBoolean(this.mContext, PreferenceInfo.Setting.SLIDE_FOR_BRIGHTNESS, z);
    }
}
