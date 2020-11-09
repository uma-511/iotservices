package com.lgmn.iotserver.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StringDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(StringDecoder.class);
    final int length = 2048;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }
}