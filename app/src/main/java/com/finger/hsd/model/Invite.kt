package com.finger.hsd.model


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Invite : RealmObject() {
    @PrimaryKey
    val _id : String?=null
    val userinvited : User? = null
    val userreceived : User? = null
    val status : Int?=null
    val created_at : String?=null
}