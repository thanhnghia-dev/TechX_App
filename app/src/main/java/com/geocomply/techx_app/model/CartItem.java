package com.geocomply.techx_app.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    public int id;
    public int cartId;
    public int productId;
    public int amount;
    public ShoppingCart cartIdNavigation;
    public Product prodIdNavigation;

    public CartItem(int productId, int amount) {
        this.productId = productId;
        this.amount = amount;
    }

    public CartItem() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ShoppingCart getCartIdNavigation() {
        return cartIdNavigation;
    }

    public void setCartIdNavigation(ShoppingCart cartIdNavigation) {
        this.cartIdNavigation = cartIdNavigation;
    }

    public Product getProdIdNavigation() {
        return prodIdNavigation;
    }

    public void setProdIdNavigation(Product prodIdNavigation) {
        this.prodIdNavigation = prodIdNavigation;
    }
}
