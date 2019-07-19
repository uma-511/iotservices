package com.lgmn.iotserver.services;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lgmn.common.service.LgmnAbstractApiService;
import com.lgmn.umaservices.basic.dto.LabelFormatDto;
import com.lgmn.umaservices.basic.dto.LabelRecordDto;
import com.lgmn.umaservices.basic.entity.LabelFormatEntity;
import com.lgmn.umaservices.basic.entity.LabelRecordEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Component
public class LabelFormatService extends LgmnAbstractApiService<LabelFormatEntity, LabelFormatDto, Integer, com.lgmn.umaservices.basic.service.LabelFormatService> {

    @Reference(version = "${demo.service.version}")
    com.lgmn.umaservices.basic.service.LabelFormatService labelFormatService;

    @Override
    public void initService() {
        setService(labelFormatService);
    }
}