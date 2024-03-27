package com.geocomply.techx_app.model.logistic;

import java.io.Serializable;

public class Province implements Serializable {
    private int ProvinceID;
    private String ProvinceName;
    private String Code;

    public Province() {
    }

    public int getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(int provinceID) {
        ProvinceID = provinceID;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
