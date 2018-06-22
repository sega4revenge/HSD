package com.finger.hsd.util

import com.finger.hsd.model.Result_Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    @FormUrlEncoded // Dòng này để mã hóa dữ liệu trước khi post ========================
    @POST("/product/Checkbarcode") // đây tên của url trên server (url mặc định http://45.77.36.109:8070/api/v1/) =========================
    fun checkBarcode(
            @Field("barcode") barcode: String): Call<Result_Product>

    @FormUrlEncoded // Dòng này để mã hóa dữ liệu trước khi post ========================
    @POST("/product/getAllProductInGroup") // đây tên của url trên server (url mặc định http://45.77.36.109:8070/api/v1/) =========================
    fun getAllProductInGroup(
            @Field("iduser") iduser: String): Call<Result_Product>

    @FormUrlEncoded // Dòng này để mã hóa dữ liệu trước khi post ========================
    @POST("/delete-products") // đây tên của url trên server (url mặc định http://45.77.36.109:8070/api/v1/) =========================
    fun deleteGroup(
            @Field("stringProduct") stringProduct: String,
            @Field("id_user") id_user: String
            ): Call<Result_Product>


    @Multipart
    @POST("/product/add-product")
    fun addProduct(@Part("nameproduct") nameproduct: RequestBody,
                   @Part("barcode") barcode: RequestBody,
                   @Part("hsd_ex") hsd_ex: RequestBody,
                   @Part("detailproduct") detailproduct: RequestBody,
                   @Part("iduser") iduser: RequestBody,
                   @Part image: MultipartBody.Part): Call<Result_Product>

    @FormUrlEncoded
    @POST("/product/add-product-non-image")
    fun addProduct_nonImage(
            @Field("nameproduct") nameproduct: String,
            @Field("barcode") barcode: String,
            @Field("hsd_ex") hsd_ex: String,
            @Field("iduser") iduser: String,
            @Field("detailproduct") detailproduct: String): Call<Result_Product>

}

