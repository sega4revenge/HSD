package com.finger.hsd.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class ProductType_v : RealmObject(), Serializable {
    @PrimaryKey
    var id: String?= null
    var barcode: String?= null
    var name: String?= null
    var image: String? = null
    var check_product : Boolean = false // Kiểm duyệt sản phẩm
    var check_barcode : Boolean = false // kiểm tra có phải là barcode thật hay không. Có một số sản phẩm không có barcode.
    var create_at: Int? = null
    var v: Int? = null


}
