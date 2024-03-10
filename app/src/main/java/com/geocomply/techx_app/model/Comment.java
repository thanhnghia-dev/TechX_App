package com.geocomply.techx_app.model;

import java.io.Serializable;

public class Comment implements Serializable {
    private int id;
    private int userId;
    private int productId;
    private String content;
    private int rating;
    private int status;
    private User userIdNavigation;
    private Product prodIdNavigation;

    public Comment() {
    }

    public Comment(int userId, int productId, String content, int rating) {
        this.userId = userId;
        this.productId = productId;
        this.content = content;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public User getUserIdNavigation() {
        return userIdNavigation;
    }

    public void setUserIdNavigation(User userIdNavigation) {
        this.userIdNavigation = userIdNavigation;
    }

    public Product getProdIdNavigation() {
        return prodIdNavigation;
    }

    public void setProdIdNavigation(Product prodIdNavigation) {
        this.prodIdNavigation = prodIdNavigation;
    }
}
