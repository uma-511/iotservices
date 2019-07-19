package com.lgmn.iotserver.model;

import com.lgmn.iotserver.controller.IotController;
import io.netty.channel.socket.SocketChannel;
import lombok.Data;

@Data
public class ConnectHolderEntity {

    private SocketChannel socketChannel;

    private IotController controller;

    private String event;

    private LoginEntity loginEntity;

    private ControlEntity controlEntity;
}