package com.lgmn.iotserver.server;

import javax.print.*;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TSCPrintTest {
    public static void main(String[] args) {
        FileInputStream textStream = null;
        try {
            textStream = new FileInputStream("C:/Users/Lonel/Desktop/微信图片_20190627121041.jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (textStream != null) // 当打印内容不为空时
        {
            // 指定打印输出格式
            DocFlavor flavor = DocFlavor.INPUT_STREAM.JPEG;//SERVICE_FORMATTED.PRINTABLE
            // 定位默认的打印服务
            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
            // 创建打印作业
            DocPrintJob job = printService.createPrintJob();
            // 设置打印属性
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            // 设置纸张大小,也可以新建MediaSize类来自定义大小
//            pras.add(MediaSizeName.ISO_A4);
            DocAttributeSet das = new HashDocAttributeSet();
            // 指定打印内容
            Doc doc = new SimpleDoc(textStream, flavor, das);
            // 不显示打印对话框，直接进行打印工作
            try {
                job.print(doc, pras); // 进行每一页的具体打印操作
            } catch (PrintException pe) {
                pe.printStackTrace();
            }
        } else {
            // 如果打印内容为空时，提示用户打印将取消
            JOptionPane.showConfirmDialog(null,
                    "Sorry, Printer Job is Empty, Print Cancelled!",
                    "Empty", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}