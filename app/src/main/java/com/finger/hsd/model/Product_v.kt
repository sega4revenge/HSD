package com.finger.hsd.model


import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class Product_v : RealmObject, Serializable {

    //    public String getImagepassed() {
    //        return imagepassed;
    //    }
    //
    //    public void setImagepassed(String imagepassed) {
    //        this.imagepassed = imagepassed;
    //    }

    @PrimaryKey
    var _id: String?= null
    var namechanged: String?= null
    var barcode: String? = null
    var expiretime: Long = 0
    var producttype_id: ProductType_v?= null
    var description: String? = null
    var imagechanged: String? = null
    //    private String imagepassed;
    var delete: Boolean = false
    var daybefore: Int = 0
    var isChecksync = true
    var create_at: Long? = null
    var isSyn = true

    constructor() {}
    constructor(namechanged: String, barcode: String, expiretime: Long, description: String, imagechanged: String) {
        this.namechanged = namechanged
        this.barcode = barcode
        this.expiretime = expiretime
        this.description = description
        this.imagechanged = imagechanged
    }

    constructor(_id: String, namechanged: String, barcode: String, expiretime: Long, description: String, imagechanged: String) {
        this.namechanged = namechanged
        this.barcode = barcode
        this.expiretime = expiretime
        this.description = description
        this.imagechanged = imagechanged
        this._id = _id
    }


    override fun toString(): String {
        return "nameproduct: $namechanged, barcode: $barcode, hsd_ex: $expiretime, detailproduct: $description"
    }
}
