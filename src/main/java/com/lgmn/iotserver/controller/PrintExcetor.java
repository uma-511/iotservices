package com.lgmn.iotserver.controller;

import com.lgmn.iotserver.holder.PrintTaskHolder;
import com.lgmn.iotserver.model.PrintTask;
import com.lgmn.utils.printer.Printer;

/**
 * @Author: TJM
 * @Date: 2020/3/27 13:27
 */
public class PrintExcetor {

    private PrintExcetor(){
        if(PrintExcetorHolder.printExcetor!=null){
            throw new RuntimeException("不允许创建多个实例");
        }
    }

    public static final PrintExcetor getInstance(){
        return PrintExcetorHolder.printExcetor;
    }

    private static class PrintExcetorHolder{
        private static final PrintExcetor printExcetor = new PrintExcetor();
    }

    public synchronized void print(PrintTask printTask){
        Printer printer = printTask.getPrinter();
        printer.print();
        PrintTaskHolder.getInstance().removeData();
    }
}