package com.finger.hsd.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Notification : RealmObject() {
    @PrimaryKey
    val _id : String?=null
    val iduser : User? = null
    val idinvite : Invite? = null
    val idproduct    : Product? = null
    val idgeneral : General?=null
    val type : Int?=null
    val created_at : String?=null
    val content : String?=null
    val watched : Boolean?=null
}