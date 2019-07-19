package com.lgmn.iotserver.server;

import com.lgmn.iotserver.controller.IotController;
import com.lgmn.iotserver.services.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class.getName());

    @Autowired
    IotController iotController;

    public ServerHandler(ViewLabelRecordApiService viewLabelRecordService, ViewOrderInfoApiService viewOrderInfoApiService, UserService userService, LabelRecordService labelRecordService, LabelFormatService labelFormatService){
        iotController=new IotController(viewLabelRecordService,viewOrderInfoApiService,userService,labelRecordService,labelFormatService);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        iotController.handler(ctx,msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String clientIP =iotController.SaveChannel(ctx);
        logger.warn("channelActive被触发，已经有设备连接上采集软件:"+clientIP);
    }

    /**
     * channelInactive: 客户端断开连接后触发
     *
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientIP = iotController.SaveChannel(ctx);
    }
}