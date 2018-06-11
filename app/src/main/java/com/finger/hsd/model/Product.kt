package com.finger.hsd.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Product : RealmObject {



    @PrimaryKey
    var _id : String?=null
    var productTypeId : ProductType?=null
    var createtime : String?=null
    var expiretime : String?=null
    var delete : Boolean?=null
    var namechange : String?=null
    var imageChange : String?=null
    var daybefor : Int? = null
    var description: String?=null
    var created_at : String?=null

    constructor(_id: String, nameChange: String, imageChange: String, expiredTime: String,
                daybefore: Int, delete: Boolean, description: String){
        this._id= _id
        this.namechange = nameChange
        this.imageChange= imageChange
        this.expiretime = expiredTime
        this.daybefor = daybefore
        this.delete= delete
        this.description = description
    }

    constructor()
}