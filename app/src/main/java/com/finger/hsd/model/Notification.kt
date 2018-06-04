package com.finger.hsd.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Notification : RealmObject() {
    @PrimaryKey
    var _id : String?=null
    var iduser : User? = null
    var idinvite : Invite? = null
    var idproduct    : Product? = null
    var idgeneral : General?=null
    var type : Int?=null
    var created_at : String?=null
    var content : String?=null
    var watched : Boolean?=null
}