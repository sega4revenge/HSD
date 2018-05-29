package com.finger.hsd.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class General : RealmObject() {
    @PrimaryKey
    val _id : String?=null

    val created_at : String?=null
    val title : String?=null
    val watched : Boolean?=null
    val description : String?=null

}