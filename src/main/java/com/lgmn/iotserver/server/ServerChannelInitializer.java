package com.lgmn.iotserver.server;

import com.lgmn.iotserver.codec.SocketDecoder;
import com.lgmn.iotserver.codec.SocketEncoder;
import com.lgmn.iotserver.services.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Autowired
    ViewLabelRecordApiService viewLabelRecordApiService;

    @Autowired
    ViewOrderInfoApiService viewOrderInfoApiService;

    @Autowired
    UserService userService;

    @Autowired
    LabelRecordService labelRecordService;

    @Autowired
    LabelFormatService labelFormatService;

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        // 解码编码
//        socketChannel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
//        socketChannel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
        socketChannel.pipeline().addLast(new SocketDecoder());
        socketChannel.pipeline().addLast(new SocketEncoder());

        socketChannel.pipeline().addLast(new ServerHandler(viewLabelRecordApiService,viewOrderInfoApiService,userService,labelRecordService,labelFormatService));
    }
}