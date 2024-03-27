package com.geocomply.techx_app.model.logistic;

import java.io.Serializable;

public class District implements Serializable {
    private int DistrictID;
    private int ProvinceID;
    private String DistrictName;

    public District() {
    }

    public int getDistrictID() {
        return DistrictID;
    }

    public void setDistrictID(int districtID) {
        DistrictID = districtID;
    }

    public int getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(int provinceID) {
        ProvinceID = provinceID;
    }

    public String getDistrictName() {
        return DistrictName;
    }

    public void setDistrictName(String districtName) {
        DistrictName = districtName;
    }

}
