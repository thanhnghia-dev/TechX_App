package com.geocomply.techx_app.model.logistic;

import java.io.Serializable;
import java.util.ArrayList;

public class WardData implements Serializable {
    private int code;
    private String message;
    private ArrayList<Ward> data;

    public WardData() {
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

    public ArrayList<Ward> getData() {
        return data;
    }

    public void setData(ArrayList<Ward> data) {
        this.data = data;
    }
}
