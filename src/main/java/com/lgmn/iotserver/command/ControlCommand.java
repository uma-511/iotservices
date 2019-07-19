package com.lgmn.iotserver.command;

import com.lgmn.iotserver.utils.CoderUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Component
public class ControlCommand {
    @Autowired
    UmaCommand command;

    public ControlCommand(){
        command=new UmaCommand();
    }

    public  String setLoginInfo(String value){
        return setControllerValue(UmaCommand.CONTROLLER.LOGIN_INFO,value);
    }

//    public  String setDate(String value){
//        return setControllerValue(UmaCommand.CONTROLLER.DATE,value);
//    }

    public  String setOrderNo(String value){
        return setControllerValue(UmaCommand.CONTROLLER.ORDER_NO,value);
    }

    public  String setModelName(String value){
        return setControllerValue(UmaCommand.CONTROLLER.MODELNAME,value);
    }

    public String setWidth(String value){
        return setControllerValue(UmaCommand.CONTROLLER.WIDTH,value);
    }

//    public  String setNumber(String value){
//        return setControllerValue(UmaCommand.CONTROLLER.NUMBER,value);
//    }

    public  String setSpec(String value){
        return setControllerValue(UmaCommand.CONTROLLER.SPECS,value);
    }

//    public  String setLine(String value){
//        return setControllerValue(UmaCommand.CONTROLLER.LINES,value);
//    }

    public  String setQuantity(String value){
        return setControllerValue(UmaCommand.CONTROLLER.QUANTITY,value);
    }

    public  String setFloor(String value){
        return setControllerValue(UmaCommand.CONTROLLER.FLOOR,value);
    }

//    public  String setMaterialQuality(String value){
//        return setControllerValue(UmaCommand.CONTROLLER.MATERIAL_QUALITY,value);
//    }

    public  String setColorNum(String value){
        return setControllerValue(UmaCommand.CONTROLLER.COLOR_NUM,value);
    }

//    public  String setMetre(String value){
//        return setControllerValue(UmaCommand.CONTROLLER.METRE,value);
//    }

//    public  String setWeight(String value){
//        return setControllerValue(UmaCommand.CONTROLLER.WEIGHT,value);
//    }

    public  String setQuanTotal(String value){
        return setControllerValue(UmaCommand.CONTROLLER.QUAN_TOTAL,value);
    }

    public  String setWeightTotal(String value){
        return setControllerValue(UmaCommand.CONTROLLER.WEIGHT_TOTAL,value);
    }

    public  String setPackNum(String value){
        return setControllerValue(UmaCommand.CONTROLLER.PACK_NUM,value);
    }

    public String setTipMsg(String value){
        return setControllerValue(UmaCommand.CONTROLLER.TIPMSG,value);
    }

    public  String getLoginInfo(){
        return getControllerValue(UmaCommand.CONTROLLER.LOGIN_INFO);
    }

//    public  String getDate(String value){
//        return getControllerValue(UmaCommand.CONTROLLER.DATE);
//    }

    public  String getOrderNo(String value){
        return getControllerValue(UmaCommand.CONTROLLER.ORDER_NO);
    }

//    public  String getNumber(String value){
//        return getControllerValue(UmaCommand.CONTROLLER.NUMBER);
//    }

    public  String getSpec(String value){
        return getControllerValue(UmaCommand.CONTROLLER.SPECS);
    }

//    public  String getLine(String value){
//        return getControllerValue(UmaCommand.CONTROLLER.LINES);
//    }

    public  String getQuantity(String value){
        return getControllerValue(UmaCommand.CONTROLLER.QUANTITY);
    }

    public  String getFloor(String value){
        return getControllerValue(UmaCommand.CONTROLLER.FLOOR);
    }

//    public  String getMaterialQuality(String value){
//        return getControllerValue(UmaCommand.CONTROLLER.MATERIAL_QUALITY);
//    }

    public  String getColorNum(String value){
        return getControllerValue(UmaCommand.CONTROLLER.COLOR_NUM);
    }

//    public  String getMetre(String value){
//        return getControllerValue(UmaCommand.CONTROLLER.METRE);
//    }
//
//    public  String getWeight(String value){
//        return getControllerValue(UmaCommand.CONTROLLER.WEIGHT);
//    }

    public  String getQuanTotal(String value){
        return getControllerValue(UmaCommand.CONTROLLER.QUAN_TOTAL);
    }

    public  String getWeightTotal(String value){
        return getControllerValue(UmaCommand.CONTROLLER.WEIGHT_TOTAL);
    }

    public  String getPackNum(String value){
        return getControllerValue(UmaCommand.CONTROLLER.PACK_NUM);
    }

    public String printBtn(){
        return command.controlPrintBtn();
    }

    public String loginOutBtn(){
        return command.controlLoginOutBtn();
    }

    public String cancelBtn(){
        return command.controlCancelBtn();
    }

    public String reprintBtn(){
        return command.controlReprintBtn();
    }

    public String setControllerValue(UmaCommand.CONTROLLER control, String value){
        return UmaCommand.FUNCTION.SET_VALUE.getValue() + " " + UmaCommand.SCREEN.CONTROLLER.getCommand() + " " + control.getCommand() + " "+ CoderUtils.stringToHexStr(value) + command.ending;
    }

    public String getControllerValue(UmaCommand.CONTROLLER control){
        return UmaCommand.FUNCTION.GET_OR_CLICK.getValue() + " " + UmaCommand.SCREEN.CONTROLLER.getCommand() + " " + control.getCommand() + " "+ command.ending;
    }

    public String clean(UmaCommand.CONTROLLER control){
        return UmaCommand.FUNCTION.CLEAN_INPUT.getValue() + " " + UmaCommand.SCREEN.CONTROLLER.getCommand() + " " + control.getCommand() + " " + command.ending;
    }

    public List<String> cleanAll(){
        List<String> commands = new ArrayList<>();
        for(UmaCommand.CONTROLLER e : EnumSet.allOf(UmaCommand.CONTROLLER.class)){
                commands.add(clean(e));
        }
        return commands;
    }

    public List<String> cleanOrderInfo(){
        List<String> commands = new ArrayList<>();
        for(UmaCommand.CONTROLLER e : EnumSet.allOf(UmaCommand.CONTROLLER.class)){
            if(!(e.getFieldName().equals("UserInfo") || e.getFieldName().equals("Date"))) {
                commands.add(clean(e));
            }
        }
        return commands;
    }

    public List<String> getAllValue(){
        List<String> commands = new ArrayList<>();
        for(UmaCommand.CONTROLLER e : EnumSet.allOf(UmaCommand.CONTROLLER.class)){
            if(!(e.getFieldName().equals("UserInfo") || e.getFieldName().equals("Date") || StringUtils.startsWith(e.getFieldName(),"event_"))) {
            commands.add(getControllerValue(e));
            }
        }
        return commands;
    }

    public String setPackNumColor(String rgb565){
        return command.setForeColor(UmaCommand.SCREEN.CONTROLLER,UmaCommand.CONTROLLER.PACK_NUM,rgb565);
    }
}