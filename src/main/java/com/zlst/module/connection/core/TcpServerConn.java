package com.zlst.module.connection.core;

import com.zlst.module.connection.config.DevConnCofig;
import com.zlst.module.connection.core.handler.AcceptorIdleStateTrigger;
import com.zlst.module.connection.core.handler.DefaultTcpServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author 170137 2018-12-26
 */
public class TcpServerConn implements ConnInterface<byte[]> {

    private DevConnCofig config;

    public TcpServerConn(DevConnCofig config) {
        this.config = config;
    }

    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);
        try {
            ServerBootstrap sbs = new ServerBootstrap();
            sbs.group(bossGroup, workerGroup);
            sbs.channel(NioServerSocketChannel.class);
            sbs.childHandler(new ChannelInitializer<SocketChannel>() {

                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new AcceptorIdleStateTrigger());
                    ch.pipeline().addLast(new DefaultTcpServerHandler());
                };

            }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定端口，开始接收进来的连接
            int port = config.getPort();
            ChannelFuture future = sbs.bind(port).sync();
            System.out.println("Server start listen at " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        DevConnCofig cfg = new DevConnCofig();
        cfg.setPort(8999);
        TcpServerConn svr = new TcpServerConn(cfg);
        svr.start();
    }

    @Override
    public boolean send(byte[] bytes) {
        return false;
    }

    @Override
    public byte[] sendSync(byte[] bytes) {
        return new byte[0];
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getUniqueCode() {
        return null;
    }

    @Override
    public ConnInterface open() {
        return null;
    }

    @Override
    public boolean close() {
        return false;
    }
}
