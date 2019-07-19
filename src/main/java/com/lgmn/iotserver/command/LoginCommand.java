package com.lgmn.iotserver.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Component
public class LoginCommand {
    @Autowired
    UmaCommand command;

    public LoginCommand(){
        command = new UmaCommand();
    }

    public String setUName(String value){
        return setLoginValue(UmaCommand.LOGIN.UNAME,value);
    }

    public String setPwd(String value){
        return setLoginValue(UmaCommand.LOGIN.PWD,value);
    }

    public String setBC(String value){
        return setLoginValue(UmaCommand.LOGIN.BC,value);
    }

    public String setJT(String value){
        return setLoginValue(UmaCommand.LOGIN.JT,value);
    }

    public String getUName(){
        return getLoginValue(UmaCommand.LOGIN.UNAME);
    }

    public String getPwd(){
        return getLoginValue(UmaCommand.LOGIN.PWD);
    }

    public String getBC(){
        return getLoginValue(UmaCommand.LOGIN.BC);
    }

    public String getJT(){
        return getLoginValue(UmaCommand.LOGIN.JT);
    }

    public String confirmBtn(){
        return  command.loginConfirmBtn();
    }

    public String cancelBtn(){
        return command.loginCancelBtn();
    }

    public String setLoginValue(UmaCommand.LOGIN login, String value){
        return UmaCommand.FUNCTION.SET_VALUE.getValue() + " " + UmaCommand.SCREEN.LOGIN.getCommand() + " " + login.getCommand() + value + " " + command.ending;
    }

    public String getLoginValue(UmaCommand.LOGIN login){
        return UmaCommand.FUNCTION.GET_OR_CLICK.getValue() + " " + UmaCommand.SCREEN.LOGIN.getCommand() + " " + login.getCommand() + " "  + command.ending;
    }

    public String clean(UmaCommand.LOGIN control){
        return UmaCommand.FUNCTION.CLEAN_INPUT.getValue() + " " + UmaCommand.SCREEN.LOGIN.getCommand() + " " + control.getCommand() + " " + command.ending;
    }

    public List<String> cleanAll(){
        List<String> commands = new ArrayList<>();
        for(UmaCommand.LOGIN e : EnumSet.allOf(UmaCommand.LOGIN.class)){
            commands.add(clean(e));
        }
        return commands;
    }

    public List<String> getAllValue(){
        List<String> commands = new ArrayList<>();
        for(UmaCommand.LOGIN e : EnumSet.allOf(UmaCommand.LOGIN.class)){
            commands.add( getLoginValue(e));
        }
        return commands;
    }
}