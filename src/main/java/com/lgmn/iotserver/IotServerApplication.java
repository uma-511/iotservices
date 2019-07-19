package com.lgmn.iotserver;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.lgmn.iotserver.config.ConfigBean;
import com.lgmn.iotserver.server.EqmServer;
import com.lgmn.iotserver.server.NettyServer;
import com.lgmn.iotserver.server.ServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
@NacosPropertySource(dataId = "iot-server", autoRefreshed = true)
@EnableSwagger2
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class IotServerApplication {

    @Value("${iot.dev1.port}")
    private int port;

    @Value("${iot.dev1.url}")
    private String url;

//    @Autowired
//    private NettyServer socketServer;

//    @Autowired
//    private NettyServer2 socketServer2;

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(IotServerApplication.class, args);

//        NettyServer nettyServer = context.getBean(NettyServer.class);
//        nettyServer.start();

        EqmServer dtuServer = context.getBean(EqmServer.class);
        dtuServer.start();

//        NettyServer2 nettyServer2 = context.getBean(NettyServer2.class);
//        nettyServer2.start();
    }

    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup());
        b.channel(NioServerSocketChannel.class);
		b.handler(new LoggingHandler(LogLevel.INFO)).childHandler(dtuServerInitializer);
        Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
        Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
        for (ChannelOption option : keySet) {
            b.option(option, tcpChannelOptions.get(option));
        }
        return b;
    }

    @Autowired
    private ConfigBean configBean;

    @Autowired
//	@Qualifier("dtuServerInitializer")
    private ServerChannelInitializer dtuServerInitializer;

    @Bean(name = "tcpChannelOptions")
    public Map<ChannelOption<?>, Object> tcpChannelOptions() {
        Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
        options.put(ChannelOption.SO_KEEPALIVE, configBean.keepalive);
        options.put(ChannelOption.SO_BACKLOG, configBean.backlog);
        return options;
    }

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(configBean.bossCount);
    }

    @SuppressWarnings("static-access")
    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(configBean.workerCount);
    }

    @Bean(name = "serverPort")
    public int port() {
        return configBean.serverPort;
    }

    @Bean(name = "serverPort1")
    public int port1() {
        return 8000;
    }
//    @Override
//    public void run(String... strings) {
//        InetSocketAddress address = new InetSocketAddress(url, port);
//        ChannelFuture future = socketServer.run(address);
//        Runtime.getRuntime().addShutdownHook(new Thread(){
//            @Override
//            public void run() {
//                socketServer.destroy();
//            }
//        });
//        future.channel().closeFuture().syncUninterruptibly();

//        InetSocketAddress address1 = new InetSocketAddress(url, 8002);
//        ChannelFuture future1 = socketServer2.run(address1);
//        Runtime.getRuntime().addShutdownHook(new Thread(){
//            @Override
//            public void run() {
//                socketServer2.destroy();
//            }
//        });
//        future1.channel().closeFuture().syncUninterruptibly();
//    }
}
