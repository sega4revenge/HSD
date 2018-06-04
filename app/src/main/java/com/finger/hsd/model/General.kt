package com.finger.hsd.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class General : RealmObject() {
    @PrimaryKey
    var _id : String?=null

    var created_at : String?=null
    var title : String?=null
    var watched : Boolean?=null
    var description : String?=null

}