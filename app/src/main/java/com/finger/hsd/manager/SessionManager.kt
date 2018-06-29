package com.finger.hsd.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SessionManager//    constructor(mContext  :Context ){
//        this.mContext = mContext
//        mSharedPreferences = mContext.getSharedPreferences(KEY_SETTING_NOTI_ALARM,PRIVATR_MODE)
//        mSharedPreferences = mContext.getSharedPreferences(KEY_SOUND_SETTING,PRIVATR_MODE)
//        mEditor = mSharedPreferences.edit()
//    }
@SuppressLint("CommitPrefEdits") constructor(context: Context) {
     var PRIVATR_MODE : Int = 0
    private val PREFERENCES = "preferences"
    private var mSharedPreferences: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null
    private var mContext: Context? = context


    private val KEY_COUNT_NOTIFICATION = "countNotification"
    private val KEY_LOGIN = "isLogin"
    private  val KEY_SETTING_NOTI_ALARM : String = "settingAlarm"
    private  val KEY_SOUND_SETTING  : String = "settingSound"
    private val KEY_CHECK_IS_SYNC : String = "isCheckSync"

    fun set_Sound(ischeck: String) {
        mEditor!!.putString(KEY_SOUND_SETTING, ischeck)
        mEditor!!.commit()
    }



    fun get_Sound(): String {
        return mSharedPreferences!!.getString(KEY_SOUND_SETTING, "plucky")
    }



    fun set_open_Alarm(ischeck: Boolean) {
        mEditor!!.putBoolean(KEY_SETTING_NOTI_ALARM, ischeck)
        mEditor!!.commit()
    }



    fun get_open_Alarm(): Boolean {
        return mSharedPreferences!!.getBoolean(KEY_SETTING_NOTI_ALARM, true)
    }

    fun setCountNotification(count: Int) {
        mEditor!!.putInt(KEY_COUNT_NOTIFICATION, count)
        mEditor!!.commit()
    }


    fun getCountNotification(): Int {
        return mSharedPreferences!!.getInt(KEY_COUNT_NOTIFICATION, 0)
    }

    // check login if user was login, set data true
    fun setLogin(count: Boolean) {
        mEditor!!.putBoolean(KEY_LOGIN, count)
        mEditor!!.commit()
    }


    fun isLogin(): Boolean {
        return mSharedPreferences!!.getBoolean(KEY_LOGIN, false)
    }
    // check đang đồng bộ
    fun setIsSync(count: Boolean) {
        mEditor!!.putBoolean(KEY_CHECK_IS_SYNC, count)
        mEditor!!.commit()
    }


    fun isSync(): Boolean {
        return mSharedPreferences!!.getBoolean(KEY_CHECK_IS_SYNC, false)
    }

    init {
        this.mSharedPreferences = mContext!!.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        this.mEditor = mSharedPreferences!!.edit()
    }
}