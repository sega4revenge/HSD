package com.finger.hsd.model

import android.os.Parcel
import android.os.Parcelable

class User() : Parcelable {
    var _id: String? = null
    var phone: String? = null
    var hashed_password: String? = null
    var tokenfirebase: String? = null
    var setting : Setting?=Setting()
    var created_at: String? = null
    var facebook : String? = null
    var google : String? = null

    constructor(parcel: Parcel) : this() {
        _id = parcel.readString()
        phone = parcel.readString()
        hashed_password = parcel.readString()
        tokenfirebase = parcel.readString()
        created_at = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(phone)
        parcel.writeString(hashed_password)
        parcel.writeString(tokenfirebase)
        parcel.writeString(created_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}