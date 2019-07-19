package com.lgmn.iotserver.model;

import lombok.Data;

@Data
public class ControlEntity {
    private boolean finish;
    private String userInfo;
//    private String date;
    private String modelName;

    private String orderNo;
//    private String number;

    private String width;
    private String specs;
//    private String lines;
    private String quantity;
    private String floor;
//    private String meterialQuality;
    private String colorNum;
//    private String metre;
//    private String weight;
    private String quanTotal;
    private String weightTotal;
    private String packNum;
    private int status;
    private int currPackNum;
    private int nextPackNum;

    public void setPackNum(String packNum) {
        this.packNum = packNum;
        this.finish = true;
    }
}