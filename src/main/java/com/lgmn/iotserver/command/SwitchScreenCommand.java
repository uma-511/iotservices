package com.lgmn.iotserver.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SwitchScreenCommand {
    @Autowired
    UmaCommand command;

    public SwitchScreenCommand(){
        command = new UmaCommand();
    }

    public String login(){
        return command.switchScan(UmaCommand.SCREEN.LOGIN);
    }

    public String control(){
        return command.switchScan(UmaCommand.SCREEN.CONTROLLER);
    }

    public String printing(){
        return command.switchScan(UmaCommand.SCREEN.PRINT);
    }

    public String relogin(){
        return command.switchScan(UmaCommand.SCREEN.RELOGIN);
    }
}