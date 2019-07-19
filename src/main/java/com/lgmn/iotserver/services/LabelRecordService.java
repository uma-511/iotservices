package com.lgmn.iotserver.services;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lgmn.common.service.LgmnAbstractApiService;
import com.lgmn.iotserver.server.LoginMap;
import com.lgmn.umaservices.basic.dto.LabelRecordDto;
import com.lgmn.umaservices.basic.entity.LabelRecordEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

@Component
public class LabelRecordService extends LgmnAbstractApiService<LabelRecordEntity, LabelRecordDto, Integer, com.lgmn.umaservices.basic.service.LabelRecordService> {

    @Reference(version = "${demo.service.version}")
    com.lgmn.umaservices.basic.service.LabelRecordService labelRecordService;

    @Override
    public void initService() {
        setService(labelRecordService);
    }

    public LabelRecordEntity createLabelRecord(int packId,int orderId,int prodId,int modelId,String handler,BigDecimal weight,int quantity,String ip){
        LabelRecordEntity entity = new LabelRecordEntity();
        //条码：类型（一位）+年月（4位）+ 6位跳码 + 机台号（两位）
        String type="1";
        SimpleDateFormat myFmt=new SimpleDateFormat("yyMM");
        String ym=myFmt.format(System.currentTimeMillis());
        String random=randomCode();
        String jt="000" + LoginMap.get(ip).getJitai().replaceAll("班次","");
        jt=jt.substring(jt.length()-3);
        String jump=type+ym+random+jt;

        entity.setLabelNum(jump);
        entity.setPackId(packId);
        entity.setOrderId(orderId);
        entity.setProdId(prodId);
        entity.setModelId(modelId);
        entity.setProdUser(handler);
        entity.setProdTime(new Timestamp(System.currentTimeMillis()));
        entity.setNetWeight(weight);
        entity.setStatus(0);
        entity.setQuantity(quantity);
        return labelRecordService.saveEntity(entity);
    }

    public void cancelLabelRecord(int labelId,int packId,String handlerId){
        int status=8;
        LabelRecordDto dto = new LabelRecordDto();
        dto.setId(labelId);
        dto.setPackId(packId);
        try {
            List<LabelRecordEntity> list = labelRecordService.getListByDto(dto);
            if(list.size()==1){
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