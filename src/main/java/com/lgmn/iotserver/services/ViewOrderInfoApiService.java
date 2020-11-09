package com.lgmn.iotserver.services;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lgmn.common.service.LgmnAbstractApiService;
import com.lgmn.umaservices.basic.dto.ViewOrderInfoDto;
import com.lgmn.umaservices.basic.entity.ViewOrderInfoEntity;
import com.lgmn.umaservices.basic.service.ViewOrderInfoService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewOrderInfoApiService extends LgmnAbstractApiService<ViewOrderInfoEntity, ViewOrderInfoDto, String, ViewOrderInfoService> {

    @Reference(version = "${demo.service.version}", timeout = 20000)
    ViewOrderInfoService viewOrderInfoService;

    @Override
    public void initService() {
        setService(viewOrderInfoService);
    }

    public ViewOrderInfoEntity findByDto(ViewOrderInfoDto dto){
        ViewOrderInfoEntity res=null;
        try {
            List<ViewOrderInfoEntity> list = viewOrderInfoService.getListByDto(dto);
            if(list!=null&&list.size()==1){
                res = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return res;
        }
    }
}
