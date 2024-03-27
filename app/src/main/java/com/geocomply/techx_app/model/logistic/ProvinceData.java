package com.geocomply.techx_app.model.logistic;

import java.io.Serializable;
import java.util.ArrayList;

public class ProvinceData implements Serializable {
    private int code;
    private String message;
    private ArrayList<Province> data;

    public ProvinceData() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Province> getData() {
        return data;
    }

    public void setData(ArrayList<Province> data) {
        this.data = data;
    }
}
