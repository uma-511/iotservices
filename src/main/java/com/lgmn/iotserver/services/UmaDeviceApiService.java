package com.lgmn.iotserver.services;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lgmn.common.service.LgmnAbstractApiService;
import com.lgmn.umaservices.basic.dto.UmaDeviceDto;
import com.lgmn.umaservices.basic.entity.UmaDeviceEntity;
import com.lgmn.umaservices.basic.service.UmaDeviceService;
import org.springframework.stereotype.Component;

@Component
public class UmaDeviceApiService extends LgmnAbstractApiService<UmaDeviceEntity, UmaDeviceDto, Integer, UmaDeviceService> {

    @Reference(version = "${demo.service.version}", timeout = 20000)
    private UmaDeviceService service;

    @Override
    public void initService() {
        setService(service);
    }

    public UmaDeviceEntity findByIp(String ip){
       return service.findByIp(ip);
    }

    public UmaDeviceEntity getPrinterByTerminal(String ip){
        return service.findPrinterByTerminal(ip);
    }
}