package com.finger.hsd.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProductType : RealmObject() {
    @PrimaryKey
    var _id : String?=null
    val barcode : String?=null
    val name : String?=null
    val image : String?=null
    val description : String?=null
    val check_barcode : Boolean?=null
    val created_at : String?=null
}