package com.finger.hsd.util


import android.util.Log

/**
 * Created by Duong on 8/4/2017.
 */

object Mylog {

    val LOG_TAG = Mylog::class.java.simpleName


    /** Log Level Error  */
    fun e(message: String) {

        if (Constants.DEBUG)
            Log.e(LOG_TAG, buildLogMsg(message))
    }

    fun w(message: String) {
        if (Constants.DEBUG)
            Log.w(LOG_TAG, buildLogMsg(message))
    }


    /** Log Level Error  */
    fun e(tag: String, message: String) {
        if (Constants.DEBUG)
            Log.e(tag, buildLogMsg(message))
    }

    fun e() {
        if (Constants.DEBUG)
            Log.e(LOG_TAG, buildLogMsg(""))
    }


    /** Log Level Warning  */
    fun w(tag: String, message: String) {
        if (Constants.DEBUG)
            Log.w(tag, buildLogMsg(message))
    }

    fun i() {
        if (Constants.DEBUG)
            Log.i(LOG_TAG, buildLogMsg(""))
    }

    /** Log Level Information  */
    fun i(message: String) {
        if (Constants.DEBUG)
            Log.i(LOG_TAG, buildLogMsg(message))
    }

    /** Log Level Information  */
    fun i(tag: String, message: String) {
        if (Constants.DEBUG)
            Log.i(tag, buildLogMsg(message))
    }

    fun d() {
        if (Constants.DEBUG)
            Log.d(LOG_TAG, buildLogMsg(""))
    }

    /** Log Level Debug  */
    fun d(message: String) {
        if (Constants.DEBUG)
            Log.d(LOG_TAG, buildLogMsg(message))
    }

    /** Log Level Debug  */
    fun d(tag: String, message: String) {
        if (Constants.DEBUG)
            Log.d(tag, buildLogMsg(message))
    }

    /** Log Level Verbose  */
    fun v(message: String) {
        if (Constants.DEBUG)
            Log.v(LOG_TAG, buildLogMsg(message))
    }

    /** Log Level Verbose  */
    fun v(tag: String, message: String) {
        if (Constants.DEBUG)
            Log.v(tag, buildLogMsg(message))
    }

    fun buildLogMsg(message: String): String {
        val ste = Thread.currentThread().stackTrace[4]

        val sb = StringBuilder()

        sb.append("[")
        try {
            sb.append(ste.fileName.replace(".java", ""))
        } catch (e: NullPointerException) {
            // When released ste.getFileName() is null
        }

        sb.append("::")
        sb.append(ste.methodName)
        sb.append("] ")
        sb.append(message)

        return sb.toString()
    }
}
