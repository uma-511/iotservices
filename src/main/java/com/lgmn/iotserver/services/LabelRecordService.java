package com.lgmn.iotserver.services;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lgmn.common.service.LgmnAbstractApiService;
import com.lgmn.iotserver.model.LoginEntity;
import com.lgmn.iotserver.server.LoginMap;
import com.lgmn.umaservices.basic.dto.LabelRecordDto;
import com.lgmn.umaservices.basic.entity.LabelRecordEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

@Component
public class LabelRecordService extends LgmnAbstractApiService<LabelRecordEntity, LabelRecordDto, Integer, com.lgmn.umaservices.basic.service.LabelRecordService> {

    @Reference(version = "${demo.service.version}", timeout = 20000)
    com.lgmn.umaservices.basic.service.LabelRecordService labelRecordService;

    @Override
    public void initService() {
        setService(labelRecordService);
    }

    public LabelRecordEntity createLabelRecord(int packId, int orderId, int prodId, int modelId, String handler, String specs, int quantity, String ip) {
        LoginEntity loginEntity = LoginMap.get(ip);
        //条码：类型（一位）+年月（4位）+ 6位跳码 + 机台号（四位）
        String type = "1";
        SimpleDateFormat myFmt = new SimpleDateFormat("yyMM");
        String ym = myFmt.format(System.currentTimeMillis());
        String random = randomCode();
        String jt = "000" + LoginMap.get(ip).getJitai().replaceAll("班次", "").replace("机", "");
        jt = jt.substring(jt.length() - 4);

        String jump = type + ym + random + jt;
        Integer count = 0;
        while (labelRecordService.countByLabelNum(jump) > 0) {
            jump = type + ym + random + jt;
        }

        LabelRecordEntity entity = new LabelRecordEntity();
        entity.setMachineNum(loginEntity.getJitai());
        entity.setBanci(loginEntity.getBanci());
        entity.setLabelNum(jump);
        entity.setPackId(packId);
        entity.setOrderId(orderId);
        entity.setProdId(prodId);
        entity.setModelId(modelId);
        entity.setProdUser(handler);
        entity.setProdTime(new Timestamp(System.currentTimeMillis()));
        entity.setSpecs(specs);
        entity.setStatus(0);
        entity.setQuantity(quantity);
        return labelRecordService.saveEntity(entity);
    }

    public void cancelLabelRecord(Integer labelId, Integer packId, String handlerId) {
        Integer status = 8;
        LabelRecordDto dto = new LabelRecordDto();
        dto.setId(labelId);
        dto.setPackId(packId);
        try {
            List<LabelRecordEntity> list = labelRecordService.getListByDto(dto);
            if (list.size() == 1) {
                LabelRecordEntity entity = list.get(0);
                entity.setStatus(status);
                entity.setInvalidUser(handlerId);
                entity.setInvalidTime(new Timestamp(System.currentTimeMillis()));
                labelRecordService.saveEntity(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String randomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}