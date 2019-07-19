package com.lgmn.iotserver.holder;

import com.lgmn.iotserver.model.ConnectHolderEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectHolder {
    private static Map<String, ConnectHolderEntity> map=new ConcurrentHashMap<>();

    public static void add(String clientId,ConnectHolderEntity connectHolderEntity){

        map.put(clientId,connectHolderEntity);

    }

    public static ConnectHolderEntity get(String clientId){

        return map.get(clientId);

    }

    public static void remove(ConnectHolderEntity socketChannel){

        for (Map.Entry entry:map.entrySet()){

            if (entry.getValue()==socketChannel){

                map.remove(entry.getKey());

            }

        }

    }
}