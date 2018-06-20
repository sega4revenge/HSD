package com.finger.hsd.util

internal object Constants {



//    val BASE_URL = "https://hsdvn.ga:7070/"
//    val IMAGE_URL = Constants.BASE_URL + "getimage/?image="
val BASE_URL = "https://hsdvn.ga/"
val IMAGE_URL = Constants.BASE_URL + "getimage/?image="

    val  DEBUG = true
//    val BASE_URL = "https://hsdvn.ga:7070/"

    val URL_DETAIL_PRODUCT = BASE_URL + "product/get-one-product"
    val URL_DELETE_PRODUCT = BASE_URL+ "product/delete_product"
    val URL_UPDATE_INFOMATION_PRODUCT = BASE_URL+ "product/update-infomation"
    val URL_CHANGE_DAY_BEFORE = BASE_URL + "product/changedaybefore"
    val URL_UPDATE_IMAGE = BASE_URL +"product/upload_image_product"
//    -====Login==============
    val URL_LOGIN = BASE_URL +"login-android"
    val URL_REGISTER_USER = BASE_URL +"register-android"

    val URL_UPDATE_NOTIFICATION = BASE_URL+"notification/update_or_add_notification"

    val FOTGOTPASSWORD = 1

//    for Activity result
    val REQUEST_DAY_BEFORE = 11
    val RESULT_DAY_BEFORE = 11
    val DATA_DAY_BEFORE = "dataDateBefore"
    val REQUEST_CHANGE_IMAGE = 12

}