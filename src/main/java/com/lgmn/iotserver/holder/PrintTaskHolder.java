package com.lgmn.iotserver.holder;

import com.lgmn.iotserver.controller.PrintExcetor;
import com.lgmn.iotserver.model.PrintTask;
import com.lgmn.utils.printer.Printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @Author: TJM
 * @Date: 2020/3/27 23:20
 */
public class PrintTaskHolder {
    private List<PrintTask> tasks = new ArrayList<>();

    private boolean running = false;

    private PrintTaskHolder(){
        if(PrintTaskHolderHolder.printTaskHolder!=null){
            throw new RuntimeException("不允许创建多个实例");
        }
    }

    public static final PrintTaskHolder getInstance(){
        return PrintTaskHolderHolder.printTaskHolder;
    }

    private static class PrintTaskHolderHolder{
        private static final PrintTaskHolder printTaskHolder = new PrintTaskHolder();
    }

    public void addData(Printer printer) {
        tasks.add(new PrintTask(printer));
        start();
    }

    public PrintTask getNext() {
        if (tasks.size() > 0) {
            return tasks.get(0);
        } else {
            running = false;
            return null;
        }
    }

    public void removeData() {
        tasks.remove(0);
        PrintTask printTask = getNext();
        if(printTask!=null) {
            PrintExcetor.getInstance().print(printTask);
        }
    }

    private void start(){
        if(!running) {
            running = true;
            PrintTask printTask = getNext();
            if (printTask != null) {
                PrintExcetor.getInstance().print(printTask);
            }
        }
    }
}