package com.geocomply.techx_app.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private int id;
    private int userId;
    private int addrId;
    private double total;
    private String paymentMethod;
    private String orderDate;
    private String paymentDate;
    private int status;
    private List<OrderDetail> orderDetails;
    private Address addrIdNavigation;
    private User userIdNavigation;

    public Order() {
    }

    public Order(int userId, int addrId, double total, String paymentMethod, List<OrderDetail> orderDetails) {
        this.userId = userId;
        this.addrId = addrId;
        this.total = total;
        this.paymentMethod = paymentMethod;
        this.orderDetails = orderDetails;
    }

    public Order(int userId, int addrId, double total, String paymentMethod) {
        this.userId = userId;
        this.addrId = addrId;
        this.total = total;
        this.paymentMethod = paymentMethod;
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

    public int getAddrId() {
        return addrId;
    }

    public void setAddrId(int addrId) {
        this.addrId = addrId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Address getAddrIdNavigation() {
        return addrIdNavigation;
    }

    public void setAddrIdNavigation(Address addrIdNavigation) {
        this.addrIdNavigation = addrIdNavigation;
    }

    public User getUserIdNavigation() {
        return userIdNavigation;
    }

    public void setUserIdNavigation(User userIdNavigation) {
        this.userIdNavigation = userIdNavigation;
    }
}
