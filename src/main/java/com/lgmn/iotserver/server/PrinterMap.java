package com.lgmn.iotserver.server;

import com.lgmn.iotserver.model.LoginEntity;
import com.lgmn.utils.printer.Printer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: TJM
 * @Date: 2020/3/27 16:24
 */
public class PrinterMap {
    private static Map<String, Printer> map=new ConcurrentHashMap<>();

    public static void add(String ip,Printer printer){

        map.put(ip, printer);

    }

    public static Printer get(String clientId){

        return map.get(clientId);

    }

    public static void remove(Printer printer){

        for (Map.Entry entry:map.entrySet()){

            if (entry.getValue()==printer){
                printer.close();
                map.remove(entry.getKey());

            }

        }

    }

    public static void remove(String ip){
        map.get(ip).close();
        map.remove(ip);
    }
}