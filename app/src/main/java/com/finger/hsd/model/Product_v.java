package com.finger.hsd.model;


import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Product_v extends RealmObject implements Serializable {

    @PrimaryKey
    private String _id;
    private String namechanged;
    private String barcode;
    private long expiretime;
    private ProductType_v producttype_id;
    private String description;
    private String imagechanged;
//    private String imagepassed;
    private boolean delete;
    public int daybefore;
    public boolean checksync = true;

    public boolean isChecksync() {
        return checksync;
    }

    public void setChecksync(boolean checksync) {
        this.checksync = checksync;
    }
    private boolean isSyn;

    public ProductType_v getProducttype_id() {
        return producttype_id;
    }

    public void setProducttype_id(ProductType_v producttype_id) {
        this.producttype_id = producttype_id;
    }

    public boolean isSyn() {
        return isSyn;
    }

    public void setSyn(boolean syn) {
        isSyn = syn;
    }

//    public String getImagepassed() {
//        return imagepassed;
//    }
//
//    public void setImagepassed(String imagepassed) {
//        this.imagepassed = imagepassed;
//    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Product_v(){}
    public Product_v(String namechanged,String barcode,long expiretime,String description,String imagechanged){
        this.namechanged = namechanged;
        this.barcode = barcode;
        this.expiretime = expiretime;
        this.description = description;
        this.imagechanged = imagechanged;
    }
    public Product_v(String _id,String namechanged,String barcode,long expiretime,String description,String imagechanged){
        this.namechanged = namechanged;
        this.barcode = barcode;
        this.expiretime = expiretime;
        this.description = description;
        this.imagechanged = imagechanged;
        this._id = _id;
    }

    public ProductType_v getProductTypeId() {
        return producttype_id;
    }

    public void setProductTypeId(ProductType_v productTypeId) {
        this.producttype_id = productTypeId;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "nameproduct: " + namechanged + ", barcode: "+barcode + ", hsd_ex: "+expiretime + ", detailproduct: "+description;
    }

    public String getNamechanged() {
        return namechanged;
    }

    public void setNamechanged(String namechanged) {
        this.namechanged = namechanged;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public long getExpiretime() {
        return expiretime;
    }

    public void setExpiretime(long expiretime) {
        this.expiretime = expiretime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagechanged() {
        return imagechanged;
    }

    public void setImagechanged(String imagechanged) {


        this.imagechanged = imagechanged;


    }
}
