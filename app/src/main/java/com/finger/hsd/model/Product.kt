package com.finger.hsd.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Product : RealmObject() {
    @PrimaryKey
    var _id : String?=null
    val productid : ProductType?=null
    val createtime : String?=null
    val expiredtime : String?=null
    val delete : Boolean?=null
    val namechange : String?=null
    val created_at : String?=null
}