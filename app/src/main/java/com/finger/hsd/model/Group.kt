package com.finger.hsd.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Group : RealmObject() {
    @PrimaryKey
    var _id : String?=null
    val name : String?=null
    val owner : User? = null
    val listuser : RealmList<User>?=null
    val listproduct : RealmList<Product>?=null
    val listinvited : RealmList<Invite>?=null
    val created_at : String?=null
}