package com.lgmn.iotserver.server;

import com.lgmn.iotserver.command.UmaCommand;
import com.lgmn.iotserver.controller.IotController;
import com.lgmn.iotserver.utils.CoderUtils;
import com.lgmn.iotserver.utils.ExcelUtils;
import com.lgmn.iotserver.utils.PrintUtil;
import com.lgmn.umaservices.basic.entity.ViewLabelRecordEntity;
import org.apache.commons.lang3.StringUtils;

import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;

public class PrintTest {
    public static void main(String[] args) {
//        String path="D:/develop/py_project/pdfconver/Sheet1.pdf";
//        String path="D:/develop/py_project/pdfconver/temp_0.png";

        String path="C:/Users/Lonel/Desktop/test.xlsx";
//		printFileAction(path);
//        List<PrintService> printServices = PrintUtil.getDeviceList();
//        for(PrintService item : printServices ){
//            System.out.println(item.getName());
//        }
/**
 * 单号：系统生成 年月日+流水号
 * 条码：类型（一位）+年月（4位）+ 6位跳码 + 机台号（两位）
// */
//        try {
//            ViewLabelRecordEntity viewLabelRecordEntity = new ViewLabelRecordEntity();
//            viewLabelRecordEntity.setLabelNum("234253465768");
//            viewLabelRecordEntity.setModelName("型号");
//            viewLabelRecordEntity.setSpecs("规格");
//            viewLabelRecordEntity.setColor("色号");
//            viewLabelRecordEntity.setPerPackQuantity(20);
//            viewLabelRecordEntity.setNetWeight(new BigDecimal("20"));
//            viewLabelRecordEntity.setProdUser("022");
//
//            path=ExcelUtils.exportLabelExcel(path,viewLabelRecordEntity);
//            PrintUtil.print(path);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

    }
}