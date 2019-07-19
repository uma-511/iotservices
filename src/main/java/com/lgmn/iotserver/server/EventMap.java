package com.lgmn.iotserver.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventMap {
    private static Map<String, String> map=new ConcurrentHashMap<>();

    public static void add(String clientId,String event){

        map.put(clientId,event);

    }

    public static String get(String clientId){

        return map.get(clientId);

    }

    public static void remove(String ip){
        map.remove(ip);
    }
}