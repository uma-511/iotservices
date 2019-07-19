package com.lgmn.iotserver.services;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lgmn.common.service.LgmnAbstractApiService;
import com.lgmn.umaservices.basic.dto.ViewLabelRecordDto;
import com.lgmn.umaservices.basic.entity.ViewLabelRecordEntity;
import com.lgmn.umaservices.basic.service.ViewLabelRecordService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ViewLabelRecordApiService extends LgmnAbstractApiService<ViewLabelRecordEntity, ViewLabelRecordDto, Integer, ViewLabelRecordService> {

    @Reference(version = "${demo.service.version}")
    ViewLabelRecordService viewLabelRecordService;

    @Override
    public void initService() {
        setService(viewLabelRecordService);
    }

    public ViewLabelRecordEntity findByDto(ViewLabelRecordDto dto){
        ViewLabelRecordEntity res=null;
        try {
            List<ViewLabelRecordEntity> list = viewLabelRecordService.getListByDto(dto);
            if(list.size()==1){
                res = list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return res;
        }
    }
}
