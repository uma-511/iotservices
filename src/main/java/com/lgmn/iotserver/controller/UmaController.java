package com.lgmn.iotserver.controller;

import com.lgmn.iotserver.command.UmaCommand;
import com.lgmn.iotserver.server.ChannelMap;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/terminal")
public class UmaController {

    @Autowired
    UmaCommand command;

    String ip="192.168.192.";

    @RequestMapping("/switch/{terminalNo}/{screenNum}")
    public void switchscreen(@PathVariable("terminalNo") int terminalNo,@PathVariable("screenNum") int screenNum){
        if(ChannelMap.get(ip+terminalNo)!=null){
            SocketChannel channel=(SocketChannel)ChannelMap.get(ip+terminalNo);
            channel.writeAndFlush(command.switchScan(screenNum));
        }
    }

    @RequestMapping("setvalue/{terminalNo}/{screenNum}/{control}/{value}")
    public void setValue(@PathVariable("terminalNo") int terminalNo,@PathVariable("screenNum") int screenNum,@PathVariable("control") int control,@PathVariable("value") String value){
        if(ChannelMap.get(ip+terminalNo)!=null){
            SocketChannel channel=(SocketChannel)ChannelMap.get(ip+terminalNo);
            channel.writeAndFlush(command.setValue(screenNum, control, value));
        }
    }

    @RequestMapping("getvalue/{terminalNo}/{screenNum}/{control}")
    public void getValue(@PathVariable("terminalNo") int terminalNo,@PathVariable("screenNum") int screenNum,@PathVariable("control") int control){
        if(ChannelMap.get(ip+terminalNo)!=null){
            SocketChannel channel=(SocketChannel)ChannelMap.get(ip+terminalNo);
            channel.writeAndFlush(command.getValue(screenNum, control));
        }
    }

    @RequestMapping("setforecolor/{terminalNo}/{screenNum}/{control}/{value}")
    public void setForeColor(@PathVariable("terminalNo") int terminalNo,@PathVariable("screenNum") int screenNum,@PathVariable("control") int control,@PathVariable("value") String value){
        if(ChannelMap.get(ip+terminalNo)!=null){
            SocketChannel channel=(SocketChannel)ChannelMap.get(ip+terminalNo);
            channel.writeAndFlush(command.setForeColor(screenNum, control, value));
        }
    }

    @RequestMapping("setfontcolor/{terminalNo}/{screenNum}/{control}/{value}")
    public void setFontColor(@PathVariable("terminalNo") int terminalNo,@PathVariable("screenNum") int screenNum,@PathVariable("control") int control,@PathVariable("value") String value){
        if(ChannelMap.get(ip+terminalNo)!=null){
            SocketChannel channel=(SocketChannel)ChannelMap.get(ip+terminalNo);
            channel.writeAndFlush(command.setFontColor(screenNum, control, value));
        }
    }
}