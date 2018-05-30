package com.finger.hsd.model

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.RealmList
open class Setting :  RealmObject() {
    @PrimaryKey
    var _id: String? = null
    var timetoalarm: Int? = null
    var timezone:Int? = null
    var typeofsound: Int? = null
    var frame_time  : RealmList<String>? = null
    var created_at: String? = null
}