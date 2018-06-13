package com.finger.hsd.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class AlarmSave : RealmObject() {
    @PrimaryKey
    var id : Int? = null
    var stampAlarm : String? = null

}