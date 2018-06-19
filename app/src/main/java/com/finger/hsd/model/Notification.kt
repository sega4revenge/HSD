package com.finger.hsd.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class Notification : RealmObject() , Serializable{
    @PrimaryKey
    var _id : String?=null
    var idinvite : Invite? = null
    var id_product    : String? = null
    var status_expiry : String?  = null // trạng thái thông báo
    var idgeneral : General?=null
    var type : Int?=null
    var create_at : String?=null
    var content : String?=null
    var watched : Boolean?=false
    var delete : Boolean = false // if(false) chưa xóa else xóa rồi
    var isSync : Boolean = true // if(false) chưa đồng bộ ELSE đồng bộ rồi
    var expiredtime : Long? = null

}