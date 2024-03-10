package com.geocomply.techx_app.model;

import java.io.Serializable;
import java.util.List;

public class ShoppingCart implements Serializable {
    private int id;
    private int userId;
    private int status;
    private List<CartItem> cartItems;
    private User userIdNavigation;

    public ShoppingCart(int userId, List<CartItem> cartItems) {
        this.userId = userId;
        this.cartItems = cartItems;
    }

    public ShoppingCart() {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public User getUserIdNavigation() {
        return userIdNavigation;
    }

    public void setUserIdNavigation(User userIdNavigation) {
        this.userIdNavigation = userIdNavigation;
    }
}
