package com.lgmn.iotserver.server;

import com.lgmn.iotserver.model.LoginEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginMap {
    private static Map<String, LoginEntity> map=new ConcurrentHashMap<>();

    public static void add(String clientId,LoginEntity loginEntity){

        map.put(clientId,loginEntity);

    }

    public static LoginEntity get(String clientId){

        return map.get(clientId);

    }

    public static void remove(LoginEntity loginEntity){

        for (Map.Entry entry:map.entrySet()){

            if (entry.getValue()==loginEntity){

                map.remove(entry.getKey());

            }

        }

    }

    public static void remove(String ip){
       map.remove(ip);
    }
}