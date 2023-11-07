package io.github.hydrocarbon;

import io.github.hydrocarbon.initializer.SocksServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.logging.Logger;

/**
 * @author zouzhenfeng
 * @since 2023-11-01
 */
public class SocksServer {
    private static final Logger log = Logger.getLogger(SocksServer.class.getName());
    static final int PORT = Integer.parseInt(System.getProperty("port", "10086"));

    public static void main(String[] args) throws Exception {
        log.info("项目将启动在端口：" + PORT);

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new SocksServerInitializer());
            bootstrap.bind(PORT).sync()
                    .channel()
                    .closeFuture()
                    .sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
