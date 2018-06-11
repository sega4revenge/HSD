package com.finger.hsd.util

internal object Constants {



    val BASE_URL = "https://hsdvn.ga:7070/"
//    val IMAGE_URL = Constants.BASE_URL + "getimage/?image="
//val BASE_URL = "http://192.168.1.56:7070/"
val IMAGE_URL = Constants.BASE_URL + "getimage/?image="

    val  DEBUG = true
//    val BASE_URL = "https://hsdvn.ga:7070/"

    val URL_DETAIL_PRODUCT = BASE_URL + "product/get-one-product"
    val URL_DELETE_PRODUCT = BASE_URL+ "product/delete-product"
    val URL_UPDATE_INFOMATION_PRODUCT = BASE_URL+ "product/update-infomation"
    val URL_CHANGE_DAY_BEFORE = BASE_URL + "product/changedaybefore"
    val URL_UPDATE_IMAGE = BASE_URL +"product/update-image"

    val FOTGOTPASSWORD = 1

//    for Activity result
    val REQUEST_DAY_BEFORE = 11
    val RESULT_DAY_BEFORE = 11
    val DATA_DAY_BEFORE = "dataDateBefore"
    val REQUEST_CHANGE_IMAGE = 12

}