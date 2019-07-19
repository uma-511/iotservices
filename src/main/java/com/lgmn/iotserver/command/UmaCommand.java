package com.lgmn.iotserver.command;

import com.lgmn.iotserver.utils.CoderUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Component
public class UmaCommand {

    /**
     * 功能分类
     * {EE B1 00} 00 01 FF FC FF FF
     *               画面切换：EE B1 00
     * 	         设置文本内容：EE B1 10
     * 	获取文本内容/按钮点击：EE B1 11
     * 	     设置文本框前景色：EE B1 18
     * 	         设置文字颜色：EE B1 19
     */
    public enum FUNCTION {
        SWITCH_SCREEN("EE B1 00"),
        SET_VALUE("EE B1 10"),
        GET_OR_CLICK("EE B1 11"),
        BOX_COLOR("EE B1 18"),
        FONT_COLOR("EE B1 19"),
        CLEAN_INPUT("EE B1 10");

        private final String value;

        private FUNCTION(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    /**
     * 画面切换
     * EE B1 00 {00 01} FF FC FF FF
     * 	固定开头EE B1 00，固定结尾FF FC FF FF，{00 01}代表画面ID
     * 	登录界面：00 00
     * 	操作界面：00 01
     * 	打印中：  00 02
     * 	重新登录：00 03
     */
    public enum SCREEN {
        LOGIN("00 00","LoginEntity"),
        CONTROLLER("00 01","ControlEntity"),
        PRINT("00 02",""),
        RELOGIN("00 03","");

        private final String command;
        private final String modelName;

        SCREEN(String command,String modelName) {
            this.command = command;
            this.modelName=modelName;
        }
        public String getCommand() {
            return this.command;
        }
        public String getModelName(){
            return this.modelName;
        }
    }

    private final Map<String,String> screenKV = new HashMap<>();

     {
        for(SCREEN e : EnumSet.allOf(SCREEN.class)){
            if(StringUtils.isNotEmpty(e.command) && StringUtils.isNotEmpty(e.modelName)) {
                screenKV.put(e.command, e.modelName);
                screenKV.put(e.modelName, e.command);
            }
        }
    }

    public String findScreen(String des){
        String value = screenKV.get(des);
        return value;
    }

    public enum LOGIN {
        //用户名
        UNAME("00 01","Username"),
        //密码
        PWD("00 02","Password"),
        //班次
        BC("00 03","Banci"),
        //机台
        JT("00 04","Jitai"),
        //确认
        SUBMIT("00 05","event_login_confirm"),
        //取消
        CANCEL("00 06","event_login_cancel");

        private final String command;

        private final String fieldName;

        LOGIN(String command,String fieldName) {
            this.command = command;
            this.fieldName=fieldName;
        }
        public String getCommand() {
            return this.command;
        }

        public String getFieldName(){
            return this.fieldName;
        }
    }

    private   final Map<String,String> loginKV = new HashMap<>();

    {
        for(LOGIN e : EnumSet.allOf(LOGIN.class)){
            if(StringUtils.isNotEmpty(e.command) && StringUtils.isNotEmpty(e.fieldName)) {
                loginKV.put(e.command, e.fieldName);
                loginKV.put(e.fieldName, e.command);
            }
        }
    }

    public String findLogin(String des){
        String value = loginKV.get(des);
        return value;
    }

    public enum CONTROLLER {
        // 登录信息
        LOGIN_INFO("00 01","UserInfo"),
        // 日期
//        DATE("00 02","Date"),
        // 单号
        ORDER_NO("00 03","OrderNo"),
        // 编号
//        NUMBER("00 04","Number"),
        // 型号
        MODELNAME("00 06","ModelName"),
        WIDTH("00 05","Width"),
        // 规格
        SPECS("00 0b","Specs"),
        // 纹路
//        LINES("00 06","Lines"),
        // 件数
        QUANTITY("00 07","Quantity"),
        // 楼层
        FLOOR("00 08","Floor"),
        // 材质
//        MATERIAL_QUALITY("00 09","MeterialQuality"),
        // 色号
        COLOR_NUM("00 0a","ColorNum"),
        // 米数
//        METRE("00 0b","Metre"),
        // 重量
//        WEIGHT("00 0c","Weight"),
        // 数量累计
        QUAN_TOTAL("00 0d","QuanTotal"),
        // 重量累计
        WEIGHT_TOTAL("00 0e","WeightTotal"),
        // 包数
        PACK_NUM("00 0f","PackNum"),
        // 登录/注销
        LOGIN_OUT("00 10","event_control_loginout"),
        // 打印
        PRINT("00 11","event_control_print"),
        // 作废
        CANCEL("00 12","event_control_cancel"),
        // 补打
        REPRINT("00 13","event_control_reprint"),
        //消息提示
        TIPMSG("00 14","");
        private final String command;

        private final String fieldName;

        CONTROLLER(String command,String fieldName) {
            this.command = command;
            this.fieldName=fieldName;
        }
        public String getCommand() {
            return this.command;
        }

        public String getFieldName(){
            return this.fieldName;
        }
    }

    private final Map<String,String> controlKV = new HashMap<>();

    {
        for(CONTROLLER e : EnumSet.allOf(CONTROLLER.class)){
            if(StringUtils.isNotEmpty(e.command) && StringUtils.isNotEmpty(e.fieldName)) {
                controlKV.put(e.command, e.fieldName);
                controlKV.put(e.fieldName, e.command);
            }
        }
    }

    public String findControl(String des){
        String value = controlKV.get(des);
        return value;
    }

    public final String ending="FF FC FF FF";


    public String switchScan(SCREEN screen){
        return  FUNCTION.SWITCH_SCREEN.getValue() + " " + screen.getCommand()  + " " + ending;
    }

    public String switchScan(int screenNo){
        String numStr = getNumStr(screenNo);
        return  FUNCTION.SWITCH_SCREEN.getValue() + " " + numStr + " " + ending;
    }

    public String setValue(int screenNo,int control,String value){
        String screenNoStr = getNumStr(screenNo);
        String controlStr = getNumStr(control);
        return FUNCTION.SET_VALUE.getValue() + " " + screenNoStr + " " + controlStr + " " + CoderUtils.stringToHexStr(value) + ending;
    }


    public String setFontColor(int screenNo,int control,String value){
        String screenNoStr = getNumStr(screenNo);
        String controlStr = getNumStr(control);
        return FUNCTION.FONT_COLOR.getValue() + " " + screenNoStr + " " + controlStr + " " + CoderUtils.cvtStr2Hex(value) + ending;
    }

    public String setForeColor(int screenNo,int control,String rgb565){
        String screenNoStr = getNumStr(screenNo);
        String controlStr = getNumStr(control);
        return FUNCTION.BOX_COLOR.getValue() + " " + screenNoStr + " " + controlStr + " " + rgb565 + " " + ending;
    }

    public String setForeColor(SCREEN screen,CONTROLLER control,String rgb565){
        return FUNCTION.BOX_COLOR.getValue() + " " + screen.getCommand() + " " + control.getCommand() + " " + rgb565 + " " + ending;
    }

    public String getValue(int screenNum, int control) {
        String screenNoStr = getNumStr(screenNum);
        String controlStr = getNumStr(control);
        return FUNCTION.GET_OR_CLICK.getValue() + " " + screenNoStr + " " + controlStr + " " + ending;
    }

    public String loginConfirmBtn() {
        return FUNCTION.GET_OR_CLICK.getValue() + " " + SCREEN.LOGIN.getCommand() + " " + LOGIN.SUBMIT + " 10 02 31 " + ending;
    }

    public String loginCancelBtn() {
        return FUNCTION.GET_OR_CLICK.getValue() + " " + SCREEN.LOGIN.getCommand() + " " + LOGIN.CANCEL + " 10 02 31 " + ending;
    }

    public String controlPrintBtn() {
        return FUNCTION.GET_OR_CLICK.getValue() + " " + SCREEN.CONTROLLER.getCommand() + " " + CONTROLLER.PRINT + " 10 02 31 " + ending;
    }

    public String controlLoginOutBtn() {
        return FUNCTION.GET_OR_CLICK.getValue() + " " + SCREEN.CONTROLLER.getCommand() + " " + CONTROLLER.LOGIN_OUT + " 10 02 31 " + ending;
    }

    public String controlCancelBtn() {
        return FUNCTION.GET_OR_CLICK.getValue() + " " + SCREEN.CONTROLLER.getCommand() + " " + CONTROLLER.CANCEL + " 10 02 31 " + ending;
    }

    public String controlReprintBtn() {
        return FUNCTION.GET_OR_CLICK.getValue() + " " + SCREEN.CONTROLLER.getCommand() + " " + CONTROLLER.REPRINT + " 10 02 31 " + ending;
    }





    private String getNumStr(int screenNo) {
        int length = screenNo>=0 && screenNo<10?1:screenNo>9 && screenNo<100?2:3;
        String numStr="";

        switch (length){
            case 1:
                numStr="00 0"+screenNo;
                break;
            case 2:
                numStr="00 "+Integer.toString(screenNo);
                break;
            case 3:
            default:
                numStr="00 "+Integer.toString(screenNo).substring(length-2,length);
                break;
        }
        return numStr;
    }

//    public String
}