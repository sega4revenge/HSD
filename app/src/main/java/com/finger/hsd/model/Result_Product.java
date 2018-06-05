package com.finger.hsd.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result_Product {

    private Integer status;
    private List<ProductType> ProductType = null;
    private String message;
    private Product_v Product;
    private ProductType_v productType_v;
    public ArrayList<Product_v> getListProduct() {
        return ListProduct;
    }
    private ArrayList<Product_v> ListProduct = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public ProductType_v getProductType_v() {
        return productType_v;
    }

    public void setProductType_v(ProductType_v productType_v) {
        this.productType_v = productType_v;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<com.finger.hsd.model.ProductType> getProductType() {
        return ProductType;
    }

    public void setProductType(List<com.finger.hsd.model.ProductType> productType) {
        ProductType = productType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Product_v getProduct() {
        return Product;
    }

    public void setProduct(Product_v product) {
        Product = product;
    }

    public void setListProduct(ArrayList<Product_v> listProduct) {
        ListProduct = listProduct;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}