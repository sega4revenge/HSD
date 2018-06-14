package com.finger.hsd.util

import android.content.Context
import android.content.SharedPreferences

open class SessionManager {

    private val PREFERENCES = "preferences"
    private val TAG = SessionManager::class.java.simpleName
    private var mSharedPreferences: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null
    private var mContext: Context? = null

    private val KEY_COUNT_NOTIFICATION = "countNotification"
    private val KEY_LOGIN = "isLogin"

    constructor(context: Context) {
        this.mContext = context
        this.mSharedPreferences = mContext!!.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        this.mEditor = mSharedPreferences!!.edit()

    }

    // save value caculate notification __($_^)|__|()|/|@
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

}