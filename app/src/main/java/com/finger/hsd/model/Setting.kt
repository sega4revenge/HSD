package com.finger.hsd.model

import android.os.Parcel
import android.os.Parcelable

class Setting() : Parcelable {
    var _id: String? = null
    var timetoalarm: Int? = null
    var timezone:Int? = null
    var typeofsound: Int? = null

    constructor(parcel: Parcel) : this() {
        _id = parcel.readString()
        timetoalarm = parcel.readInt()
        timezone= parcel.readInt()
        typeofsound = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeInt(timetoalarm!!)
        parcel.writeInt(timezone!!)
        parcel.writeInt(typeofsound!!)
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