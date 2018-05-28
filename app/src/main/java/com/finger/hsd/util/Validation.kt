package com.finger.hsd.util

import android.text.TextUtils
import android.util.Patterns
import java.util.regex.Pattern

object Validation {
    //    var regexStr : String = "0\\[0-9]{10}$"
    var pattern = Pattern.compile("0\\d{9,10}")

    fun validateFields(name: String?): Boolean {

        return !(TextUtils.isEmpty(name) || name!!.trim { it <= ' ' } == "")
    }
    fun validatePassword(name: String?): Boolean {

        return !(TextUtils.isEmpty(name) || name!!.trim { it <= ' ' } == "" || name.length < 6)
    }
    fun validatePhone(name: String?): Boolean {

        return !(TextUtils.isEmpty(name) || name!!.trim { it <= ' ' } == "" || name.length < 8)
    }
    fun validatePhone2(phone : String?): Boolean? {
        var matcher = pattern.matcher(phone)
        return matcher.matches()
    }
    fun validateEmail(string: String?): Boolean {

        return !(TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches() || string!!.trim { it <= ' ' } == "")
    }
}
