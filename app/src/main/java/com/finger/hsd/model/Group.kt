package com.finger.hsd.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Group : RealmObject() {
    @PrimaryKey
    var _id : String?=null
    var name : String?=null
    var owner : User? = null
    var listuser : RealmList<User>?=null
    var listproduct : RealmList<Product>?=null
    var listinvited : RealmList<Invite>?=null
    var created_at : String?=null
}