package com.finger.hsd.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Notification : RealmObject() {

    @PrimaryKey
    var _id : String?=null
    var idinvite : Invite? = null
    var id_product    : String? = null
    var idgeneral : General?=null
    var type : Int?=null
    var countProductExprited : Int? = null
    var create_at : String?=null
    var content : String?=null
    var watched : Boolean?=false
    var namechanged : String? = null
    var imagechanged : String? = null
    var expiredtime : Long? = null

}