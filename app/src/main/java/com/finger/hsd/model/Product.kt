package com.finger.hsd.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Product : RealmObject() {
    @PrimaryKey
    var _id : String?=null
    var productid : ProductType?=null
    var createtime : String?=null
    var expiredtime : String?=null
    var delete : Boolean?=null
    var namechange : String?=null
    var created_at : String?=null
}