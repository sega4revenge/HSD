package com.finger.hsd.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result_Product {

    private Integer status;
    private List<ProductType> ProductType = null;

    public ArrayList<Product_v> getListProduct() {
        return ListProduct;
    }

    private ArrayList<Product_v> ListProduct = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<ProductType> getProduct() {

        return ProductType;
    }

    public void setListProduct(ArrayList<Product_v> listProduct) {
        ListProduct = listProduct;
    }

    public void setProduct(List<ProductType> ProductType) {
        this.ProductType = ProductType;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}