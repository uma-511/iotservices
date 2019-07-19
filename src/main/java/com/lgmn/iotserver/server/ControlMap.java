package com.lgmn.iotserver.server;

import com.lgmn.iotserver.model.ControlEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControlMap {
    private static Map<String, ControlEntity> map=new ConcurrentHashMap<>();

    public static void add(String clientId,ControlEntity controlEntity){

        map.put(clientId,controlEntity);

    }

    public static ControlEntity get(String clientId){

        return map.get(clientId);

    }

    public static void remove(ControlEntity controlEntity){

        for (Map.Entry entry:map.entrySet()){

            if (entry.getValue()==controlEntity){

                map.remove(entry.getKey());

            }

        }

    }

    public static void remove(String ip){
        map.remove(ip);
    }
}