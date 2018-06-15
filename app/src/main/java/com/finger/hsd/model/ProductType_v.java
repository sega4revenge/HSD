package com.finger.hsd.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ProductType_v  extends RealmObject {
   @PrimaryKey
    private String _id;
    private String barcode;
    private String name;
    private String image;
    private Integer createAt;
    private Integer v;



    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Integer createAt) {
        this.createAt = createAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }


}
