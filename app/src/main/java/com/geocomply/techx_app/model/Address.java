package com.geocomply.techx_app.model;

import java.io.Serializable;

public class Address implements Serializable {
    private int id;
    private int userId;
    private String name;
    private String phone;
    private String detail;
    private String ward;
    private String city;
    private String province;
    private String note;
    private int status;
    private User userIdNavigation;

    public Address() {
    }

    public Address(int userId, String name, String phone, String detail, String ward, String city, String province) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.detail = detail;
        this.ward = ward;
        this.city = city;
        this.province = province;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
}
