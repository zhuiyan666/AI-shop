package com.root.aishopback.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Component
public class NettyMonitorServer implements CommandLineRunner {

    // You can externalize this port to application.properties if needed
    private static final int PORT = 9000;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    public void run(String... args) throws Exception {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new MonitorServerInitializer());

            ChannelFuture f = b.bind(PORT).sync();
            System.out.println("Netty Monitor Server started on port " + PORT);
            
            // Do not call f.channel().closeFuture().sync() here because it will block the Spring Boot main thread.
        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
        }
    }

    @PreDestroy
    public void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        System.out.println("Netty Monitor Server stopped.");
    }
}
