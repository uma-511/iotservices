package com.lgmn.iotserver.controller;

import com.lgmn.iotserver.command.UmaCommand;
import com.lgmn.iotserver.model.PrintPojo;
import com.lgmn.iotserver.server.ChannelMap;
import com.lgmn.umaservices.basic.entity.UmaConfigValueEntity;
import com.lgmn.utils.printer.PrintProps;
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
        Channel channel=ChannelMap.get(ip+terminalNo);
        if(channel!=null){
            SocketChannel socketChannel=(SocketChannel)channel;
            socketChannel.writeAndFlush(command.setValue(screenNum, control, value));
            socketChannel.writeAndFlush(command.getValue(screenNum, control));
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

    @Autowired
    IotController iotController;

    @RequestMapping("pritnTest")
    public void printTest(){
        String ip = "192.168.192.201";
        int port = 9100;
        String svgPath = "E:\\项目资料\\优码\\盈俊\\盈俊3.svg";
        PrintProps printProps = new PrintProps();
        printProps.setWidth(100);
        printProps.setHeight(150);

        PrintPojo printPojo = new PrintPojo();
        printPojo.setProdNum("134");
        printPojo.setLabelNum("1234567890");
        printPojo.setModelName("134");
        printPojo.setProdUser("134");;
        printPojo.setProdDate("2020-02-01");
        printPojo.setWidth("134");
        printPojo.setSpecs("134");
        printPojo.setPerPackQuantity("134");
        printPojo.setJitai("134");
        printPojo.setColor("134");
        printPojo.setPackNum("134");
        iotController.print(ip,svgPath,printPojo);
    }
}