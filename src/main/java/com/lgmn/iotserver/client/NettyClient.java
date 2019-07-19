package com.lgmn.iotserver.client;

import com.alibaba.druid.sql.visitor.functions.Char;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.jboss.logging.Logger;
import org.springframework.security.crypto.codec.Hex;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Vector;

public class NettyClient {
    private static final Logger log = Logger.getLogger(NettyClient.class);

    private static Bootstrap b;
    private static ChannelFuture f;
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private static void init () {
        try {
            log.info("init...");
            b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) {
                    // 解码编码
                    socketChannel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
                    socketChannel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));

                    socketChannel.pipeline().addLast(new ClientHandler());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object startAndWrite(InetSocketAddress address, Object send) throws InterruptedException {
        init();
        f = b.connect(address).sync();
        // 传数据给服务端
        f.channel().writeAndFlush(send);
        f.channel().closeFuture().sync();
        return f.channel().attr(AttributeKey.valueOf("Attribute_key")).get();
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress( "127.0.0.1",8000);
        String message = "hello";
        try {
            Object result = NettyClient.startAndWrite(address, message);
            log.info("....result:" + result);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            f.channel().close();
            workerGroup.shutdownGracefully();
            log.info("Closed client!");
        }

//        String str="得到";
//        test333();
//        try {
//            System.out.println(bytesToHex(str.getBytes("GB2312")));
//            System.out.println(new String(hexToByteArray("b5c3b5bd"),"GB2312"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }


    public static byte[] hexToByteArray(String inHex){
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1){
            //奇数
            hexlen++;
            result = new byte[(hexlen/2)];
            inHex="0"+inHex;
        }else {
            //偶数
            result = new byte[(hexlen/2)];
        }
        int j=0;
        for (int i = 0; i < hexlen; i+=2){
            result[j]=hexToByte(inHex.substring(i,i+2));
            j++;
        }
        return result;
    }

    public static byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }

    public static void test333(){
        String a="得到";
        try {
            byte[] b=a.getBytes("GB2312");
            System.out.println(cvtStr2Hex(bytesToHexFun1(b)));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    //将byte数组转成16进制字符串
    public static String bytesToHexFun1(byte[] bytes) {
        char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for(byte b : bytes) { // 使用除与取余进行转换
            if(b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }
            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }
        return new String(buf);
    }

    public static String cvtStr2Hex(String str) {
        if (str == null)
            return "";
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < str.length() / 2; i ++) {
            String sub = str.substring(i*2, i*2 + 2);
            sb.append("0x" + sub.toUpperCase() + " ");
        }
// 转换的时候一定注意将自己的文件编码改成GBK
        return sb.toString();
    }
}