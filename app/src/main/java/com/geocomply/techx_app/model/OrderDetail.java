package com.geocomply.techx_app.model;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private int id;
    private int orderId;
    private int productId;
    private int amount;
    private double price;
    private int status;
    private Order orderIdNavigation;
    private Product prodIdNavigation;

    public OrderDetail() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Order getOrderIdNavigation() {
        return orderIdNavigation;
    }

    public void setOrderIdNavigation(Order orderIdNavigation) {
        this.orderIdNavigation = orderIdNavigation;
    }

    public Product getProdIdNavigation() {
        return prodIdNavigation;
    }

    public void setProdIdNavigation(Product prodIdNavigation) {
        this.prodIdNavigation = prodIdNavigation;
    }
}
