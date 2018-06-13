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

    constructor(context: Context) {
        this.mContext = context
        this.mSharedPreferences = mContext!!.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
        this.mEditor = mSharedPreferences!!.edit()

    }

    // save value friend notification __($_^)|__|()|/|@
    fun setCountNotification(count: Int) {
        mEditor!!.putInt(KEY_COUNT_NOTIFICATION, count)
        mEditor!!.commit()
    }


    fun getCountNotification(): Int {
        return mSharedPreferences!!.getInt(KEY_COUNT_NOTIFICATION, 0)
    }
}