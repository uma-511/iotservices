package com.lgmn.iotserver.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrintPojo implements Serializable {
    String labelNum;
    String modelName;
    String width;
    String color;
    String perPackQuantity;
    String specs;
    String prodUser;
    String jitai;
    String packNum;
    String prodDate;
}