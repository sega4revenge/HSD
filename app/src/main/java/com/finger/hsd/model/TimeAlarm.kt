package com.finger.hsd.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TimeAlarm : RealmObject {
    @PrimaryKey
    var listtime : Int? = null
    var isSelected : Boolean? = null

    constructor() : super()

    constructor(listtime: Int?, isSelected: Boolean?) : super() {
        this.listtime = listtime
        this.isSelected = isSelected
    }
}