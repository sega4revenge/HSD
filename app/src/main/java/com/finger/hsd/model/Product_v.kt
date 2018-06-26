package com.finger.hsd.model


import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Product_v : RealmObject, Parcelable {

    @PrimaryKey
   public  var _id: String?= null
    public var namechanged: String?= null
    public var barcode: String? = null
    public var expiretime: Long = 0
    public var producttype_id: ProductType_v?= null
    public var description: String? = null
    public  var imagechanged: String? = null
    public  var delete: Boolean = false
    public  var isNewImage = false
    public  var daybefore: Int = 0
    public var createtime: Long? = null // ngày tạo product
    public var isSyn = true

    constructor() {

    }
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

    constructor(p0: Parcel) {
        readFromParcel(p0)
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0!!.writeString(_id)

    }

    override fun describeContents(): Int {
        return 0;
    }

    fun readFromParcel(p0: Parcel) {
        this._id  = p0.readString()

    }


    companion object CREATOR : Parcelable.Creator<Product_v> {
        override fun createFromParcel(parcel: Parcel): Product_v {
            return Product_v(parcel)
        }

        override fun newArray(size: Int): Array<Product_v?> {
            return arrayOfNulls(size)
        }
    }
}
