package com.finger.hsd.util

internal object Constants {



    val BASE_URL = "https://hsdvn.ga:7070/"
    val IMAGE_URL = Constants.BASE_URL + "getimage/?image="
    val FACEBOOK =1
    val GOOGLE = 2
    val LOCAL = 0
    val BORROW = 1
    val NEEDBORROW = 2
    val VIEW_MODE = "view_mode"
    val VIEW_MODE_GRID = 1
    val VIEW_MODE_LIST = 2
    val VIEW_MODE_COMPACT = 3
    val TABLE_USER = "user_settings"
    val LAST_SELECTED = "last_drawer_selection"
    val THUMBNAIL_SIZE = "thumbnail_size"
    val RESULD_ADD_ERROR = 1
    val FOTGOTPASSWORD = 1
    val CREATEPRODUCT = 1


    val MAPS_INTENT_URI = "geo:0,0?q="

    val VIEW_TYPE = "view_type"

    val SUCCESS = "success"

    val FALSE = "false"

    val VIEW_TYPE_NEW = 1
    val VIEW_TYPE_NEAR = 2

    val VIEW_TYPE_TO_SEE = 6

    val search_text = "search_text"
    val cate_text = "cate_text"
    val area_text = "area_text"
    val fitter_text = "fitter_text"
    // Intent Extra + Bundle Argument + Saved Instance State
    val TOOLBAR_TITLE = "toolbar_title"
    val product_ID = "product_id"
    val comment_ID = "comment_id"
    val user_ID = "user_id"
    val currency_ID = "currency_id"
    val currency_RATE = "currency_rate"

    val product_NAME = "product_name"
    val product_OBJECT = "product_object"
    val seller_DETAIL = "seller_detail"
    val seller_ID = "seller_id"
    val user_name = "user_name"
    val seller_name = "seller_name"
    val product_LIST = "product_list"
    val COMMENT_TYPE = "COMMENT_type"
    val COMMENT_LIST = "COMMENT_list"

    val PAGE_TO_DOWNLOAD = "page_to_download"

    val IS_LOADING = "is_loading"
    val IS_LOCKED = "is_locked"
    val COMMENT_TYPE_CAST = 1


    val TAG_GRID_FRAGMENT = "product_grid_fragment"
}