package com.geocomply.techx_app.model;

import java.io.Serializable;

public class Image implements Serializable {
    private int id;
    private int productId;
    private String url;
    private Product idSpNavigation;

    public Image() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Product getIdSpNavigation() {
        return idSpNavigation;
    }

    public void setIdSpNavigation(Product idSpNavigation) {
        this.idSpNavigation = idSpNavigation;
    }
}
