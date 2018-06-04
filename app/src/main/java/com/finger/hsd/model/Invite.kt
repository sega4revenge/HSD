package com.finger.hsd.model


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Invite : RealmObject() {
    @PrimaryKey
    var _id : String?=null
    var userinvited : User? = null
    var userreceived : User? = null
    var status : Int?=null
    var created_at : String?=null
}