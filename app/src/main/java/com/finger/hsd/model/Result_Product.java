package com.finger.hsd.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Result_Product {

    private Integer status;
    private List<ProductType_v> ListProductType = null;
    private String message;
    private Product_v product;
    private ProductType_v ProductType;
    public ArrayList<Product_v> getListProduct() {
        return ListProduct;
    }
    private ArrayList<Product_v> ListProduct = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<com.finger.hsd.model.ProductType_v> getListProductType() {
        return ListProductType;
    }

    public void setListProductType(List<com.finger.hsd.model.ProductType_v> listProductType) {
        ListProductType = listProductType;
    }

    public ProductType_v getProductType() {
        return ProductType;
    }

    public void setProductType(ProductType_v productType) {
        ProductType = productType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Product_v getProduct() {
        return product;
    }

    public void setProduct(Product_v product) {
        product = product;
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