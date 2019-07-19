package com.lgmn.iotserver.model;

import com.lgmn.umaservices.basic.entity.ViewLabelRecordEntity;
import com.lgmn.umaservices.basic.entity.ViewOrderInfoEntity;
import com.lgmn.userservices.basic.entity.LgmnUserEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginEntity implements Serializable {

    private boolean finish;

    // 用户名
    private String username;
    // 密码
    private String password;
    // 班次
    private String banci;

    // 机台
    private String jitai;

    private LgmnUserEntity lgmnUserEntity;

    private ViewLabelRecordEntity labelRecordEntity;

    private ViewOrderInfoEntity viewOrderInfoEntity;

    public void setJitai(String jitai) {
        this.jitai = jitai;
        setFinish(true);
    }

}