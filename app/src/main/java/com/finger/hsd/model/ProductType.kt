package com.finger.hsd.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ProductType : RealmObject() {
    @PrimaryKey
    var _id : String?=null
    var barcode : String?=null
    var name : String?=null
    var image : String?=null
    var description : String?=null
    var check_barcode : Boolean?=null
    var created_at : String?=null
}