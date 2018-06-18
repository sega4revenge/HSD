package com.finger.hsd.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SessionManager {
     var PRIVATR_MODE : Int = 0

    lateinit var mSharedPreferences : SharedPreferences
    lateinit var mEditor : SharedPreferences.Editor
    lateinit var mContext: Context

    @SuppressLint("CommitPrefEdits")
    constructor(mContext  :Context ){
        this.mContext = mContext
        mSharedPreferences = mContext.getSharedPreferences(KEY_SETTING_NOTI_ALARM,PRIVATR_MODE)
        mSharedPreferences = mContext.getSharedPreferences(KEY_SOUND_SETTING,PRIVATR_MODE)
        mEditor = mSharedPreferences.edit()
    }

    companion object {
        private const val KEY_SETTING_NOTI_ALARM : String = "settingAlarm"
        private const val KEY_SOUND_SETTING  : String = "settingSound"

    }
    fun set_Sound(ischeck: String) {
        mEditor.putString(KEY_SOUND_SETTING, ischeck)
        mEditor.commit()
    }



    fun get_Sound(): String {
        return mSharedPreferences.getString(KEY_SOUND_SETTING, "suri_chipmunk")
    }



    fun set_open_Alarm(ischeck: Boolean) {
        mEditor.putBoolean(KEY_SETTING_NOTI_ALARM, ischeck)
        mEditor.commit()
    }



    fun get_open_Alarm(): Boolean {
        return mSharedPreferences.getBoolean(KEY_SETTING_NOTI_ALARM, true)
    }
}