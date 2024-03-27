package com.geocomply.techx_app.model.logistic;

import java.io.Serializable;

public class Ward implements Serializable {
    private String WardCode;
    private int DistrictID;
    private String WardName;

    public Ward() {
    }

    public String getWardCode() {
        return WardCode;
    }

    public void setWardCode(String wardCode) {
        WardCode = wardCode;
    }

    public int getDistrictID() {
        return DistrictID;
    }

    public void setDistrictID(int districtID) {
        DistrictID = districtID;
    }

    public String getWardName() {
        return WardName;
    }

    public void setWardName(String wardName) {
        WardName = wardName;
    }
}
