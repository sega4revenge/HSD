package com.finger.hsd.model;



import android.app.TimePickerDialog;
import android.widget.TimePicker;

import java.util.HashMap;
import java.util.Map;

public class Product_v {
    private String namechanged;
    private String barcode;
    private long expiretime;
    private String description;
    private String imagechanged;
    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    Product_v(){}
    public Product_v(String namechanged,String barcode,long expiretime,String description,String imagechanged){
        this.namechanged = namechanged;
        this.barcode = barcode;
        this.expiretime = expiretime;
        this.description = description;
        this.imagechanged = imagechanged;
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
