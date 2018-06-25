package com.finger.hsd.model

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User() : Parcelable, RealmObject() {

    @PrimaryKey
    var _id: String? = null
    var phone : String? = null
    var password: String? = null
    var tokenfirebase: String? = null
    var setting : Setting?=Setting()
    var created_at: String? = null
    var type_login : Int? = 0
    var facebook : String? = null
    var google : String? = null
    var listgroup : RealmList<Group>?=null
    var listnotification : RealmList<Notification>?=null

    constructor(parcel: Parcel) : this() {
        _id = parcel.readString()
        password = parcel.readString()
        tokenfirebase = parcel.readString()
        created_at = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(password)
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