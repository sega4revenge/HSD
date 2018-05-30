package com.finger.hsd.model


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User() : RealmObject() {
    @PrimaryKey
    var _id: String? = null
    var iduser: String? = null
    var password: String? = null
    var tokenfirebase: String? = null
    var image: String? = null
    var name: String? = null
    var setting : Setting?=Setting()
    var created_at: String? = null
    var facebook : String? = null
    var google : String? = null

}